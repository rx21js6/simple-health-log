package jp.nauplius.app.shl.page.record.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.db.model.PhysicalCondition;
import jp.nauplius.app.shl.common.service.AbstractService;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.bean.MonthlyRecordModel;
import jp.nauplius.app.shl.page.record.bean.RecordHolder;

@Named
@SessionScoped
public class MonthlyRecordService extends AbstractService {
    @Inject
    private Logger logger;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private MonthlyRecordModel monthlyRecordModel;

    public List<RecordHolder> loadMonthlyRecords() {

        // 当月の1日～月末までのリストを作成
        List<RecordHolder> monthlyRecordHolders = new ArrayList<>();

        LocalDate localDate = this.monthlyRecordModel.getToday();
        if (Objects.isNull(localDate)) {
            localDate = this.loginInfo.getUsersLocalToday();
            this.monthlyRecordModel.setToday(localDate);
        }

        LocalDate date = LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
        LocalDate firstDate = date;
        LocalDate today = this.loginInfo.getUsersLocalToday();

        while (localDate.getMonth().equals(date.getMonth())) {
            RecordHolder tmpRec = new RecordHolder();
            boolean isToday = today.equals(date) ? true : false;
            tmpRec.setToday(isToday);
            tmpRec.setDateText(date.format(ShlConstants.RECORDING_DATE_FORMATTER));
            monthlyRecordHolders.add(tmpRec);
            this.logger.debug(String.format("dateText: %s", tmpRec.getDateText()));
            date = date.plusDays(1);
        }
        LocalDate lastDate = date.plusDays(-1);

        // レコード取得
        String sql = "SELECT pc FROM PhysicalCondition pc WHERE pc.id.id = :loginUserId AND pc.id.recordingDate BETWEEN :firstDate AND :lastDate ORDER BY pc.id.recordingDate";

        TypedQuery<PhysicalCondition> query = this.entityManager.createQuery(sql, PhysicalCondition.class);
        query.setParameter("loginUserId", this.loginInfo.getUserInfo().getId());
        query.setParameter("firstDate", firstDate.format(ShlConstants.RECORDING_DATE_FORMATTER));
        query.setParameter("lastDate", lastDate.format(ShlConstants.RECORDING_DATE_FORMATTER));
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

        this.monthlyRecordModel.setMonthlyRecords(monthlyRecordHolders);

        return monthlyRecordHolders;
    }
}
