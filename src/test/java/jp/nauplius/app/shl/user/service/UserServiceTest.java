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
import jp.nauplius.app.shl.user.bean.UserEditingModel;
import jp.nauplius.app.shl.user.bean.UserInfoListItem;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class UserServiceTest extends AbstractServiceTest {
    @Inject
    private UserService userService;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private UserEditingModel userEditingModel;

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

        this.userEditingModel.setId(2);
        this.userEditingModel.setLoginId("test1");

        this.userService.delete();

        UserInfo resultUserInfo = this.userService.getEntityManager().find(UserInfo.class,
                this.userEditingModel.getId());
        assertTrue(resultUserInfo.getDeleted());

        assertNull(this.userService.getEntityManager().find(UserToken.class, this.userEditingModel.getId()));
    }

    @Test(expected = SimpleHealthLogException.class)
    public void testDeleteOwnAccount() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.userEditingModel.setId(1);
        this.userEditingModel.setLoginId("admin");

        // assertThrows(SimpleHealthLogException.class, () -> {
        // this.userService.delete(form);
        // });
        this.userService.delete();
    }

    @Test
    public void testRegister() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setRoleId(UserRoleId.ADMIN.getInt());
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.userEditingModel.setLoginId("test3");
        this.userEditingModel.setName("テスト３");
        this.userEditingModel.setMailAddress("test3@test.maybe.not.exist.com");
        this.userEditingModel.setPassword("123xyz");
        this.userEditingModel.setPasswordRetype("123xyz");
        this.userEditingModel.setNewData(true);
        this.userEditingModel.setZoneId(StringUtils.EMPTY);

        this.userService.register();
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

        this.userService.loadMaintUsernfo(2);
        this.userEditingModel.setName(NAME);
        this.userEditingModel.setZoneId(ZONE_ID);

        this.userService.update();

        this.userService.loadMaintUsernfo(2);
        assertEquals(NAME, this.userEditingModel.getName());
        assertEquals(ZONE_ID, this.userEditingModel.getZoneId());
    }

    @Test
    public void testLoadMaintUserInfos() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        List<UserInfoListItem> userInfos = this.userService.getUserInfoListItems();
        assertEquals(2, userInfos.size());

    }

    @Test
    public void testCreateNewData() {
        this.userService.createNewData();

        assertNotNull(this.userEditingModel);
        assertTrue("isNewData", this.userEditingModel.isNewData());
        assertTrue("isPasswordChanged", this.userEditingModel.isPasswordChanged());
        assertEquals(UserRoleId.USER.getInt(), this.userEditingModel.getRoleId());

    }

    @Test
    public void testGetMaintUsernfo() {
        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.userService.loadMaintUsernfo(1);

        assertNotNull(this.userEditingModel);
        assertEquals(1, this.userEditingModel.getId());
        assertEquals("admin", this.userEditingModel.getLoginId());
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
