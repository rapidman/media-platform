package com.mediaplatform.manager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
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
public class EntityManagerFactory implements Serializable {
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @RequestScoped
    @Produces
    @Named(value = "appEm")
    public EntityManager getAppEntityManager() {
        return entityManager;
    }
}
