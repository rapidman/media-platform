package org.jboss.seam.examples.booking.test;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mediaplatform.account.Authenticated;
import com.mediaplatform.model.User;

public class AuthenticatedUserProducer {
    @PersistenceContext
    EntityManager em;

    @Produces
    @Authenticated
    public User getRegisteredUser() {
        return em.find(User.class, "ike");
    }
}
