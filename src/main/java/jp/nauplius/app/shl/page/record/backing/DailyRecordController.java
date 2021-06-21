
package jp.nauplius.app.shl.page.record.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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
import jp.nauplius.app.shl.page.login.bean.LoginForm;
import jp.nauplius.app.shl.page.login.bean.LoginInfo;
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

    @Getter
    @Setter
    private String selectedDate;

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
        try {
            this.dailyRecordService.register();

            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "登録しました。", null));
        } catch (SimpleHealthLogException e) {
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "認証に失敗しました。", null));
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
     * @return null
     */
    public String loadYesterday() {
        this.today = this.today.minusDays(1);
        this.dailyRecordService.loadRecord(this.today);
        return null;
    }

    /**
     * 当日表示
     *
     * @return null
     */
    public String loadToday() {
        this.today = LocalDate.now();
        this.dailyRecordService.loadRecord(this.today);
        return null;
    }

    /**
     * 翌日表示
     *
     * @return null
     */
    public String loadTomorrow() {
        this.today = this.today.plusDays(1);
        this.dailyRecordService.loadRecord(today);
        return null;
    }
}
