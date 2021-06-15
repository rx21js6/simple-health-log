package jp.nauplius.app.shl.user.bean;

import lombok.Data;

@Data
public class RegisterUserTemporarilyResponse {
    /**
     * 生成したトークン
     */
    private String token;
    /**
     *
     */
    private String warningMessage;
}
