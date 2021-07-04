package jp.nauplius.app.shl.common.ui.bean;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import lombok.Data;

@Named
@Data
@ApplicationScoped
public class KeyIvHolder implements Serializable {
    private byte[] keyBytes;

    private byte[] ivBytes;
}
