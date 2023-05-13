package jp.nauplius.app.shl.page.record.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.backing.MonthlyRecordModel;
import jp.nauplius.app.shl.page.record.bean.RecordHolder;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class MonthlyRecordServiceTest extends AbstractServiceTest {
    @Inject
    private MonthlyRecordService monthlyRecordService;

    @Inject
    private MonthlyRecordModel monthlyRecordModel;

    @Inject
    private LoginInfo loginInfo;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.monthlyRecordService.getEntityManager().getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        this.monthlyRecordService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    @Test
    public void testLoadMonthlyRecordsDateNull() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.monthlyRecordService.getEntityManager(),
                "dbunit/MonthlyRecordServiceTest_data01.xml");

        this.monthlyRecordModel.setToday(null);

        this.monthlyRecordService.loadMonthlyRecords();

        LocalDate today = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate();
        assertThat(this.monthlyRecordModel.getToday(), is(today));
        assertNotNull(this.monthlyRecordModel.getMonthlyRecords());
        assertThat(this.monthlyRecordModel.getMonthlyRecords().size(), is(today.lengthOfMonth()));

    }

    @Test
    public void testLoadMonthlyRecordsDateLeapYear() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.monthlyRecordService.getEntityManager(),
                "dbunit/MonthlyRecordServiceTest_data01.xml");

        LocalDate today = LocalDate.of(2024, 2, 29);

        this.monthlyRecordModel.setToday(today);

        this.monthlyRecordService.loadMonthlyRecords();

        assertThat(this.monthlyRecordModel.getToday(), is(today));
        assertNotNull(this.monthlyRecordModel.getMonthlyRecords());
        assertThat(this.monthlyRecordModel.getMonthlyRecords().size(), is(today.lengthOfMonth()));

        List<RecordHolder> monthlyRecords = this.monthlyRecordModel.getMonthlyRecords();
        assertThat(monthlyRecords.get(0).getDateText(), is("20240201"));
        assertThat(monthlyRecords.get(0).getPhysicalCondition().getConditionNote(), is("20240201テスト"));
        assertThat(monthlyRecords.get(today.lengthOfMonth() - 1).getDateText(), is("20240229"));
        assertThat(monthlyRecords.get(today.lengthOfMonth() - 1).getPhysicalCondition().getConditionNote(),
                is("20240229テスト"));
    }
}
