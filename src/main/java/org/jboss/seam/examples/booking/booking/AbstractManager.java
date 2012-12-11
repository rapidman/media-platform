package org.jboss.seam.examples.booking.booking;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * User: timur
 * Date: 11/27/12
 * Time: 8:48 PM
 */
public class AbstractManager implements Serializable{
    @Inject
    protected Logger log;

    @Inject
    protected Messages messages;

    @Inject
    protected Identity identity;

    @Inject
    @Authenticated
    protected Instance<User> currentUserInstance;

    @Inject
    protected EntityManager appEm;

    @Inject
    protected ConfigBean configBean;

}
