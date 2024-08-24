package jp.nauplius.app.shl.maint.service;

import static org.junit.Assert.assertNotNull;

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
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.maint.bean.CustomSettingKeyIvModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class})
public class AdminSettingServiceTest extends AbstractServiceTest {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private AdminSettingService adminSettingService;

    @Inject
    private CustomSettingKeyIvModel customSettingKeyIvModel;

    private GreenMail greenMail;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.adminSettingService.getEntityManager().getTransaction().begin();

        this.greenMail = new GreenMail(ServerSetupTest.SMTP);
        this.greenMail.start();
    }

    @After
    public void tearDown() throws Exception {
        this.adminSettingService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();

        this.greenMail.stop();
    }

    /**
     * 画面ロード 正常
     */
    @Test
    public void testLoad() {
        this.insertTestDataXml(this.adminSettingService.getEntityManager(),
                "dbunit/AdminSettingServiceTest_data01.xml");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(2);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        this.adminSettingService.load();

        // Key/Iv loaded?
        assertNotNull("Key exists?", this.customSettingKeyIvModel.getKey());
        assertNotNull("Iv exists?", this.customSettingKeyIvModel.getIv());
    }

    /**
     * 画面ロード ユーザなし
     */
    @Test(expected = SimpleHealthLogException.class)
    public void testLoad_userInfoNull() {
        this.insertTestDataXml(this.adminSettingService.getEntityManager(),
                "dbunit/AdminSettingServiceTest_data01.xml");
        UserInfo userInfo = new UserInfo();
        // Not exists.
        userInfo.setId(3278);
        userInfo.setMailAddress(null);
        this.loginInfo.setUserInfo(userInfo);

        this.adminSettingService.load();
    }
}
