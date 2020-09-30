package jp.nauplius.app.shl.page.record.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import jp.nauplius.app.shl.page.record.bean.DailyRecord;

@Named
@SessionScoped
public class DailyRecordService implements Serializable {
    public List<DailyRecord> getMontylyRecords(int year, int month) {
        return null;
    }

    public void register(DailyRecord dailyRecord) {
        // TODO: 処理をかく。
    }
}
