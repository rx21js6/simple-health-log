package jp.nauplius.app.shl.common.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import jp.nauplius.app.shl.common.db.model.KeyIv;
import jp.nauplius.app.shl.common.exception.DatabaseException;
import jp.nauplius.app.shl.common.exception.SimpleHealthLogException;
import jp.nauplius.app.shl.common.ui.bean.KeyIvHolder;
import jp.nauplius.app.shl.common.util.CipherUtil;

/**
 * Key/IV管理
 */
@ApplicationScoped
public class KeyIvHolderService extends AbstractService {
    @Inject
    private KeyIvHolder keyIvHolder;

    @Inject
    private CipherUtil cipherUtil;

    @Inject
    private ConfigFileService configFileService;

    public byte[] getKeyBytes() {
        return this.keyIvHolder.getKeyBytes();
    }

    public byte[] getIvBytes() {
        return this.keyIvHolder.getIvBytes();
    }

    public String getSalt() {
        return this.keyIvHolder.getSalt();
    }

    public boolean isRegistered() {
        if (Objects.nonNull(this.keyIvHolder.getKeyBytes())) {
            // 取得済み
            return true;
        }

        KeyIv keyIv = this.entityManager.find(KeyIv.class, 1);
        if (Objects.nonNull(keyIv)) {
            this.keyIvHolder.setKeyBytes(this.cipherUtil.base64StringToBytes(keyIv.getEncryptionKey()));
            this.keyIvHolder.setIvBytes(this.cipherUtil.base64StringToBytes(keyIv.getEncryptionIv()));
            try {
                this.keyIvHolder.setSalt(this.configFileService.loadSalt());
            } catch (IOException e) {
                throw new SimpleHealthLogException(e);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public void registerKeyIv() {
        try {
            byte[] keyBytes = this.cipherUtil.createKey();
            byte[] ivBytes = this.cipherUtil.createInitialVector();
            String key64 = this.cipherUtil.byteToBase64String(keyBytes);
            String iv64 = this.cipherUtil.byteToBase64String(ivBytes);

            this.keyIvHolder.setKeyBytes(keyBytes);
            this.keyIvHolder.setIvBytes(ivBytes);
            this.keyIvHolder.setSalt(this.configFileService.loadSalt());

            KeyIv keyIv = new KeyIv();
            keyIv.setId(1);
            keyIv.setEncryptionKey(key64);
            keyIv.setEncryptionIv(iv64);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            keyIv.setCreatedDate(timestamp);
            keyIv.setModifiedDate(timestamp);

            this.entityManager.persist(keyIv);
            this.entityManager.merge(keyIv);
            this.entityManager.flush();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DatabaseException(e);
        } catch (IOException e) {
            throw new SimpleHealthLogException(e);
        }

    }

    public void clearKeyIvBytes() {
        this.keyIvHolder.setKeyBytes(null);
        this.keyIvHolder.setIvBytes(null);
    }
}
