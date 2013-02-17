package com.mediaplatform.manager;

import com.mediaplatform.model.User;
import com.mediaplatform.security.Authenticated;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
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
    protected User currentUser;

    @Inject
    @Named(value = "appEm")
    protected EntityManager appEm;

    @Inject
    protected ConfigBean configBean;

    @Inject
    protected AppCacheBean appCacheBean;

}
