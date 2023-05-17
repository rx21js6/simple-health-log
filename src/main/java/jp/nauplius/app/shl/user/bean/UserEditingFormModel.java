package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Named
@SessionScoped
@Data
public class UserEditingFormModel implements Serializable {
    private int id;

    @NotEmpty(message = "{userEditing.loginId.notEmpty}")
    @Size(min = 5, max = 10)
    @Pattern(regexp = "[a-z]+[a-z0-9_]+", message = "{userEditing.loginId.pattern}")
    private String loginId;

    @NotEmpty(message = "{userEditing.name.notEmpty}")
    @Size(max = 10)
    private String name;

    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "{userEditing.mailAddress.email}")
    private String mailAddress;

    private String password;

    private String passwordRetype;

    private boolean passwordChanged;
    private int roleId;

    private String zoneId;

}
