package com.mediaplatform.manager;

import com.mediaplatform.manager.media.ContentManager;
import com.mediaplatform.model.Comment;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.UserMessage;
import com.mediaplatform.security.Restrictions;
import com.mediaplatform.security.User;

import javax.ejb.TransactionAttribute;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 1:05 AM
 */

@ViewScoped
@Named
public class UserMessageManager extends AbstractManager implements Serializable{
    private UserMessage currentMessage;
    private List<UserMessage> userMessages;
    @Inject
    private UserManager userManager;

    @Inject
    private AntiSamyBean antiSamyBean;

    @TransactionAttribute
    @User
    public void addMessage(){
        com.mediaplatform.model.User selectedUser = userManager.getSelectedUser();
        currentMessage.setDescription(antiSamyBean.cleanHtml(currentMessage.getDescription()));
        currentMessage.setTitle(HtmlTextHelper.htmlToPlain(HtmlTextHelper.truncateTitle(currentMessage.getTitle())));
        currentMessage.setAuthor(currentUser);
        appEm.persist(currentMessage);
        selectedUser.getUserMessages().add(currentMessage);
        userManager.update(selectedUser);
        messages = null;
        currentMessage = null;
    }

    @TransactionAttribute
    @User
    public void delete(Comment comment){
        comment = appEm.find(Comment.class, comment.getId());
        appEm.remove(comment);
        messages.info("Сообщение добавлено.");
        refresh();
    }

    private void refresh() {
        messages = null;
    }


    public UserMessage getCurrentMessage() {
        if(currentMessage == null){
            currentMessage = new UserMessage();
        }
        return currentMessage;
    }

    public void setCurrentMessage(UserMessage currentMessage) {
        this.currentMessage = currentMessage;
    }

    public List<UserMessage> getUserMessages() {
        if(userMessages == null){
            //если смотрим от лица хозяина аккаунта или админа
            if(Restrictions.checkAdmin(identity) || Restrictions.isOwner(currentUser, userManager.getSelectedUser())){
                userMessages = appEm.createQuery("select m from User u inner join u.userMessages m where u.id = :currentUserId").setParameter("currentUserId", currentUser.getId()).getResultList();
            }else{
                //от лица гостя аккаунта
                userMessages = appEm.createQuery("select m from User u inner join u.userMessages m where u.id = :selectedUserId and m.author.id = :currentUserId").
                        setParameter("selectedUserId", userManager.getSelectedUser().getId()).
                        setParameter("currentUserId", currentUser.getId()).getResultList();
            }
        }
        return userMessages;
    }

    public void setUserMessages(List<UserMessage> userMessages) {
        this.userMessages = userMessages;
    }
}
