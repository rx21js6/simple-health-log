package jp.nauplius.app.shl.maint.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.nauplius.app.shl.common.db.model.NotEnteredNotice;
import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.producer.TestEntityManagerFactoryProducer;
import jp.nauplius.app.shl.common.producer.TestLoggerProducer;
import jp.nauplius.app.shl.common.producer.TestMessageBundleProducer;
import jp.nauplius.app.shl.common.service.AbstractServiceTest;
import jp.nauplius.app.shl.maint.bean.NotEnteredNoticeFormModel;
import jp.nauplius.app.shl.maint.bean.NotEnteredNoticeSelection;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;

@RunWith(CdiRunner.class)
@ActivatedAlternatives({ TestLoggerProducer.class, TestEntityManagerFactoryProducer.class,
        TestMessageBundleProducer.class })
public class NotEnteredNoticeServiceTest extends AbstractServiceTest {
    @Inject
    private LoginInfo loginInfo;

    @Inject
    private NotEnteredNoticeService notEnteredNoticeService;

    @Inject
    private NotEnteredNoticeFormModel notEnteredNoticeFormModel;

    @Before
    public void setUp() throws Exception {
        this.sessionContext.associate(new HashMap<String, Object>());
        this.sessionContext.activate();

        this.notEnteredNoticeService.getEntityManager().getTransaction().begin();

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        this.loginInfo.setUserInfo(userInfo);

        this.insertTestDataXml(this.notEnteredNoticeService.getEntityManager(),
                "dbunit/NotEnteredNoticeServiceTest_data01.xml");
    }

    @After
    public void tearDown() throws Exception {
        this.notEnteredNoticeService.getEntityManager().getTransaction().rollback();

        this.sessionContext.invalidate();
        this.sessionContext.deactivate();
    }

    @Test
    public void testInit() {
        this.notEnteredNoticeService.init();
    }

    @Test
    public void testFindActivatedNotEnteredNotices() {
        List<NotEnteredNotice> notEnteredNotices = this.notEnteredNoticeService.findActivatedNotEnteredNotices();
        assertEquals(6, notEnteredNotices.size());
    }

    @Test
    public void testUpdate() {
        List<NotEnteredNotice> notEnteredNotices = this.notEnteredNoticeService.findActivatedNotEnteredNotices();

        List<NotEnteredNoticeSelection> selections = new ArrayList<NotEnteredNoticeSelection>();
        for (NotEnteredNotice notEnteredNotice : notEnteredNotices) {
            NotEnteredNoticeSelection selection = new NotEnteredNoticeSelection();
            try {
                BeanUtils.copyProperties(selection, notEnteredNotice);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SimpleHealthLogException(e);
            }

            selection.setChecked(!notEnteredNotice.isChecked());

            selections.add(selection);
        }

        notEnteredNoticeFormModel.setNotEnteredNoticeSelections(selections);

        this.notEnteredNoticeService.update();

        List<NotEnteredNotice> resultNotEnteredNotices1 = this.notEnteredNoticeService.findActivatedNotEnteredNotices();
        assertEquals(0, resultNotEnteredNotices1.size());
    }

}
