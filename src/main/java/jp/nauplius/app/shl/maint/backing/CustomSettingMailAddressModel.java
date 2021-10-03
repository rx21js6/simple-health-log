package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class CustomSettingMailAddressModel implements Serializable {
    @Getter
    @Setter
    private String currentMailAddress;

    @Getter
    @Setter
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "{common.mailAddress.invalid}")
    private String mailAddress;
}
