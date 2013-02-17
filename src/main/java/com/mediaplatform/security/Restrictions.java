package com.mediaplatform.security;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;
import org.jboss.seam.social.Current;

public class Restrictions {
    public @Secures @Admin boolean isAdmin(Identity identity) {
        return checkAdmin(identity);
    }

    public static boolean checkAdmin(Identity identity) {
        return identity.hasRole("admin", "USERS", "GROUP");
    }

    public @Secures @User boolean isUser(Identity identity) {
        return identity.isLoggedIn();
    }

    public static boolean isAdminOrOwner(Identity identity, com.mediaplatform.model.User currentUser, com.mediaplatform.model.User editableUser) {
        return checkAdmin(identity) || isOwner(currentUser, editableUser);
    }

    public static boolean isOwner(com.mediaplatform.model.User currentUser, com.mediaplatform.model.User editableUser) {
        return currentUser.getUsername().equals(editableUser.getUsername());
    }
}
