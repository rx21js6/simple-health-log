package jp.nauplius.app.shl.common.service;

import java.io.Serializable;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import jp.nauplius.app.shl.common.db.model.UserInfo;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

@Named
public class DbConnectionService implements Serializable {
    @Inject
    private EntityManager em;

    public GetUsersResponse getUsers() {
        GetUsersResponse response = new GetUsersResponse();

        // クエリの生成
        TypedQuery<UserInfo> q = em.createQuery("SELECT ui FROM UserInfo ui", UserInfo.class);

        // 抽出
        response.setUserInfos(q.getResultList());
        response.setCount(response.getUserInfos().size());
        return response;
    }
}
