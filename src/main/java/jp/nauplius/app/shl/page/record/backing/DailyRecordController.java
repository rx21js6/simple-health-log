
package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.ShlConstants;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.ui.backing.CommonConfirmModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalControllerListener;
import jp.nauplius.app.shl.common.ui.bean.CommonConfirmModalBean;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.service.CookieService;
import jp.nauplius.app.shl.page.login.service.LoginService;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import jp.nauplius.app.shl.page.record.service.MonthlyRecordService;
import lombok.Getter;
import lombok.Setter;

/**
 * 日次入力画面コントローラ
 *
 */
@SessionScoped
@Named
public class DailyRecordController implements Serializable, ModalControllerListener {
    @Inject
    private Logger logger;

    @Inject
    private transient ResourceBundle messageBundle;

    @Inject
    private FacesContext facesContext;

    @Inject
    private LoginService loginService;

    @Inject
    private DailyRecordService dailyRecordService;

    @Inject
    private MonthlyRecordService monthlyRecordService;

    @Inject
    private CookieService cookieService;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    private CommonConfirmModalController commonConfirmModalController;

    @Inject
    private CommonConfirmModalBean commonConfirmModalBean;

    @Inject
    private DailyRecordInputModel dailyRecordInputModel;

    @Inject
    private MonthlyRecordModel monthlyRecordModel;

    @Inject
    @Getter
    @Setter
    private LoginForm loginForm;

    @Getter
    @Setter
    private LocalDate today;

    @Getter
    @Setter
    private String selectedDate;

    private Method dispatchMethod;

    @PostConstruct
    public void postConstruct() {
        this.today = LocalDate.now();
    }

    public void init() {
        this.logger.info("DailyRecordController#init");

        this.initModal();

        UserInfo tmpUserInfo = this.loginInfo.getUserInfo();
        if (Objects.isNull(tmpUserInfo)) {
            // 未ログイン状態
            String token = this.cookieService.getToken(this.facesContext);
            if (StringUtils.isNotEmpty(token)) {
                tmpUserInfo = this.loginService.loginFromToken(token);
            }
        }

        if (tmpUserInfo != null) {
            // ログインできた場合は当日のレコードを取得
            this.loginInfo.setUserInfo(tmpUserInfo);
            this.today = !StringUtils.isEmpty(this.selectedDate)
                    ? LocalDate.parse(this.selectedDate, ShlConstants.RECORDING_DATE_FORMATTER)
                    : LocalDate.now();
            this.dailyRecordService.loadRecord(this.today);
        }

        this.logger.info("DailyRecordController#init complete");
    }

    public void loadRecord() {
        this.logger.info("DailyRecordController#loadRecord");
        this.dailyRecordService.loadRecord(today);
    }

    public String register() {
        this.commonConfirmModalBean.setVisible(false);
        try {
            this.dailyRecordService.register();

            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.record.recordInput.msg.registered"), null));
        } catch (SimpleHealthLogException e) {
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    this.messageBundle.getString("contents.record.recordInput.label.msg.registrationFailed"), null));
        }

        return null;
    }

    /**
     * 入力画面表示 月次リストから遷移時に実行
     *
     * @return
     */
    public String showInput() {
        return "/contents/record/recordInput.xhtml?faces-redirect=true";
    }

    /**
     * 前日表示
     *
     * @param force
     * @return
     */
    public String loadYesterday(boolean force) {
        this.commonConfirmModalBean.setVisible(false);
        if (!force && this.isFormEdited()) {
            this.setModal("loadYesterday");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.today = this.today.minusDays(1);
        this.dailyRecordService.loadRecord(this.today);
        return null;
    }

    /**
     * 当日表示
     *
     * @param force
     * @return
     */
    public String loadToday(boolean force) {
        this.commonConfirmModalBean.setVisible(false);
        if (!force && this.isFormEdited()) {
            this.setModal("loadToday");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.today = LocalDate.now();
        this.dailyRecordService.loadRecord(this.today);
        return null;
    }

    /**
     * 翌日表示
     *
     * @param force
     * @return
     */
    public String loadTomorrow(boolean force) {
        this.commonConfirmModalBean.setVisible(false);
        if (!force && this.isFormEdited()) {
            this.setModal("loadTomorrow");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.today = this.today.plusDays(1);
        this.dailyRecordService.loadRecord(today);
        return null;
    }

    /**
     * 月次表示
     *
     * @param force
     * @return
     */
    public String showMonthlyList(boolean force) {
        this.commonConfirmModalBean.setVisible(false);
        if (!force && this.isFormEdited()) {
            this.setModal("showMonthlyList");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.today = LocalDate.of(this.today.getYear(), this.today.getMonth(), 1);
        this.monthlyRecordModel.setToday(this.today);
        this.monthlyRecordService.loadMonthlyRecords();

        return "/contents/record/monthlyRecord.xhtml?faces-redirect=true";
    }

    /**
     * 全利用者の日次表示
     *
     * @param force
     * @return
     */
    public String showDailyList(boolean force) {
        if (!force && this.isFormEdited()) {
            this.setModal("showMonthlyList");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.dailyRecordService.loadDailyRecords(this.today);
        return "/contents/record/dailyRecord.xhtml?faces-redirect=true";
    }

    @Override
    public void initModal() {
        this.logger.debug("initModa");
        this.commonConfirmModalBean.setVisible(false);
        this.commonConfirmModalBean.setMessage(StringUtils.EMPTY);
        this.commonConfirmModalController.setModalControllerListener(this);
    }

    @Override
    public String fireAction(ModalController modalAction, String commandTypeName, String buttonName) {
        String res = null;
        try {
            res = (String) this.dispatchMethod.invoke(this, true);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new SimpleHealthLogException(e);
        }
        return res;
    }

    private boolean isFormEdited() {
        return this.dailyRecordInputModel.isModelEdited();
    }

    private void setModal(String actionMethodName) {
        this.commonConfirmModalBean.setTitle(this.messageBundle.getString("common.msg.editing"));
        this.commonConfirmModalBean.setMessage(this.messageBundle.getString("contents.record.recordInput.msg.editing"));
        this.commonConfirmModalBean.setVisible(true);
        this.commonConfirmModalBean.setOkButtonValue(this.messageBundle.getString("common.label.run"));
        this.commonConfirmModalBean.setCancelButtonValue(this.messageBundle.getString("common.label.cancel"));
        this.dispatchMethod = getActionMethod(actionMethodName);
    }

}
