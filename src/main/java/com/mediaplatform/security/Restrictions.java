package com.mediaplatform.security;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;

/**
 * @author Shane Bryzak
 */
public class Restrictions {
    public
    @Secures
    @Admin
    boolean isAdmin(Identity identity) {
        return identity.hasRole("admin", "USERS", "GROUP");
    }
}
