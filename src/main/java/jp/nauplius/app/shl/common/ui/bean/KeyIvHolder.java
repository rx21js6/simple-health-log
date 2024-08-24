package jp.nauplius.app.shl.common.ui.bean;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@Data
@ApplicationScoped
public class KeyIvHolder implements Serializable {
    private byte[] keyBytes;

    private byte[] ivBytes;

    private String salt;
}
