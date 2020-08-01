package jp.nauplius.app.shl.common.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import jp.nauplius.app.shl.common.model.LoginUser;
import jp.nauplius.app.shl.ws.bean.GetUsersResponse;

@Named
public class DbConnectionService implements Serializable {
    @Inject
    private EntityManager em;

    public GetUsersResponse getUsers() {
        GetUsersResponse response = new GetUsersResponse();

        // クエリの生成
        TypedQuery<LoginUser> q = em.createQuery("SELECT lu FROM LoginUser lu",LoginUser.class);

        // 抽出
        response.setLoginUsers(q.getResultList());
        response.setCount(response.getLoginUsers().size());
        return response;
    }
}
