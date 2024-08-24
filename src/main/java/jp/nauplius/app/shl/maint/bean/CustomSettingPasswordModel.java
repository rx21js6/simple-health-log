package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Named
@SessionScoped
@Data
public class CustomSettingPasswordModel implements Serializable {
    @NotEmpty
    private String password;

    @NotEmpty
    private String passwordReenter;
}
