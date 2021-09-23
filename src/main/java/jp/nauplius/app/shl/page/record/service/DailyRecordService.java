package jp.nauplius.app.shl.page.record.service;

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
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.PhysicalConditionPK;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.backing.DailyRecordInputModel;
import jp.nauplius.app.shl.page.record.backing.DailyRecordListModel;
import jp.nauplius.app.shl.page.record.bean.DailyRecord;

@Named
@SessionScoped
public class DailyRecordService extends AbstractService {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private DailyRecordInputModel dailyRecordInputModel;

    @Inject
    private DailyRecordListModel dailyRecordListModel;

    @PostConstruct
    public void init() {
        // System.out.println("DailyRecordService#init() entityManager: " +
        // this.entityManager);
    }

    /**
     * 指定日と前日のレコードを取得
     *
     * @param recordingDate
     * @return
     */
    @Transactional
    public void loadRecord(LocalDate recordingDate) {
        this.dailyRecordInputModel.setPhysicalCondition(this.loadSelectedDateRecord(recordingDate));
        LocalDate previousDate = recordingDate.minusDays(1);
        this.dailyRecordInputModel.setPreviousPhysicalCondition(this.loadSelectedDateRecord(previousDate));
    }

    /**
     * 登録
     *
     */
    @Transactional
    public void register() {
        PhysicalCondition physicalCondition = this.dailyRecordInputModel.getPhysicalCondition();
        PhysicalCondition conditionForUpdate = this.entityManager.find(PhysicalCondition.class,
                physicalCondition.getId());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(conditionForUpdate)) {
            // 新規
            this.entityManager.persist(physicalCondition);
            physicalCondition.setCreatedBy(this.loginInfo.getUserInfo().getId());
            physicalCondition.setCreatedDate(Timestamp.valueOf(now));
            physicalCondition.setModifiedBy(this.loginInfo.getUserInfo().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            this.entityManager.merge(physicalCondition);
        } else {
            // 更新
            try {
                BeanUtils.copyProperties(conditionForUpdate, physicalCondition);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }
            conditionForUpdate.setModifiedBy(this.loginInfo.getUserInfo().getId());
            conditionForUpdate.setModifiedDate(Timestamp.valueOf(now));
            this.entityManager.merge(conditionForUpdate);
        }
        this.entityManager.flush();

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
    public List<DailyRecord> loadDailyRecords(LocalDate date) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW jp.nauplius.app.shl.page.record.bean.DailyRecord(u, p) ");
        queryBuilder.append("FROM UserInfo u ");
        queryBuilder.append("LEFT JOIN PhysicalCondition p ON ");
        queryBuilder.append("u.id = p.id.id ");
        queryBuilder.append("AND p.id.recordingDate = :date ");
        queryBuilder.append("WHERE u.deleted = false ");
        queryBuilder.append("ORDER BY u.id");
        TypedQuery<DailyRecord> query = this.entityManager.createQuery(queryBuilder.toString(), DailyRecord.class);
        query.setParameter("date", date.format(ShlConstants.RECORDING_DATE_FORMATTER));
        List<DailyRecord> results = query.getResultList();

        this.dailyRecordListModel.setDailyRecords(results);
        return results;
    }

    /**
     * 指定日のレコードを取得
     *
     * @param recordingDate
     * @return
     */
    private PhysicalCondition loadSelectedDateRecord(LocalDate recordingDate) {
        this.entityManager.flush();

        String dateTextToday = recordingDate.format(ShlConstants.RECORDING_DATE_FORMATTER);

        PhysicalConditionPK pkToday = new PhysicalConditionPK();
        pkToday.setId(this.loginInfo.getUserInfo().getId());
        pkToday.setRecordingDate(dateTextToday);
        PhysicalCondition todayRecord = this.entityManager.find(PhysicalCondition.class, pkToday);

        if (Objects.isNull(todayRecord)) {
            todayRecord = new PhysicalCondition();
            pkToday.setId(null);
            pkToday.setRecordingDate(dateTextToday);
            todayRecord.setUserInfo(this.loginInfo.getUserInfo());
            todayRecord.setId(pkToday);
        }
        return todayRecord;
    }
}
