package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.validation.constraints.Email;

import lombok.Data;
import lombok.NoArgsConstructor;

@Named
@SessionScoped
@Data
@NoArgsConstructor
public class CustomSettingMailAddressModel implements Serializable {
    private String currentMailAddress;

    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "{common.mailAddress.invalid}")
    private String mailAddress;
}
