package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Named
public class InitialSettingForm implements Serializable {
    @Getter
    @Setter
    @NotEmpty(message = "入力してください。")
    @Size(min = 5, max = 10)
    @Pattern(regexp = "~[a-z]+[a-z0-9_]+$", message = "英数小文字で入力してください。")
    private String loginId;

    @Getter
    @Setter
    @NotEmpty(message = "入力してください。")
    @Size(max = 10)
    private String name;

    @Getter
    @Setter
    // @NotEmpty
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Email: 形式が不正です。")
    // @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
    // message = "This is not a valid email")
    private String mailAddress;

    @Getter
    @Setter
    private String mailAddressReEnter;

    @Getter
    @Setter
    @NotEmpty
    private String password;

    @Getter
    @Setter
    @NotEmpty
    private String passwordReenter;

    public void validate(ComponentSystemEvent e) {
        if (!this.password.equals(this.passwordReenter)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "入力内容が不正です。", "テキスト・フィールドに正しい値が入力されていません。");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        // パスワードチェック
        /*
         * UIComponent component = e.getComponent(); if
         * (this.password.equals(this.getpasswordReenter())) { FacesContext facesContext
         * = FacesContext.getCurrentInstance();
         * facesContext.getExternalContext().getFlash().setKeepMessages(true);
         * FacesMessage message = new FacesMessage("パスワードが一致しません。");
         * message.setSeverity(FacesMessage.SEVERITY_ERROR);
         * facesContext.addMessage(component.getClientId(), message);
         * facesContext.renderResponse(); }
         */
        // メールアドレスチェック
    }
}
