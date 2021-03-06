package com.mediaplatform.account;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.mediaplatform.model.User;
import com.mediaplatform.security.Authenticated;

import java.io.Serializable;


@Stateful
@SessionScoped
public class CurrentUserManager implements Serializable{
    private User currentUser;

    @Produces
    @Authenticated
    @Named("currentUser")
    public User getCurrentAccount() {
        return currentUser;
    }

    public void updateCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // Injecting HttpServletRequest instead of HttpSession as the latter conflicts with a Weld bean on GlassFish 3.0.1
    public void onLogin(@Observes @Authenticated User user, HttpServletRequest request) {
        currentUser = user;
        // reward authenticated users with a longer session
        // default is kept short to prevent search engines from driving up # of sessions
        request.getSession().setMaxInactiveInterval(3600);
    }
}
