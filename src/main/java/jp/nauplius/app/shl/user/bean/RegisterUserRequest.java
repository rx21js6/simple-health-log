package jp.nauplius.app.shl.user.bean;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegisterUserRequest {
    /**
     * トークン
     */
    @NotEmpty
    private String token;
}
