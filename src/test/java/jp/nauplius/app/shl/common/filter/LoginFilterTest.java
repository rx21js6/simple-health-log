package jp.nauplius.app.shl.common.filter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.internal.servlet.MockHttpServletRequestImpl;
import org.jglue.cdiunit.internal.servlet.MockHttpServletResponseImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.common.service.KeyIvHolderService;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class })
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
    public void testDoFilterNormal() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.NORMAL.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "/dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl();
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
    public void testDoFilterNormalRedirection() throws IOException, ServletException {
        UserInfo normalUserInfo = new UserInfo();
        normalUserInfo.setId(2);
        normalUserInfo.setRoleId(UserRoleId.NORMAL.getInt());
        this.loginInfo.setUserInfo(normalUserInfo);

        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "/dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl();
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
        this.insertTestDataXml(this.keyIvHolderService.getEntityManager(), "/dbunit/LoginFilterTest_data01.xml");

        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl();
        request.setRequestURI("/simple-health-log");
        request.setServletPath("/");
        request.setContextPath("/simple-health-log");

        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl();

        this.loginFilter.doFilter(request, response, createDummyFilterChain());

        // 結果確認
        String location = response.getHeader("Location");
        assertTrue(location.endsWith("recordInput.xhtml"));

    }

    private FilterChain createDummyFilterChain() {
        return new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response)
                    throws IOException, ServletException {
            }
        };
    }
}
