package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class CustomSettingPasswordModel implements Serializable {
    @Getter
    @Setter
    @NotEmpty
    private String password;

    @Getter
    @Setter
    @NotEmpty
    private String passwordReenter;
}
