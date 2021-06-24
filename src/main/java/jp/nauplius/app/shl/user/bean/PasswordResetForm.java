package jp.nauplius.app.shl.user.bean;

import java.io.Serializable;

import javax.inject.Named;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Named
public class PasswordResetForm implements Serializable {
    @NotEmpty(message = "ログインIDを入力してください。")
    private String loginId;

    @NotEmpty(message = "氏名を入力してください。")
    private String name;

    @NotEmpty(message = "メールアドレスを入力してください。")
    private String mailAddress;
}
