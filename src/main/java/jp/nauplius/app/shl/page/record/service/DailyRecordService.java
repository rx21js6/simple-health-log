package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.PhysicalConditionPK;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.bean.RecordHolder;

@Named
@SessionScoped
public class DailyRecordService implements Serializable {
    private static final DateTimeFormatter RECORDING_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Inject
    private transient EntityManager em;

    @Inject
    private LoginInfo loginInfo;

    @PostConstruct
    public void init() {
        System.out.println("DailyRecordService#init() em: " + this.em);
    }

    /**
     * レコード取得
     * @param recordingDate
     * @return
     */
    public PhysicalCondition getRecord(LocalDate recordingDate) {
        String dateText = recordingDate.format(RECORDING_DATE_FORMATTER);

        PhysicalConditionPK pk = new PhysicalConditionPK();
        pk.setId(this.loginInfo.getUserInfo().getId());
        pk.setRecordingDate(dateText);
        PhysicalCondition record = this.em.find(PhysicalCondition.class, pk);

        if (Objects.isNull(record)) {
            record = new PhysicalCondition();
            record.setId(pk);
        }

        return record;
    }

    public List<RecordHolder> getMontylyRecords(LocalDate localDate) {

        // 当月の1日～月末までのリストを作成
        List<RecordHolder> monthlyRecordHolders = new ArrayList<>();

        LocalDate date = LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
        LocalDate firstDate = date;

        while (localDate.getMonth().equals(date.getMonth())) {
            RecordHolder tmpRec = new RecordHolder();
            tmpRec.setDateText(date.format(RECORDING_DATE_FORMATTER));
            monthlyRecordHolders.add(tmpRec);
            System.out.println(tmpRec);
            date = date.plusDays(1);
        }
        LocalDate lastDate = date.plusDays(-1);

        // レコード取得
        String sql = "SELECT pc FROM PhysicalCondition pc WHERE pc.id.id = :loginUserId AND pc.id.recordingDate BETWEEN :firstDate AND :lastDate ORDER BY pc.id.recordingDate";

        TypedQuery<PhysicalCondition> query = this.em.createQuery(sql, PhysicalCondition.class);
        query.setParameter("loginUserId", this.loginInfo.getUserInfo().getId());
        query.setParameter("firstDate", firstDate.format(RECORDING_DATE_FORMATTER));
        query.setParameter("lastDate", lastDate.format(RECORDING_DATE_FORMATTER));
        List<PhysicalCondition> results = query.getResultList();

        // マッチしたら設定
        for (RecordHolder holder : monthlyRecordHolders) {
            for (PhysicalCondition tmpCondition : results) {
                if (tmpCondition.getId().getRecordingDate().equals(holder.getDateText())) {
                    holder.setPhysicalCondition(tmpCondition);
                    break;
                }
            }
        }

        return monthlyRecordHolders;
    }

    /**
     * 登録
     *
     * @param physicalCondition
     */
    @Transactional
    public void register(PhysicalCondition physicalCondition) {
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
    }
}
