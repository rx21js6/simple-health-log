package jp.nauplius.app.shl.maint.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.hamcrest.CoreMatchers;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.maint.backing.CustomSettingMailAddressModel;
import jp.nauplius.app.shl.maint.backing.CustomSettingPasswordModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class CustomSettingServiceTest extends AbstractServiceTest {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private CustomSettingService customSettingService;

    @Inject
    private CustomSettingPasswordModel customSettingPasswordModel;

    @Inject
    private CustomSettingMailAddressModel customSettingMailAddressModel;

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

    @Test
    public void testLoad() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "/dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        this.customSettingService.load();

        assertThat(this.customSettingMailAddressModel.getCurrentMailAddress(),
                is("normal_test@maybe.noexistant.nauplius.jp"));
    }

    @Test
    public void testChangePassword() {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "/dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        userInfo.setEncryptedPassword("lOMRTqtxn8qcIp/9j+k9l3Ruu8E0W/aRH/sAKui0Vps=");
        this.loginInfo.setUserInfo(userInfo);

        String password = "ABCDabcd_1234";
        String currentEncryptedPassword = this.loginInfo.getUserInfo().getEncryptedPassword();

        this.customSettingPasswordModel.setPassword(password);
        this.customSettingService.changePassword();

        // LoginInfoが更新されていること
        assertNotEquals(currentEncryptedPassword, this.loginInfo.getUserInfo().getEncryptedPassword());
    }

    @Test
    public void testSendTestMail() throws MessagingException {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "/dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        String mailAddress = "changed@foobar.xxxxxxxxxxxxxxxx123456789.com";
        this.customSettingMailAddressModel.setMailAddress(mailAddress);
        this.customSettingService.sendTestMail();

        // テストメール送信確認
        assertThat(this.greenMail.getReceivedMessages().length, is(1));
        MimeMessage actual = this.greenMail.getReceivedMessages()[0];
        assertThat(actual.getRecipients(RecipientType.TO)[0].toString(), CoreMatchers.is(mailAddress));
    }

    @Test
    public void testChangeMailAddress() throws MessagingException {
        this.insertTestDataXml(this.customSettingService.getEntityManager(),
                "/dbunit/CustomSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        String mailAddress = "changed@foobar.xxxxxxxxxxxxxxxx123456789.com";
        this.customSettingMailAddressModel.setMailAddress(mailAddress);

        this.customSettingService.changeMailAddress();

        // LoginInfoが更新されていること
        assertEquals(mailAddress, this.loginInfo.getUserInfo().getMailAddress());

        // テストメール送信確認
        assertThat(this.greenMail.getReceivedMessages().length, is(1));
        MimeMessage actual = this.greenMail.getReceivedMessages()[0];
        assertThat(actual.getRecipients(RecipientType.TO)[0].toString(), CoreMatchers.is(mailAddress));

    }

}
