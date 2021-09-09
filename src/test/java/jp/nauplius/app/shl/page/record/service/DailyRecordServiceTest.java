package jp.nauplius.app.shl.page.record.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.backing.DailyRecordInputModel;
import jp.nauplius.app.shl.page.record.backing.DailyRecordListModel;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class DailyRecordServiceTest extends AbstractServiceTest {
    @Inject
    private DailyRecordService dailyRecordService;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private DailyRecordInputModel dailyRecordInputModel;

    @Inject
    private DailyRecordListModel dailyRecordListModel;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.dailyRecordService.getEntityManager().getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {

        this.dailyRecordService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    @Test
    public void testLoadRecordNotExists() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "/dbunit/DailyRecordServiceTest_data01.xml");
        PhysicalCondition condition = this.dailyRecordService.loadRecord(LocalDate.of(2021, 7, 31));

        assertNotNull(condition);
    }

    @Test
    public void testRegisterNew() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        LocalDate localDate = LocalDate.of(221, 8, 2);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "/dbunit/DailyRecordServiceTest_data01.xml");
        PhysicalCondition conditionForRegistration = this.dailyRecordService.loadRecord(localDate);

        conditionForRegistration = this.dailyRecordInputModel.getPhysicalCondition();
        conditionForRegistration.setAwakeTime(Time.valueOf(LocalTime.of(6, 20, 0)));
        conditionForRegistration.setBedTime(Time.valueOf(LocalTime.of(23, 20, 0)));
        conditionForRegistration.setBodyTemperatureMorning(BigDecimal.valueOf(36.3));
        conditionForRegistration.setBodyTemperatureMorning(BigDecimal.valueOf(36.4));
        conditionForRegistration.setConditionNote("𠮷野家で牛丼食った😎\n健康");

        this.dailyRecordInputModel.setPhysicalCondition(conditionForRegistration);

        this.dailyRecordService.register();

        PhysicalCondition conditionResult = this.dailyRecordService.loadRecord(localDate);

        assertNotNull(conditionResult);
        assertThat(conditionResult.getAwakeTime(), is(conditionForRegistration.getAwakeTime()));
        assertThat(conditionResult.getBedTime(), is(conditionForRegistration.getBedTime()));
        assertThat(conditionResult.getBodyTemperatureMorning(),
                is(conditionForRegistration.getBodyTemperatureMorning()));
        assertThat(conditionResult.getBodyTemperatureEvening(),
                is(conditionForRegistration.getBodyTemperatureEvening()));
        assertThat(conditionResult.getConditionNote(), is(conditionForRegistration.getConditionNote()));
    }

    @Test
    public void testRegisterUpdate() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "/dbunit/DailyRecordServiceTest_data01.xml");
        PhysicalCondition conditionForUpdate = this.dailyRecordService.loadRecord(LocalDate.of(2021, 8, 1));

        assertNotNull(conditionForUpdate);

        conditionForUpdate.setAwakeTime(Time.valueOf(LocalTime.of(6, 20, 0)));
        conditionForUpdate.setBedTime(Time.valueOf(LocalTime.of(23, 20, 0)));
        conditionForUpdate.setBodyTemperatureMorning(BigDecimal.valueOf(36.3));
        conditionForUpdate.setBodyTemperatureMorning(BigDecimal.valueOf(36.4));
        conditionForUpdate.setConditionNote("ちょっと熱っぽいな🤒\nメロン食べるか😋🍈");

        this.dailyRecordInputModel.setPhysicalCondition(conditionForUpdate);

        this.dailyRecordService.register();

        PhysicalCondition conditionResult = this.dailyRecordService.loadRecord(LocalDate.of(2021, 8, 1));

        assertNotNull(conditionResult);
        assertThat(conditionResult.getAwakeTime(), is(conditionForUpdate.getAwakeTime()));
        assertThat(conditionResult.getBedTime(), is(conditionForUpdate.getBedTime()));
        assertThat(conditionResult.getBodyTemperatureMorning(), is(conditionForUpdate.getBodyTemperatureMorning()));
        assertThat(conditionResult.getBodyTemperatureEvening(), is(conditionForUpdate.getBodyTemperatureEvening()));
        assertThat(conditionResult.getConditionNote(), is(conditionForUpdate.getConditionNote()));
        assertThat(conditionResult.getModifiedBy(), is(userInfo.getId()));
    }

    @Test
    public void testLoadDailyRecords() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "/dbunit/DailyRecordServiceTest_data01.xml");

        this.dailyRecordService.loadDailyRecords(LocalDate.of(2021, 8, 1));
        assertNotNull(this.dailyRecordListModel.getDailyRecords());

        // 有効な利用者のレコードのみ取得
        assertThat(this.dailyRecordListModel.getDailyRecords().size(), is(2));
    }

}
