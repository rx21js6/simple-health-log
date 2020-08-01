package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.exception.DatabaseException;
import jp.nauplius.app.shl.common.model.KeyIv;
import jp.nauplius.app.shl.common.util.CipherUtil;
import lombok.Getter;

@ApplicationScoped
public class KeyIvHolderService implements Serializable {
    @Inject
    private EntityManager em;

    @Getter
    private byte[] keyBytes;

    @Getter
    private byte[] ivBytes;

    @Inject
    private CipherUtil cipherUtil;

    @Transactional
    public boolean isRegistered() {
        if (this.keyBytes != null) {
            // 取得済み
            return true;
        }

        KeyIv keyIv = em.find(KeyIv.class, 1);
        if (keyIv != null) {
            this.keyBytes = this.cipherUtil.base64StringToBytes(keyIv.getEncryptionKey());
            this.ivBytes = this.cipherUtil.base64StringToBytes(keyIv.getEncryptionIv());
            return true;
        }
        return false;
    }

    public void registerKeyIv() {
        try {
            this.keyBytes = this.cipherUtil.createKey();
            this.ivBytes = this.cipherUtil.createInitialVector();
            String key64 = this.cipherUtil.byteToBase64String(this.keyBytes);
            String iv64 = this.cipherUtil.byteToBase64String(this.ivBytes);

            KeyIv keyIv = new KeyIv();
            keyIv.setId(1);
            keyIv.setEncryptionKey(key64);
            keyIv.setEncryptionIv(iv64);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            keyIv.setCreatedDate(timestamp);
            keyIv.setModifiedDate(timestamp);

            em.persist(keyIv);
            em.merge(keyIv);
            em.flush();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DatabaseException(e);
        }

    }
}
