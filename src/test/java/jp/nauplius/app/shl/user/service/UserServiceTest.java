package jp.nauplius.app.shl.user.service;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.db.model.UserToken;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.user.bean.MaintUserInfo;
import jp.nauplius.app.shl.user.bean.UserInfoListItem;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class})
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

    @Test(expected = SimpleHealthLogException.class)
    public void testDeleteOwnAccount() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo form = new MaintUserInfo();
        form.setId(1);
        form.setLoginId("admin");

        // assertThrows(SimpleHealthLogException.class, () -> {
        // this.userService.delete(form);
        // });
        this.userService.delete(form);
    }

    @Test
    public void testRegister() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setRoleId(UserRoleId.ADMIN.getInt());
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo form = new MaintUserInfo();
        form.setLoginId("test3");
        form.setName("テスト３");
        form.setMailAddress("test3@test.maybe.not.exist.com");
        form.setPassword("123xyz");
        form.setPasswordRetype("123xyz");
        form.setNewData(true);
        form.setZoneId(StringUtils.EMPTY);

        this.userService.register(form);
    }

    @Test
    public void testUpdate() {
        final String NAME = "名称変更";
        final String ZONE_ID = "Asia/Tokyo";
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setRoleId(UserRoleId.ADMIN.getInt());
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo maintUserInfo = this.userService.getMaintUsernfo(2);
        maintUserInfo.setName(NAME);
        maintUserInfo.setZoneId(ZONE_ID);

        this.userService.update(maintUserInfo);

        MaintUserInfo resultUserInfo = this.userService.getMaintUsernfo(2);
        assertEquals(NAME, resultUserInfo.getName());
        assertEquals(ZONE_ID, resultUserInfo.getZoneId());
    }

    @Test
    public void testLoadMaintUserInfos() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        List<UserInfoListItem> userInfos = this.userService.loadMaintUserInfos();
        assertEquals(2, userInfos.size());

    }

    @Test
    public void testCreateNewData() {
        MaintUserInfo maintUserInfo = this.userService.createNewData();

        assertNotNull(maintUserInfo);
        assertTrue("isNewData", maintUserInfo.isNewData());
        assertTrue("isPasswordChanged", maintUserInfo.isPasswordChanged());
        assertEquals(UserRoleId.NORMAL.getInt(), maintUserInfo.getRoleId());

    }

    @Test
    public void testGetMaintUsernfo() {
        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        MaintUserInfo maintUserInfo = this.userService.getMaintUsernfo(1);

        assertNotNull(maintUserInfo);
        assertEquals(1, maintUserInfo.getId());
        assertEquals("admin", maintUserInfo.getLoginId());
    }

    @Test
    public void testPerformSecurityEnhancement() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        adminUserInfo.setLoginId("admin");
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.userService.performSecurityEnhancement();
    }
}
