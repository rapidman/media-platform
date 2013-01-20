package com.mediaplatform.manager;

import com.mediaplatform.model.Content;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.jsf.FacesUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<Content> contentList;

    public List<Content> getContentList() {
        return contentList;
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
            FullTextEntityManager fullTextEntityManager =
                    org.hibernate.search.jpa.Search.getFullTextEntityManager(appEm);
            // create native Lucene query unsing the query DSL
            // alternatively you can write the Lucene query using the Lucene query parser
            // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Content.class).get();
            org.apache.lucene.search.Query q = qb
                    .keyword()
                    .onFields("title", "description")
                    .matching(query)
                    .createQuery();
            // wrap Lucene query in a javax.persistence.Query
            javax.persistence.Query persistenceQuery =
                    fullTextEntityManager.createFullTextQuery(q, Content.class);
            // execute search
            contentList = persistenceQuery.getResultList();
        }
        if(contentList == null || contentList.isEmpty()){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String msg = "По запросу '" + query + "' ничего не найдено.";
//            messages.info(msg + "1");
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
//            FacesUtil.saveMessages(facesContext);
        }
    }

    private void refresh() {
        contentList = null;
    }

    public void formSearch(){
        search(query);
    }
}
