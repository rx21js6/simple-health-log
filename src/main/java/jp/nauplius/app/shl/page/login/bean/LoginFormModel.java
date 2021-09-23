package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Data;

@Named
@SessionScoped
@Data
public class LoginFormModel implements Serializable {
    @Inject
    private LoginForm loginForm;

    private LoginResponse loginResponse;
}
