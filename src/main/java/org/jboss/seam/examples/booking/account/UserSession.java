package org.jboss.seam.examples.booking.account;

import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoginFailedEvent;

import javax.ejb.Stateful;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Model;

/**
 * User: timur
 * Date: 11/29/12
 * Time: 10:59 AM
 */
@Stateful
@Model
public class UserSession {
    private boolean loginFailed = false;

    public boolean isLoginFailed() {
        return loginFailed;
    }

    public void setLoginFailed(boolean loginFailed) {
        this.loginFailed = loginFailed;
    }

    public void observeLoginFailed(@Observes LoginFailedEvent event){
        loginFailed = true;
    }

    public void observeLoginOk(@Observes LoggedInEvent event){
        loginFailed = false;
    }

}
