package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import jp.nauplius.app.shl.common.model.DailyHealthDetail;
import jp.nauplius.app.shl.common.model.DailyHealthDetailTemplate;
import jp.nauplius.app.shl.common.model.DailyHealthRecord;
import jp.nauplius.app.shl.common.model.DailyHealthRecordPK;
import jp.nauplius.app.shl.common.model.LoginUser;
import jp.nauplius.app.shl.common.model.converters.LocalDateAttributeConverter;
import jp.nauplius.app.shl.page.record.bean.DailyRecord;
import jp.nauplius.app.shl.page.record.bean.RecordHolder;

@Named
@SessionScoped
public class DailyRecordService implements Serializable {
    @Inject
    private transient EntityManager em;

    @Inject
    private LocalDateAttributeConverter converter;

    @PostConstruct
    public void init() {
        System.out.println("DailyRecordService#init() em: " + this.em);
    }

    public DailyHealthRecord getRecord(LoginUser loginUser, LocalDate postedDate) {

        String sql = "SELECT dhr FROM DailyHealthRecord dhr WHERE dhr.loginUser.id = :loginUserId AND dhr.id.postedDate = :postedDate";

        TypedQuery<DailyHealthRecord> query = this.em.createQuery(sql, DailyHealthRecord.class);
        query.setParameter("loginUserId", loginUser.getId());
        query.setParameter("postedDate", postedDate);
        try {
            query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<DailyHealthRecord> records = query.getResultList();

        DailyHealthRecord record = null;
        if (records.size() == 0) {
            record = new DailyHealthRecord();
            DailyHealthRecordPK pk = new DailyHealthRecordPK();
            pk.setLoginUserId(loginUser.getId());
            pk.setPostedDate(postedDate);
            record.setId(pk);
            record.setLoginUser(loginUser);
            record.setDailyHealthDetails(createDetails());
        } else {
            record = records.get(0);
        }

        return record;
    }

    /**
     * テンプレートから詳細を作成。
     *
     * @return
     */
    private List<DailyHealthDetail> createDetails() {
        List<DailyHealthDetail> details = new ArrayList<>();
        String sql = "SELECT t FROM DailyHealthDetailTemplate t WHERE t.deleted = FALSE ORDER BY t.id";

        TypedQuery<DailyHealthDetailTemplate> query = this.em.createQuery(sql, DailyHealthDetailTemplate.class);
        try {
            query.getResultList();
        } catch(Exception e) {
            e.printStackTrace();
        }
        List<DailyHealthDetailTemplate> templates = query.getResultList();

        for (DailyHealthDetailTemplate template : templates) {
            DailyHealthDetail detail = new DailyHealthDetail();
            detail.setDailyHealthDetailTemplate(template);
            detail.setHealthDetailId(template.getId());
            detail.setValue(1);
            details.add(detail);
        }

        return details;
    }

    public List<RecordHolder> getMontylyRecords(int loginUserId, int year, int month) {

        // 当月の1日～月末までのリストを作成
        List<RecordHolder> monthlyRecordHolders = new ArrayList<>();

        LocalDate date = LocalDate.of(year, month, 1);
        LocalDate firstDate = date;
        while (month == date.getMonthValue()) {
            RecordHolder tmpRec = new RecordHolder();
            tmpRec.setDateText(date.toString());
            monthlyRecordHolders.add(tmpRec);
            System.out.println(tmpRec);
            date = date.plusDays(1);
        }
        LocalDate lastDate = date.plusDays(-1);

        // レコード取得
        // String sql = "SELECT dhr FROM DailyHealthRecord dhr WHERE dhr.id.loginUser.id
        // = :loginUserId AND dhr.id.postedDate BETWEEN :firstDate AND :lastDate ORDER
        // BY dhr.id.postedDate";
        String sql = "SELECT dhr FROM DailyHealthRecord dhr WHERE dhr.loginUser.id = :loginUserId AND dhr.id.postedDate BETWEEN :firstDate AND :lastDate ORDER BY dhr.id.postedDate";

        TypedQuery<DailyHealthRecord> query = this.em.createQuery(sql, DailyHealthRecord.class);
        query.setParameter("loginUserId", loginUserId);
        query.setParameter("firstDate", firstDate);
        query.setParameter("lastDate", lastDate);
        List<DailyHealthRecord> results = query.getResultList();

        // マッチしたら設定
        results.forEach(d -> {
            LocalDate postedDate = d.getId().getPostedDate();
            int dom = postedDate.getDayOfMonth();
            monthlyRecordHolders.get(dom - 1).setDailyHealthRecord(d);
        });

        return monthlyRecordHolders;
    }

    public void register(DailyRecord dailyRecord) {
        // TODO: 処理をかく。
    }
}
