package jp.nauplius.app.shl.common.filter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cdiunit.ActivatedAlternatives;
import io.github.cdiunit.CdiRunner;
import io.github.cdiunit.internal.servlet5.MockHttpServletRequestImpl;
import io.github.cdiunit.internal.servlet5.MockHttpServletResponseImpl;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class})
public class LoginFilterTest extends AbstractServiceTest {
    @Inject
    private LoginFilter loginFilter;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private KeyIvHolderService keyIvHolderService;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.keyIvHolderService.getEntityManager().getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        this.keyIvHolderService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    /**
     * ログイン済み
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public void testDoFilterUser() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.USER.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(null, null);
        request.setRequestURI("/simple-health-log/contents/record/monthlyRecord.xhtml");
        request.setServletPath("/contents/record/monthlyRecord.xhtml");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        // 遷移しない
        assertNull(location);
    }

    /**
     * ログイン済みでトップページからリダイレクト確認
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public void testDoFilterUserRedirection() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.USER.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(null, null);
        request.setRequestURI("/simple-health-log");
        request.setServletPath("/");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        assertTrue(location.endsWith("recordInput.xhtml"));
    }

    /**
     * 未ログイン
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public void testDoFilterNotLoggedIn() throws IOException, ServletException {
        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(null, null);
        request.setRequestURI("/simple-health-log");
        request.setServletPath("/");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        assertTrue(location.endsWith("recordInput.xhtml"));

    }

    /**
     * ログイン済み、一般、不許可ページ
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public void testDoFilterNormalNotAuthorized() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.USER.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(null, null);
        request.setRequestURI("/simple-health-log/contents/maint/user/userList.xhtml");
        request.setServletPath("/contents/maint/user/userList.xhtml");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        // 権限エラー画面に遷移
        assertTrue(location.endsWith("/error/authError.xhtml"));
    }

    /**
     * ログイン済み、管理者、制限ページ
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public void testDoFilterAdmin() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.ADMIN.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(null, null);
        request.setRequestURI("/simple-health-log/contents/maint/user/userList.xhtml");
        request.setServletPath("/contents/maint/user/userList.xhtml");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        // 遷移しない
        assertNull(location);
    }

    /**
     * ダミーのFilterChain生成
     *
     * @return
     */
    private FilterChain createDummyFilterChain() {
        return new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response)
                    throws IOException, ServletException {
            }
        };
    }
}
