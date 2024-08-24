package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Named
@SessionScoped
public class PasswordResetForm implements Serializable {
    @NotEmpty(message = "{passwordReset.loginId.notEmpty}")
    private String loginId;

    @NotEmpty(message = "{passwordReset.name.notEmpty}")
    private String name;

    @NotEmpty(message = "{passwordReset.mailAddress.notEmpty}")
    private String mailAddress;
}
