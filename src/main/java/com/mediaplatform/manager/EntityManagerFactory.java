package com.mediaplatform.manager;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 10:15 PM
 */

@SessionScoped
public class EntityManagerFactory implements Serializable{
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Produces
    @Named(value = "appEm")
    public EntityManager getAppEntityManager() {
        return entityManager;
    }
}
