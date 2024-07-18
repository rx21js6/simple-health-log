package jp.nauplius.app.shl.page.login.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@SessionScoped
@Data
public class LoginFormModel implements Serializable {
    @Inject
    private LoginForm loginForm;

    private LoginResponse loginResponse;
}
