package jp.nauplius.app.shl.maint.backing;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import lombok.Data;

@Named
@Data
@SessionScoped
public class CustomSettingKeyIvModel implements Serializable {
    private String key;
    private String iv;
}
