package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.NotEmpty;

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
