package jp.nauplius.app.shl.common.service;

import java.io.Serializable;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import jp.nauplius.app.shl.common.db.model.UserInfo;
import lombok.Getter;

public abstract class AbstractService implements Serializable {
    @Inject
    @Getter
    protected transient EntityManager entityManager;

    /**
     * 管理者のメールアドレスを取得
     *
     * @return 管理者メールアドレス
     */
    protected String getAdminMailAddress() {
        TypedQuery<UserInfo> query = this.entityManager.createQuery(
                "SELECT ui FROM UserInfo ui WHERE ui.roleId = 0 AND ui.deleted = cast('false' as boolean)",
                UserInfo.class);
        List<UserInfo> results = query.getResultList();
        UserInfo userInfo = results.get(0);
        return userInfo.getMailAddress();
    }
}
