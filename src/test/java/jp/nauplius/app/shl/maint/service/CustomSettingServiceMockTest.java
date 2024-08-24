package jp.nauplius.app.shl.maint.service;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import io.github.cdiunit.ActivatedAlternatives;
import io.github.cdiunit.CdiRunner;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.producer.TestCustomSettingMailSenderProducer;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.maint.bean.CustomSettingMailAddressModel;
import jp.nauplius.app.shl.maint.bean.CustomSettingPasswordModel;
import jp.nauplius.app.shl.maint.bean.TimeZoneInputModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class, TestCustomSettingMailSenderProducer.class})
public class CustomSettingServiceMockTest extends AbstractServiceTest {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private CustomSettingService customSettingService;

    @Inject
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private CustomSettingMailAddressModel customSettingMailAddressModel;

    @Inject
    private TimeZoneInputModel timeZoneInputModel;

    private GreenMail greenMail;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.customSettingService.getEntityManager().getTransaction().begin();

        this.greenMail = new GreenMail(ServerSetupTest.SMTP);
        this.greenMail.start();
    }

    @After
    public void tearDown() throws Exception {
        this.customSettingService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();

        this.greenMail.stop();
    }

    /**
     * 画面ロード（失敗） User not exists.
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testLoadUserNotExists() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(65535);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        this.customSettingService.load();
    }

    /**
     * 画面ロード（失敗） MailSender is not active.
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testLoadMailSenderNotActive() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        this.customSettingService.load();
    }

    /**
     * パスワード変更 User not exists.
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testChangePasswordUserNotExists() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(65535);
        userInfo.setMailAddress(null);
        userInfo.setEncryptedPassword("dCpslkAK/OHhhhnWw7JvcFE0lL+hj4sMNql5IHtm8vg=");
        this.loginInfo.setUserInfo(userInfo);

        String password = "ABCDabcd_1234";
        // String currentEncryptedPassword =
        // this.loginInfo.getUserInfo().getEncryptedPassword();

        this.customSettingPasswordModel.setPassword(password);
        this.customSettingService.changePassword();
    }

    /**
     * メールアドレス変更（失敗） User not exists.
     *
     * @throws MessagingException
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testChangeMailAddress() throws MessagingException {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(65535);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        String mailAddress = "changed@foobar.xxxxxxxxxxxxxxxx123456789.com";
        this.customSettingMailAddressModel.setMailAddress(mailAddress);

        this.customSettingService.changeMailAddress();
    }

    /**
     * タイムゾーン変更（失敗） User not exists.
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testChangeTimeZone() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(65535);
        userInfo.setZoneId("UTC");
        this.loginInfo.setUserInfo(userInfo);

        final String ZONE_ID = "Asia/Tokyo";

        this.timeZoneInputModel.setSelectedZoneId(ZONE_ID);
        this.customSettingService.changeTimeZone();
    }
}
