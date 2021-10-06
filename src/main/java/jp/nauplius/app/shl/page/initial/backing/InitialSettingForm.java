
package jp.nauplius.app.shl.page.initial.backing;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Named
public class InitialSettingForm implements Serializable {
    @Inject
    private transient ResourceBundle messageBundle;

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
            String title = this.messageBundle.getString("common.label.inputValueInvalid");
            String message = this.messageBundle.getString("common.msg.passwordUnmatch");

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, title, message);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
}
