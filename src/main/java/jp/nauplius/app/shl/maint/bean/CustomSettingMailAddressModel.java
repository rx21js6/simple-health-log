package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.Email;

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
