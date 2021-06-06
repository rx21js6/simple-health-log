package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import javax.inject.Named;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Named
public class LoginForm implements Serializable {
    @Getter
    @Setter
    @NotEmpty(message = "入力してください。")
    // @Size(min = 5, max = 10)
    // @Pattern(regexp = "~[a-z]+[a-z0-9_]+$", message = "英数小文字で入力してください。")
    private String loginId;


    @Getter
    @Setter
    @NotEmpty(message = "入力してください。")
    private String password;
}
