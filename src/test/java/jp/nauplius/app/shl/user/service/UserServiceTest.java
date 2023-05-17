package jp.nauplius.app.shl.user.service;

import static org.junit.Assert.*;

import java.util.HashMap;

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
import jp.nauplius.app.shl.user.bean.UserEditingFormModel;
import jp.nauplius.app.shl.user.bean.UserEditingModel;
import jp.nauplius.app.shl.user.bean.UserListModel;
import jp.nauplius.app.shl.user.constants.UserRoleId;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class})
public class UserServiceTest extends AbstractServiceTest {
    @Inject
    private UserService userService;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private UserEditingModel userEditingModel;

    @Inject
    private UserListModel uesrListModel;

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

        UserEditingFormModel userEditingFormModel = new UserEditingFormModel();
        userEditingFormModel.setId(2);
        userEditingFormModel.setLoginId("test1");
        this.userEditingModel.setUserEditingFormModel(userEditingFormModel);

        this.userService.delete();

        UserInfo resultUserInfo = this.userService.getEntityManager().find(UserInfo.class,
                this.userEditingModel.getUserEditingFormModel().getId());
        assertTrue(resultUserInfo.getDeleted());

        assertNull(this.userService.getEntityManager().find(UserToken.class,
                this.userEditingModel.getUserEditingFormModel().getId()));
    }

    @Test(expected = SimpleHealthLogException.class)
    public void testDeleteOwnAccount() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        UserEditingFormModel userEditingFormModel = new UserEditingFormModel();
        userEditingFormModel.setId(1);
        userEditingFormModel.setLoginId("admin");
        this.userEditingModel.setUserEditingFormModel(userEditingFormModel);

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

        UserEditingFormModel userEditingFormModel = new UserEditingFormModel();
        userEditingFormModel.setLoginId("test3");
        userEditingFormModel.setName("テスト３");
        userEditingFormModel.setMailAddress("test3@test.maybe.not.exist.com");
        userEditingFormModel.setPassword("123xyz");
        userEditingFormModel.setPasswordRetype("123xyz");
        userEditingFormModel.setZoneId(StringUtils.EMPTY);
        this.userEditingModel.setNewData(true);
        this.userEditingModel.setUserEditingFormModel(userEditingFormModel);

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

        this.uesrListModel.setSelectedId(2);
        this.userService.loadMaintUsernfo();

        assertFalse(this.userEditingModel.isNewData());

        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        assertNotNull(userEditingFormModel);

        userEditingFormModel.setName(NAME);
        userEditingFormModel.setZoneId(ZONE_ID);

        this.userService.update();

        this.userService.loadMaintUsernfo();

        userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        assertNotNull(userEditingFormModel);

        assertEquals(NAME, userEditingFormModel.getName());
        assertEquals(ZONE_ID, userEditingFormModel.getZoneId());
    }

    @Test
    public void testLoadMaintUserInfos() {
        UserInfo adminUserInfo = new UserInfo();
        adminUserInfo.setId(1);
        this.loginInfo.setUserInfo(adminUserInfo);

        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.userService.loadUserInfoListItems();
        assertEquals(2, this.uesrListModel.getUserInfoListItems().size());

    }

    @Test
    public void testCreateNewData() {
        this.userService.createNewData();

        assertNotNull(this.userEditingModel);
        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        assertNotNull(userEditingFormModel);

        assertTrue("isNewData", this.userEditingModel.isNewData());
        assertTrue("isPasswordChanged", userEditingFormModel.isPasswordChanged());
        assertEquals(UserRoleId.USER.getInt(), userEditingFormModel.getRoleId());

    }

    @Test
    public void testGetMaintUsernfo() {
        this.insertTestDataXml(this.userService.getEntityManager(), "dbunit/UserServiceTest_data01.xml");

        this.uesrListModel.setSelectedId(1);
        this.userService.loadMaintUsernfo();

        assertNotNull(this.userEditingModel);
        UserEditingFormModel userEditingFormModel = this.userEditingModel.getUserEditingFormModel();
        assertNotNull(userEditingFormModel);

        assertEquals(1, userEditingFormModel.getId());
        assertEquals("admin", userEditingFormModel.getLoginId());
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
