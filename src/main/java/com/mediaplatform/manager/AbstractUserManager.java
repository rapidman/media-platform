package com.mediaplatform.manager;

import com.mediaplatform.model.User;
import org.richfaces.component.SortOrder;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * User: timur
 * Date: 11/27/12
 * Time: 8:48 PM
 */
public abstract class AbstractUserManager extends AbstractManager{

    protected void update(User user) {
        appEm.merge(user);
    }

    public User findByUsername(String userName) {
        try {
            return (User) appEm.createQuery("select u from User u where u.username = :username").setParameter("username", userName).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> getUsers() {
        if (appCacheBean.getAllUsers() == null || appCacheBean.getAllUsers().size() == 0) {
            appCacheBean.setAllUsers(findUsers(SortOrder.ascending, null, null));
        }
        return appCacheBean.getAllUsers();
    }

    public List<User> getTopUserList(){
        if(appCacheBean.getTopUsers() == null || appCacheBean.getTopUsers().size() == 0){
            List topUsers = findUsers(null, SortOrder.descending, null);
            appCacheBean.setTopUsers(topUsers);
        }
        return appCacheBean.getTopUsers();
    }

    protected List<User> findUsers(SortOrder nameOrder, SortOrder contentCount, SortOrder rateOrder) {
        StringBuffer sb = new StringBuffer();
        boolean hasOrder = setOrder("name", nameOrder, sb);
        if(hasOrder && hasOrder(contentCount)) sb.append(",");
        setOrder("u.contents.size", contentCount, sb);
        String orderBy = sb.length() > 0 ? " order by " + sb.toString() : "";
        List<User> result = appEm.createQuery("select u from User u where u.admin = false " + orderBy).getResultList();
        for(User u:result){
            u.getContents().size();
        }
        return result;
    }

    private boolean setOrder(String field, SortOrder order, StringBuffer sb) {
        if(hasOrder(order)){
            sb.append(field + (SortOrder.ascending == order ? " asc " : " desc "));
            return true;
        }
        return false;
    }

    private boolean hasOrder(SortOrder order) {
        return order != null && SortOrder.unsorted != order;
    }

    public User getById(Long id) {
        return appEm.find(User.class, id);

    }
}
