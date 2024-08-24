package jp.nauplius.app.shl.page.record.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.db.model.PhysicalCondition;
import jp.nauplius.app.shl.common.db.model.PhysicalConditionPK;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.common.ui.bean.KeyIvHolder;
import jp.nauplius.app.shl.common.util.CipherUtil;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.bean.DailyRecord;
import jp.nauplius.app.shl.page.record.bean.DailyRecordForm;
import jp.nauplius.app.shl.page.record.bean.DailyRecordInputModel;
import jp.nauplius.app.shl.page.record.bean.DailyRecordListModel;

@Named
@SessionScoped
public class DailyRecordService extends AbstractService {
    @Inject
    private Logger logger;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private DailyRecordInputModel dailyRecordInputModel;

    @Inject
    private DailyRecordListModel dailyRecordListModel;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private KeyIvHolder keyIvHolder;

    /**
     * 指定日と前日のレコードを取得
     *
     * @param recordingDate
     * @return
     */
    public void loadRecord(LocalDate recordingDate) {
        this.dailyRecordInputModel.setPhysicalCondition(this.loadSelectedDateRecord(recordingDate));
        LocalDate previousDate = recordingDate.minusDays(1);

        this.dailyRecordInputModel
                .setPreviousDailyRecordForm(DailyRecordForm.valueOf(this.loadSelectedDateRecord(previousDate)));
    }

    /**
     * 登録
     *
     */
    @Transactional
    public void register() {
        this.logger.debug(String.format("register entityManager: %s", this.entityManager));

        DailyRecordForm dailyRecordform = this.dailyRecordInputModel.getDailyRecordForm();
        dailyRecordform.getId().setId(loginInfo.getUserInfo().getId());
        PhysicalCondition conditionForUpdate = this.entityManager.find(PhysicalCondition.class,
                dailyRecordform.getId());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(conditionForUpdate)) {
            // 新規
            PhysicalCondition physicalCondition = new PhysicalCondition();
            try {
                BeanUtils.copyProperties(physicalCondition, dailyRecordform);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }

            this.entityManager.persist(physicalCondition);
            physicalCondition.setUserInfo(this.loginInfo.getUserInfo());
            physicalCondition.setCreatedBy(this.loginInfo.getUserInfo().getId());
            physicalCondition.setCreatedDate(Timestamp.valueOf(now));
            physicalCondition.setModifiedBy(this.loginInfo.getUserInfo().getId());
            physicalCondition.setModifiedDate(Timestamp.valueOf(now));
            this.entityManager.merge(physicalCondition);
        } else {
            // 更新
            try {
                BeanUtils.copyProperties(conditionForUpdate, dailyRecordform);
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
                this.dailyRecordInputModel.getDailyRecordForm().getId().getRecordingDate(),
                ShlConstants.RECORDING_DATE_FORMATTER);

        this.loadRecord(recordingDate);
    }

    /**
     * 有効な利用者の指定日データ取得
     *
     * @param date
     *            指定日
     * @return
     */
    public List<DailyRecord> loadDailyRecords(LocalDate date) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT NEW jp.nauplius.app.shl.page.record.bean.DailyRecord(u, p, u.name) ");
        queryBuilder.append("FROM UserInfo u ");
        queryBuilder.append("LEFT JOIN PhysicalCondition p ON ");
        queryBuilder.append("u.id = p.id.id ");
        queryBuilder.append("AND p.id.recordingDate = :date ");
        queryBuilder.append("WHERE u.deleted = false ");
        queryBuilder.append("ORDER BY u.id");
        TypedQuery<DailyRecord> query = this.entityManager.createQuery(queryBuilder.toString(), DailyRecord.class);
        query.setParameter("date", date.format(ShlConstants.RECORDING_DATE_FORMATTER));
        List<DailyRecord> results = query.getResultList();

        for (DailyRecord record : results) {
            UserInfo userInfo = record.getUserInfo();
            String name = userInfo.getName();
            if (userInfo.getSecurityLevel() == SecurityLevel.LEVEL1.getInt()) {
                name = this.cipherUtil.decrypt(userInfo, userInfo.getEncryptedName(), this.keyIvHolder.getKeyBytes(),
                        this.keyIvHolder.getIvBytes(), this.keyIvHolder.getSalt());
            }
            record.setName(name);
        }

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
