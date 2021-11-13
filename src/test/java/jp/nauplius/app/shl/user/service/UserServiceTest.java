package jp.nauplius.app.shl.user.service;

import static org.junit.Assert.*;

import java.util.HashMap;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.model.UserToken;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class UserServiceTest extends AbstractServiceTest {
    @Inject
    private UserService userService;

    @Inject
    private LoginInfo loginInfo;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.userService.getEntityManager().getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {

        this.userService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    @Test
    public void testDelete() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo form = new MaintUserInfo();
        form.setId(2);
        form.setLoginId("test1");

        this.userService.delete(form);

        UserInfo resultUserInfo = this.userService.getEntityManager().find(UserInfo.class, form.getId());
        assertTrue(resultUserInfo.getDeleted());

        assertNull(this.userService.getEntityManager().find(UserToken.class, form.getId()));
    }

    @Test
    public void testDeleteOwnAccount() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo form = new MaintUserInfo();
        form.setId(1);
        form.setLoginId("admin");

        assertThrows(SimpleHealthLogException.class, () -> {
            this.userService.delete(form);
        });
    }

}
