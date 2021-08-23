package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.PhysicalConditionPK;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.backing.DailyRecordInputModel;
import jp.nauplius.app.shl.page.record.bean.DailyRecord;

@Named
@SessionScoped
public class DailyRecordService implements Serializable {
    @Inject
    private transient EntityManager em;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private DailyRecordInputModel dailyRecordInputModel;

    @PostConstruct
    public void init() {
        System.out.println("DailyRecordService#init() em: " + this.em);
    }

    /**
     * レコード取得
     *
     * @param recordingDate
     * @return
     */
    @Transactional
    public PhysicalCondition loadRecord(LocalDate recordingDate) {
        this.em.flush();

        String dateText = recordingDate.format(ShlConstants.RECORDING_DATE_FORMATTER);

        PhysicalConditionPK pk = new PhysicalConditionPK();
        pk.setId(this.loginInfo.getUserInfo().getId());
        pk.setRecordingDate(dateText);
        PhysicalCondition record = this.em.find(PhysicalCondition.class, pk);

        if (Objects.isNull(record)) {
            record = new PhysicalCondition();
            pk.setId(null);
            pk.setRecordingDate(dateText);
            record.setUserInfo(this.loginInfo.getUserInfo());
            record.setId(pk);
        }

        this.dailyRecordInputModel.setPhysicalCondition(record);

        return record;
    }

    /**
     * 登録
     *
     */
    @Transactional
    public void register() {
        PhysicalCondition physicalCondition = this.dailyRecordInputModel.getPhysicalCondition();
        PhysicalCondition tmpCondition = this.em.find(PhysicalCondition.class, physicalCondition.getId());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(tmpCondition)) {
            // 新規
            this.em.persist(physicalCondition);
            physicalCondition.setModifiedBy(physicalCondition.getId().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            physicalCondition.setModifiedBy(physicalCondition.getId().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            this.em.merge(physicalCondition);
        } else {
            // 更新
            try {
                BeanUtils.copyProperties(tmpCondition, physicalCondition);
                tmpCondition.setModifiedBy(physicalCondition.getId().getId());
                tmpCondition.setModifiedDate(Timestamp.valueOf(now));
                this.em.merge(tmpCondition);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }
        }
        this.em.flush();

        // ミラー更新するため読み直し
        LocalDate recordingDate = LocalDate.parse(
                this.dailyRecordInputModel.getPhysicalCondition().getId().getRecordingDate(),
                ShlConstants.RECORDING_DATE_FORMATTER);

        this.loadRecord(recordingDate);
    }

    /**
     * 有効な利用者の指定日データ取得
     *
     * @param date 指定日
     * @return
     */
    public List<DailyRecord> getDailyRecords(LocalDate date) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW jp.nauplius.app.shl.page.record.bean.DailyRecord(u, p) ");
        queryBuilder.append("FROM UserInfo u ");
        queryBuilder.append("LEFT JOIN PhysicalCondition p ON ");
        queryBuilder.append("u.id = p.id.id ");
        queryBuilder.append("AND p.id.recordingDate = :date ");
        queryBuilder.append("WHERE u.deleted = false ");
        queryBuilder.append("ORDER BY u.id");
        TypedQuery<DailyRecord> query = this.em.createQuery(queryBuilder.toString(), DailyRecord.class);
        query.setParameter("date", date.format(ShlConstants.RECORDING_DATE_FORMATTER));
        List<DailyRecord> results = query.getResultList();
        return results;
    }
}
