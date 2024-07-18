package jp.nauplius.app.shl.user.bean;

import jakarta.enterprise.inject.Default;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
