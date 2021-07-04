package jp.nauplius.app.shl.common.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import lombok.Getter;

@Named
public abstract class AbstractService implements Serializable {
    @Inject
    @Getter
    protected transient EntityManager entityManager;
}
