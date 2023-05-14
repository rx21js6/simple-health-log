package jp.nauplius.app.shl.user.bean;

import javax.enterprise.inject.Default;
import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import jp.nauplius.app.shl.common.ui.exception.order.FirstOrder;
import jp.nauplius.app.shl.user.constants.UserRoleId;
import lombok.Data;

@Data
@GroupSequence({RegisterUserTemporarilyRequest.class, FirstOrder.class, Default.class})
public class RegisterUserTemporarilyRequest {
    @NotBlank(message = "{registerUserTemporarilyRequest.loginId.blank}", groups = FirstOrder.class)
    @Size(min = 4, max = 12)
    @Pattern(regexp = "^[a-z][a-z0-9]+", message = "{registerUserTemporarilyRequest.loginId.regexp}")
    private String loginId;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
    @NotBlank
    private String passwordReenter;
    @NotEmpty
    private String name;
    @NotBlank
    @Email
    private String mailAddress;
    // @NotEmpty
    private UserRoleId userRoleId;
    @NotBlank
    private String token;

    private boolean viaWebService;
}
