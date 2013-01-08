package com.mediaplatform.manager;

import com.mediaplatform.model.Content;
import com.mediaplatform.util.ConversationUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
@Stateful
@ConversationScoped
@Named
public class ContentSearchManager extends AbstractManager {
    @Inject
    protected Conversation conversation;

    private String query = null;
    private List<Content> contentList;

    public List<Content> getContentList() {
        String localQuery = query == null ? "" : query;
        if (contentList == null && query != null) {
            FullTextEntityManager fullTextEntityManager =
                    org.hibernate.search.jpa.Search.getFullTextEntityManager(appEm);
            // create native Lucene query unsing the query DSL
            // alternatively you can write the Lucene query using the Lucene query parser
            // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Content.class).get();
            org.apache.lucene.search.Query query = qb
                    .keyword()
                    .onFields("title", "description")
                    .matching(localQuery)
                    .createQuery();
            // wrap Lucene query in a javax.persistence.Query
            javax.persistence.Query persistenceQuery =
                    fullTextEntityManager.createFullTextQuery(query, Content.class);
            // execute search
            contentList = persistenceQuery.getResultList();
        }
        return contentList;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void search(){
        ConversationUtils.safeBegin(conversation);
        contentList = null;
    }
}
