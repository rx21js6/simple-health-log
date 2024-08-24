
package jp.nauplius.app.shl.page.initial.bean;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class InitialSettingForm implements Serializable {
    @Inject
    private transient ResourceBundle messageBundle;

    @Getter
    @Setter
    @NotEmpty(message = "{initialSetting.loginid.notEmpty}")
    @Size(min = 5, max = 10)
    @Pattern(regexp = "~[a-z]+[a-z0-9_]+$", message = "{initialSetting.loginId.pattern}")
    private String loginId;

    @Getter
    @Setter
    @NotEmpty(message = "{initialSetting.name.notEmpty}")
    @Size(max = 10)
    private String name;

    @Getter
    @Setter
    // @NotEmpty
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "{initialSetting.mailAddress.email}")
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
