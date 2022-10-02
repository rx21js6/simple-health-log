
package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.constants.NotEnteredNoticeTypeKey;
import jp.nauplius.app.shl.common.constants.SecurityLevel;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.NotEnteredNotice;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.common.ui.backing.CommonConfirmModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalController;
import jp.nauplius.app.shl.common.ui.backing.ModalControllerListener;
import jp.nauplius.app.shl.common.ui.bean.CommonConfirmModalBean;
import jp.nauplius.app.shl.maint.service.NotEnteredNoticeService;
import jp.nauplius.app.shl.page.login.bean.LoginFormModel;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
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
@ViewScoped
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
    private NotEnteredNoticeService notEnteredNoticeService;

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
    private LoginFormModel loginFormModel;

    @Getter
    @Setter
    private LocalDate today;

    private String methodName;

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

                // クッキーの期限を更新
                this.cookieService.registerToken(this.facesContext, token);
            }
        }

        if (tmpUserInfo != null) {
            // ログインできた場合は選択日のレコードを取得
            this.loginInfo.setUserInfo(tmpUserInfo);
            this.today = this.dailyRecordInputModel.parseSelectedDate();
            this.load();
        }

        this.logger.info("DailyRecordController#init complete");
    }

    /**
     * ログイン処理
     *
     * @return null
     */
    public String login() {
        this.logger.info("login");
        if (Objects.isNull(this.loginInfo.getUserInfo())) {
            try {
                LoginResponse loginResponse = this.loginService.login(this.loginFormModel.getLoginForm());
                if (this.loginFormModel.getLoginForm().isLoggingPersistent()) {
                    this.cookieService.registerToken(this.facesContext, loginResponse.getUserToken().getToken());
                }

                this.loadToday(true);

            } catch (SimpleHealthLogException e) {
                this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
                this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        this.messageBundle.getString("login.msg.failed") + e.getMessage(), null));
            }
        }
        return null;
    }

    /**
     * ログイン中のユーザ名を表示
     * @return
     */
    public String showLoggingUserName() {
        return this.loginService.showLoggingUserName();
    }

    /**
     * レコード取得
     */
    public void loadRecord() {
        this.logger.info("DailyRecordController#loadRecord");
        this.dailyRecordService.loadRecord(this.today);
        this.setMessages();
    }

    /**
     * 登録
     *
     * @return
     */
    public String register() {
        this.commonConfirmModalBean.setVisible(false);
        try {
            this.dailyRecordService.register();

            this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    this.messageBundle.getString("contents.record.recordInput.msg.registered"), null));
        } catch (SimpleHealthLogException e) {
            this.facesContext.getExternalContext().getFlash().setKeepMessages(true);
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    this.messageBundle.getString("contents.record.recordInput.label.msg.registrationFailed"), null));
        }

        return null;
    }

    /**
     * 入力画面表示 月次リスト以外から遷移時に実行
     *
     * @return
     */
    public String showInput() {
        this.dailyRecordInputModel.setSelectedDate(StringUtils.EMPTY);
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
        this.setMessages();
        return null;
    }

    /**
     * 表示
     *
     * @param force
     * @return
     */
    public String load() {
        this.dailyRecordInputModel.reset();
        this.dailyRecordService.loadRecord(this.today);
        this.setMessages();
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
        this.setMessages();
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
        this.dailyRecordService.loadRecord(this.today);
        this.setMessages();
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

    /**
     * カスタム設定表示
     *
     * @param force
     * @return
     */
    public String showCustomSetting(boolean force) {
        this.commonConfirmModalBean.setVisible(false);
        if (!force && this.isFormEdited()) {
            this.setModal("showCustomSetting");
            return null;
        }

        this.dailyRecordInputModel.reset();
        this.today = LocalDate.of(this.today.getYear(), this.today.getMonth(), 1);
        this.monthlyRecordModel.setToday(this.today);
        this.monthlyRecordService.loadMonthlyRecords();

        return "/contents/maint/setting/customSetting.xhtml?faces-redirect=true";
    }

    /**
     * メッセージを設定
     */
    private void setMessages() {
        this.facesContext.getExternalContext().getFlash().setKeepMessages(true);

        PhysicalCondition previousPysicalCondition = this.dailyRecordInputModel.getPreviousPhysicalCondition();

        if (Objects.nonNull(previousPysicalCondition)) {
            BigDecimal temperature = previousPysicalCondition.getBodyTemperatureEvening();
            if (Objects.nonNull(temperature)) {
                // 前日夜の体温
                String message = MessageFormat.format(
                        this.messageBundle.getString("contents.record.recordInput.msg.prevTemperature"), temperature);
                this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
            }
        }

        // セキュリティ警告
        if (this.loginInfo.getUserInfo().getSecurityLevel() < SecurityLevel.LEVEL1.getInt()) {
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    this.messageBundle.getString("contents.maint.settings.cutomSetting.msg.securityLevelWarning"),
                    null));
        }

        // 当日表示時の場合に前日の入力状態をチェック
        if (LocalDate.now().equals(this.today)) {
            this.checkPreviousDaysRecordEntered();
        }
    }

    /**
     * 前日の情報が未記入の場合にメッセージを表示する。
     */
    private void checkPreviousDaysRecordEntered() {
        PhysicalCondition previousPysicalCondition = this.dailyRecordInputModel.getPreviousPhysicalCondition();

        List<NotEnteredNotice> notEnteredNotices = this.notEnteredNoticeService.findActivatedNotEnteredNotices();

        List<String> messages = new ArrayList<>();

        // 未入力項目の判定
        for (NotEnteredNotice notEnteredNotice : notEnteredNotices) {
            boolean empty = false;

            if (notEnteredNotice.isChecked()) {
                if (Objects.isNull(previousPysicalCondition)) {
                    messages.add(this.messageBundle.getString(notEnteredNotice.getMessageId()));
                    empty = true;
                } else {
                    switch (NotEnteredNoticeTypeKey.valueOf(notEnteredNotice.getTypeKey())) {
                        case AWAKE_TIME:
                            empty = Objects.isNull(previousPysicalCondition.getAwakeTime()) ? true : false;
                            break;
                        case BED_TIME:
                            empty = Objects.isNull(previousPysicalCondition.getBedTime()) ? true : false;
                            break;
                        case TEMP_MORNING:
                            empty = Objects.isNull(previousPysicalCondition.getBodyTemperatureMorning()) ? true : false;
                            break;
                        case TEMP_EVENING:
                            empty = Objects.isNull(previousPysicalCondition.getBodyTemperatureEvening()) ? true : false;
                            break;
                        case OX_SAT_MORNING:
                            empty = Objects.isNull(previousPysicalCondition.getOxygenSaturationMorning()) ? true : false;
                            break;
                        case OX_SAT_EVENING:
                            empty = Objects.isNull(previousPysicalCondition.getOxygenSaturationEvening()) ? true : false;
                            break;
                        default:
                            break;
                    }
                }

            }

            if (empty) {
                messages.add(this.messageBundle.getString(notEnteredNotice.getMessageId()));
            }
        }

        if (0 < messages.size()) {
            // メッセージを設定
            String notEnteredParam = String.join(" / ", messages);
            String message = MessageFormat.format(
                    this.messageBundle.getString("contents.record.recordInput.msg.prevNotEntered"), notEnteredParam);
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
        }
    }

    @Override
    public void initModal() {
        this.logger.debug("initModal");
        this.commonConfirmModalBean.setVisible(false);
        this.commonConfirmModalBean.setMessage(StringUtils.EMPTY);
        this.commonConfirmModalController.setModalControllerListener(this);
    }

    @Override
    public String fireAction(ModalController modalAction, String commandTypeName, String buttonName) {
        return this.dispatchMethod(this.methodName);
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
        this.methodName = actionMethodName;
    }

}
