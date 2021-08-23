package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.backing.MonthlyRecordModel;
import jp.nauplius.app.shl.page.record.bean.RecordHolder;

@Named
@SessionScoped
public class MonthlyRecordService implements Serializable {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private transient EntityManager em;

    @Inject
    private MonthlyRecordModel monthlyRecordModel;

    public List<RecordHolder> loadMonthlyRecords() {

        // 当月の1日～月末までのリストを作成
        List<RecordHolder> monthlyRecordHolders = new ArrayList<>();

        LocalDate localDate = this.monthlyRecordModel.getToday();

        LocalDate date = LocalDate.of(localDate.getYear(), localDate.getMonth(), 1);
        LocalDate firstDate = date;
        LocalDate today = LocalDate.now();

        while (localDate.getMonth().equals(date.getMonth())) {
            RecordHolder tmpRec = new RecordHolder();
            boolean isToday = today.equals(date) ? true : false;
            tmpRec.setToday(isToday);
            tmpRec.setDateText(date.format(ShlConstants.RECORDING_DATE_FORMATTER));
            monthlyRecordHolders.add(tmpRec);
            System.out.println(tmpRec);
            date = date.plusDays(1);
        }
        LocalDate lastDate = date.plusDays(-1);

        // レコード取得
        String sql = "SELECT pc FROM PhysicalCondition pc WHERE pc.id.id = :loginUserId AND pc.id.recordingDate BETWEEN :firstDate AND :lastDate ORDER BY pc.id.recordingDate";

        TypedQuery<PhysicalCondition> query = this.em.createQuery(sql, PhysicalCondition.class);
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
