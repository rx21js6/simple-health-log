package jp.nauplius.app.shl.user.bean;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegisterUserRequest {
    /**
     * トークン
     */
    @NotEmpty
    private String token;
}
