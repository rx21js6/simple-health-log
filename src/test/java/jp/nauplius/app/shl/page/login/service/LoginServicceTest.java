package jp.nauplius.app.shl.page.login.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.inject.Inject;

import org.jboss.weld.context.bound.BoundSessionContext;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.listener.InitializationListener;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

/**
 * LoginService test
 *
 */
@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class })
public class LoginServicceTest extends AbstractServiceTest {
    @Inject
    private LoginService loginService;

    @Inject
    private BoundSessionContext sessionContext;

    @Inject
    private InitializationListener initializationListener;

    @Before
    public void setUp() throws Exception {
        this.initializationListener.contextInitialized(null);
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.loginService.getEntityManager().getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {

        this.loginService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    @Test
    public void testLoginSuccess() {
        this.insertTestDataXml(this.loginService.getEntityManager(), "/dbunit/LoginServiceTest_data01.xml");

        LoginForm loginForm = new LoginForm();
        loginForm.setLoginId("admin");
        loginForm.setPassword("password1234!\"$#");
        loginForm.setKeepLogin(false);
        LoginResponse loginResponse = loginService.login(loginForm);
        assertEquals("admin", loginResponse.getUserInfo().getLoginId());
    }

    @Test(expected = SimpleHealthLogException.class)
    public void testLoginFailed() {
        this.insertTestDataXml(this.loginService.getEntityManager(), "/dbunit/LoginServiceTest_data01.xml");

        LoginForm loginForm = new LoginForm();
        loginForm.setLoginId("admin");
        loginForm.setPassword("unmatchedPassword1234");
        loginForm.setKeepLogin(false);
        loginService.login(loginForm);
    }

    @Test
    public void testLogout() {
        this.loginService.logout();
    }

    @Test
    public void testGetUsers() {
       GetUsersResponse response = this.loginService.getUsers();
       assertEquals(1,response.getUserInfos().size());
    }


    @Test
    public void testLoginFromToken() {
        this.insertTestDataXml(this.loginService.getEntityManager(), "/dbunit/LoginServiceTest_data01.xml");
        UserInfo userInfo = this.loginService.loginFromToken("0cn4Zn3GIJCIE6D8oPtvnFbL6i5d6xGyk3uvO++ea5ui2LDm0ZUZykFwuSLJgOiWBL36GVEM+GKsnJjG1pyb6A==");
        assertEquals("admin", userInfo.getLoginId());
    }

    @Test
    public void testResetPassword() {
        // fail("まだ実装されていません");
    }
}