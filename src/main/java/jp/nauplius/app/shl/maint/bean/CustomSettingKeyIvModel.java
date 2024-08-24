package jp.nauplius.app.shl.maint.bean;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@Data
@SessionScoped
public class CustomSettingKeyIvModel implements Serializable {
    private String key;
    private String iv;
}
