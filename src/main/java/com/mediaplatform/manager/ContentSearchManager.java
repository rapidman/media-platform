package com.mediaplatform.manager;

import com.mediaplatform.model.AbstractEntity;
import com.mediaplatform.model.Comment;
import com.mediaplatform.model.Content;
import com.mediaplatform.security.User;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 1/6/13
 * Time: 3:35 PM
 */
@ViewScoped
@Named
public class ContentSearchManager extends AbstractManager {
    private String query = null;
    private List<AbstractEntity> searchResultList;

    public List<AbstractEntity> getSearchResultList() {
        return searchResultList;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void search(String query){
        refresh();
        this.query = query;
        if (StringUtils.isNotBlank(query)) {
            searchResultList = new ArrayList<AbstractEntity>();
            FullTextEntityManager fullTextEntityManager =
                    org.hibernate.search.jpa.Search.getFullTextEntityManager(appEm);
            // create native Lucene query unsing the query DSL
            // alternatively you can write the Lucene query using the Lucene query parser
            // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Content.class).get();
            org.apache.lucene.search.Query q = qb
                    .keyword()
                    .onFields("title", "description", "shortDescription")
                    .matching(query)
                    .createQuery();
            // wrap Lucene query in a javax.persistence.Query
            javax.persistence.Query persistenceQuery =
                    fullTextEntityManager.createFullTextQuery(q, Content.class);
            // execute search
            searchResultList.addAll(persistenceQuery.getResultList());

            qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(com.mediaplatform.model.User.class).get();
            q = qb.keyword()
                    .onFields("name")
                    .matching(query)
                    .createQuery();
            persistenceQuery = fullTextEntityManager.createFullTextQuery(q, com.mediaplatform.model.User.class);
            searchResultList.addAll(persistenceQuery.getResultList());

            qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Comment.class).get();
            q = qb.keyword()
                    .onFields("description")
                    .matching(query)
                    .createQuery();
            persistenceQuery = fullTextEntityManager.createFullTextQuery(q, Comment.class);
            searchResultList.addAll(persistenceQuery.getResultList());
        }
        if(searchResultList == null || searchResultList.isEmpty()){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String msg = "По запросу '" + query + "' ничего не найдено.";
//            messages.info(msg + "1");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
//            FacesUtil.saveMessages(facesContext);
        }
    }

    private void refresh() {
        searchResultList = null;
    }

    public void formSearch(){
        search(query);
    }
}
