package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.model.PhysicalCondition;
import jp.nauplius.app.shl.common.model.PhysicalConditionPK;
import jp.nauplius.app.shl.common.model.UserInfo;
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
import jp.nauplius.app.shl.page.login.bean.LoginResponse;
import jp.nauplius.app.shl.page.login.service.CookieService;
import jp.nauplius.app.shl.page.login.service.LoginService;
import jp.nauplius.app.shl.page.record.service.DailyRecordService;
import lombok.Getter;
import lombok.Setter;

/**
 * 日次入力画面コントローラ
 *
 */
@SessionScoped
@Named
public class DailyRecordController implements Serializable {
    @Inject
    private Logger logger;

    @Inject
    private FacesContext facesContext;

    @Inject
    @Getter
    @Setter
    private PhysicalCondition physicalCondition;

    @Inject
    private LoginService loginService;

    @Inject
    private DailyRecordService dailyRecordService;

    @Inject
    private CookieService cookieService;

    @Inject
    private LoginInfo loginInfo;

    @Inject
    @Getter
    @Setter
    private LoginForm loginForm;

    @Getter
    @Setter
    private LocalDate today;

    @PostConstruct
    public void postConstruct() {
        this.today = LocalDate.now();
    }

    public void init() {
        this.logger.info("DailyRecordController#init");

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
            this.today = LocalDate.now();
            this.physicalCondition = this.dailyRecordService.getRecord(this.today);

            if (this.physicalCondition == null) {
                this.physicalCondition = new PhysicalCondition();
                PhysicalConditionPK pk = new PhysicalConditionPK();
                pk.setId(null);
                String dateText = this.today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                pk.setRecordingDate(dateText);
                this.physicalCondition.setId(pk);
            }
        }

        this.logger.info("DailyRecordController#init complete");
    }

    public void loadRecord() {
        this.logger.info("DailyRecordController#loadRecord");
        this.physicalCondition = this.dailyRecordService.getRecord(today);
    }

    public String login() {
        if (Objects.isNull(this.loginInfo.getUserInfo())) {
            try {
                LoginResponse loginResponse = this.loginService.login(loginForm);
                this.cookieService.registerToken(this.facesContext, loginResponse.getUserToken().getToken());

            } catch (SimpleHealthLogException e) {
                facesContext.getExternalContext().getFlash().setKeepMessages(true);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "認証に失敗しました。: " + e.getMessage(), null));
            }
        }
        return null;
    }

    public String logout() {
        this.logger.info("DailyRecordController#logout");
        try {
            if (!Objects.isNull(this.loginInfo.getUserInfo())) {
                this.loginService.logout();
                this.cookieService.removeToken(this.facesContext);
            }
        } catch (SimpleHealthLogException e) {
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "ログアウトしました。", null));
        }

        return null;
    }

    public String register() {
        try {
            this.physicalCondition.getId().setId(this.loginInfo.getUserInfo().getId());
            this.physicalCondition.setUserInfo(this.loginInfo.getUserInfo());
            this.dailyRecordService.register(this.physicalCondition);

            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "登録しました。", null));
        } catch (SimpleHealthLogException e) {
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "認証に失敗しました。", null));
        }

        return null;
    }

    /**
     * 月次リスト表示
     *
     * @return
     */
    public String showMonthlyList() {
        return null;
    }

    /**
     * 前日表示
     *
     * @return null
     */
    public String loadYesterday() {
        this.today = this.today.minusDays(1);
        this.physicalCondition = this.dailyRecordService.getRecord(today);
        return null;
    }

    /**
     * 当日表示
     *
     * @return null
     */
    public String loadToday() {
        this.today = LocalDate.now();
        this.physicalCondition = this.dailyRecordService.getRecord(today);
        return null;
    }

    /**
     * 翌日表示
     *
     * @return null
     */
    public String loadTomorrow() {
        this.today = this.today.plusDays(1);
        this.physicalCondition = this.dailyRecordService.getRecord(today);
        return null;
    }
}
