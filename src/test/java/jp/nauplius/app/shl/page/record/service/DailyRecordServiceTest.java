package jp.nauplius.app.shl.page.record.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cdiunit.ActivatedAlternatives;
import io.github.cdiunit.CdiRunner;
import jakarta.inject.Inject;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.record.bean.DailyRecordForm;
import jp.nauplius.app.shl.page.record.bean.DailyRecordInputModel;
import jp.nauplius.app.shl.page.record.bean.DailyRecordListModel;

/**
 * DailyRecordServiceTest
 *
 */
@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class})
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
    public void testLoadRecordExists() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(2);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "dbunit/DailyRecordServiceTest_data01.xml");
        // 登録済み日付
        this.dailyRecordService.loadRecord(LocalDate.of(2021, 8, 2));

        // 当日分
        assertNotNull(this.dailyRecordInputModel.getDailyRecordForm());
        assertThat(this.dailyRecordInputModel.getDailyRecordForm().getId().getRecordingDate(), is("20210802"));

        // 前日分
        assertNotNull(this.dailyRecordInputModel.getPreviousDailyRecordForm());
        assertThat(this.dailyRecordInputModel.getPreviousDailyRecordForm().getId().getRecordingDate(), is("20210801"));
        assertThat(this.dailyRecordInputModel.getPreviousDailyRecordForm().getBodyTemperatureEvening(),
                is(BigDecimal.valueOf(36.3)));
    }

    @Test
    public void testLoadRecordNotExists() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(2);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "dbunit/DailyRecordServiceTest_data01.xml");
        // 未登録レコード
        this.dailyRecordService.loadRecord(LocalDate.of(2021, 7, 31));

        // 当日分
        assertNotNull(this.dailyRecordInputModel.getDailyRecordForm());
        assertThat(this.dailyRecordInputModel.getDailyRecordForm().getId().getRecordingDate(), is("20210731"));
        // 前日分
        assertNotNull(this.dailyRecordInputModel.getPreviousDailyRecordForm());
        assertThat(this.dailyRecordInputModel.getPreviousDailyRecordForm().getId().getRecordingDate(), is("20210730"));
        assertNull(this.dailyRecordInputModel.getPreviousDailyRecordForm().getBodyTemperatureEvening());
    }

    @Test
    public void testRegisterNew() {
        LocalDateTime now = LocalDateTime.now();
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        LocalDate localDate = LocalDate.of(221, 8, 2);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "dbunit/DailyRecordServiceTest_data01.xml");
        this.dailyRecordService.loadRecord(localDate);

        DailyRecordForm conditionForRegistration = this.dailyRecordInputModel.getDailyRecordForm();
        conditionForRegistration.setAwakeTime(Time.valueOf(LocalTime.of(6, 20, 0)));
        conditionForRegistration.setBedTime(Time.valueOf(LocalTime.of(23, 20, 0)));
        conditionForRegistration.setBodyTemperatureMorning(BigDecimal.valueOf(36.3));
        conditionForRegistration.setBodyTemperatureMorning(BigDecimal.valueOf(36.4));
        conditionForRegistration.setConditionNote("𠮷野家で牛丼食った😎\n健康");

        this.dailyRecordInputModel.setPhysicalCondition(conditionForRegistration.toPhysicalCondition());

        this.dailyRecordService.register();

        this.dailyRecordService.loadRecord(localDate);

        DailyRecordForm conditionResult = this.dailyRecordInputModel.getDailyRecordForm();

        assertNotNull(conditionResult);
        assertThat(conditionResult.getAwakeTime(), is(conditionForRegistration.getAwakeTime()));
        assertThat(conditionResult.getBedTime(), is(conditionForRegistration.getBedTime()));
        assertThat(conditionResult.getBodyTemperatureMorning(),
                is(conditionForRegistration.getBodyTemperatureMorning()));
        assertThat(conditionResult.getBodyTemperatureEvening(),
                is(conditionForRegistration.getBodyTemperatureEvening()));
        assertThat(conditionResult.getConditionNote(), is(conditionForRegistration.getConditionNote()));
        assertThat(conditionResult.getCreatedBy(), is(userInfo.getId()));
        assertTrue(now.isBefore(conditionResult.getCreatedDate().toLocalDateTime()));
        assertThat(conditionResult.getModifiedBy(), is(userInfo.getId()));
        assertTrue(now.isBefore(conditionResult.getModifiedDate().toLocalDateTime()));
    }

    @Test
    public void testRegisterUpdate() {
        LocalDateTime now = LocalDateTime.now();
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "dbunit/DailyRecordServiceTest_data01.xml");
        this.dailyRecordService.loadRecord(LocalDate.of(2021, 8, 1));

        DailyRecordForm conditionForUpdate = this.dailyRecordInputModel.getDailyRecordForm();
        assertNotNull(conditionForUpdate);
        conditionForUpdate.setAwakeTime(Time.valueOf(LocalTime.of(6, 20, 0)));
        conditionForUpdate.setBedTime(Time.valueOf(LocalTime.of(23, 20, 0)));
        conditionForUpdate.setBodyTemperatureMorning(BigDecimal.valueOf(36.3));
        conditionForUpdate.setBodyTemperatureMorning(BigDecimal.valueOf(36.4));
        conditionForUpdate.setConditionNote("ちょっと熱っぽいな🤒\nメロン食べるか😋🍈");

        this.dailyRecordInputModel.setPhysicalCondition(conditionForUpdate.toPhysicalCondition());

        this.dailyRecordService.register();

        this.dailyRecordService.loadRecord(LocalDate.of(2021, 8, 1));
        DailyRecordForm conditionResult = this.dailyRecordInputModel.getDailyRecordForm();
        assertNotNull(conditionResult);
        assertThat(conditionResult.getAwakeTime(), is(conditionForUpdate.getAwakeTime()));
        assertThat(conditionResult.getBedTime(), is(conditionForUpdate.getBedTime()));
        assertThat(conditionResult.getBodyTemperatureMorning(), is(conditionForUpdate.getBodyTemperatureMorning()));
        assertThat(conditionResult.getBodyTemperatureEvening(), is(conditionForUpdate.getBodyTemperatureEvening()));
        assertThat(conditionResult.getConditionNote(), is(conditionForUpdate.getConditionNote()));
        assertThat(conditionResult.getCreatedBy(), is(userInfo.getId()));
        assertTrue(now.isAfter(conditionResult.getCreatedDate().toLocalDateTime()));
        assertThat(conditionResult.getModifiedBy(), is(userInfo.getId()));
        assertTrue(now.isBefore(conditionResult.getModifiedDate().toLocalDateTime()));
    }

    @Test
    public void testLoadDailyRecords() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.dailyRecordService.getEntityManager(), "dbunit/DailyRecordServiceTest_data01.xml");

        this.dailyRecordService.loadDailyRecords(LocalDate.of(2021, 8, 1));
        assertNotNull(this.dailyRecordListModel.getDailyRecords());

        // 有効な利用者のレコードのみ取得
        assertThat(this.dailyRecordListModel.getDailyRecords().size(), is(2));
    }

}
