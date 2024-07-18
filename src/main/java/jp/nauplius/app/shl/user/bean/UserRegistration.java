package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import jp.nauplius.app.shl.user.constants.UserRoleId;
import lombok.Data;

/**
 * 利用者登録情報
 */
@Data
@Named
public class UserRegistration implements Serializable {
    @NotBlank
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordReenter;
    @NotEmpty
    private String name;
    @NotEmpty
    private String mailAddress;
    // @NotEmpty
    private UserRoleId userRoleId;
    @NotEmpty
    private String token;
}
