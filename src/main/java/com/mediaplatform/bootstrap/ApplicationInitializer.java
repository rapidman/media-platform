/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mediaplatform.bootstrap;

import com.mediaplatform.model.Genre;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.User;
import com.mediaplatform.util.TwoTuple;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// @Stateless // can't use EJB since they are not yet available for lookup when initialized event is fired
@Alternative
public class ApplicationInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    @Inject
    private Logger log;

    private static List<User> users =
            Arrays.asList(
                    new User("admin", "admin", "admin@example.com", "admin", true),
                    new User("Shane Bryzak", "shane", "shane@example.com", "brisbane", false)
            );
    private static final TwoTuple<String, String>[] genres = new TwoTuple[]{
            new TwoTuple<String, String>("ИНФОРМАЦИЯ", "Статьи о конкретном термине или явлении."),
            new TwoTuple<String, String>("ПРОИСШЕСТВИЯ", "Происшествия на дороге, криминал и пр.."),
            new TwoTuple<String, String>("РУКОВОДСТВА (МАНУАЛЫ)", "Пошаговый план, который поможет совершить ряд действий, чтобы добиться нужного результата."),
            new TwoTuple<String, String>("ОБЗОРЫ", "Обзоры полезных онлайн-сервисов, партнёрских программ, гаджетов и т.д."),
            new TwoTuple<String, String>("НОВОСТИ", "Горячие новости."),
            new TwoTuple<String, String>("ИНТЕРВЬЮ", "Статьи оформленные в виде интервью."),
            new TwoTuple<String, String>("ИСТОРИИ", "Интересные истории"),
            new TwoTuple<String, String>("РЕЙТИНГИ", "Статьи о лучших постах недели в других блогах, лучших тематических книгах, программах и т.д."),
            new TwoTuple<String, String>("ПРОБЛЕМА – РЕШЕНИЕ", "Статьи предусматривающие постановку актуальной проблемы с последующим изложением её решения.."),
            new TwoTuple<String, String>("РАЗОБЛАЧЕНИЯ", "Разоблачение неправдивых новостей, мифов или действий конкретных личностей."),
            new TwoTuple<String, String>("ПЕРЕВОДЫ", "Авторские переводы полезных статей, опубликованых в зарубежных блогах."),
            new TwoTuple<String, String>("РАЗВЛЕЧЕНИЯ", "Развлекательные посты."),
            new TwoTuple<String, String>("ПРОЧЕЕ", "Все что не относится к перечисленным выше жанрам."),

    };


    public ApplicationInitializer() {
    }

    public void indextData() throws InterruptedException {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
    }

    /**
     * Import seed data when Seam Servlet fires an event notifying observers that the web application is being initialized.
     */
    public void importData(@Observes @Initialized WebApplication webapp) {
        log.info("Importing seed data for application " + webapp.getName());
        // use manual transaction control since this is a managed bean
        try {
            utx.begin();
            if(entityManager.createQuery("select g from Genre g").getResultList().size() == 0){
                // AS7-2045
//            entityManager.createQuery("delete from User").executeUpdate();
//
//            entityManager.createQuery("delete from Content").executeUpdate();
//            entityManager.createQuery("delete from Genre").executeUpdate();

                persist(users);
                for (TwoTuple<String, String> genre : genres) {
                    if ("ИСТОРИИ".equals(genre.getFirst())) {
                        createGenre(new Genre(genre.getFirst(), genre.getSecond()),
                                Arrays.asList(
                                        new TwoTuple<Genre, List<Content>>(
                                                new Genre("ЛИЧНЫЙ ОПЫТ", "Рассказ читателям о результатах собственных экспериментов."),
                                                new ArrayList<Content>()
                                        )
                                )
                        );
                    } else if ("ОБЗОРЫ".equals(genre.getFirst())) {
                        createGenre(new Genre(genre.getFirst(), genre.getSecond()),
                                Arrays.asList(
                                        new TwoTuple<Genre, List<Content>>(
                                                new Genre("ГАДЖЕТЫ", "Обзор гаджетов."),
                                                new ArrayList<Content>()
                                        )
                                )
                        );
                    } else if ("РАЗВЛЕЧЕНИЯ".equals(genre.getFirst())) {
                        createGenre(new Genre(genre.getFirst(), genre.getSecond()),
                                Arrays.asList(
                                        new TwoTuple<Genre, List<Content>>(
                                                new Genre("Юмор", "Анекдоты, шутки..."),
                                                new ArrayList<Content>()
                                        )
                                )
                        );
                    } else if ("ПРОЧЕЕ".equals(genre.getFirst())) {
                        createGenre(new Genre(genre.getFirst(), genre.getSecond()),
                                Arrays.asList(
                                        new TwoTuple<Genre, List<Content>>(
                                                new Genre("ВИДЕО", "Любительское видео"),
                                                new ArrayList<Content>()
                                        )
                                )
                        );
                    } else {
                        createGenre(new Genre(genre.getFirst(), genre.getSecond()),
                                new ArrayList<TwoTuple<Genre, List<Content>>>()
                        );
                    }
                }
            }
            indextData();
            utx.commit();
            log.info("Seed data successfully imported");
        } catch (Exception e) {
            log.error("Import failed. Seed data will not be available.", e);
            try {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    try {
                        utx.rollback();
                    } catch (Exception rbe) {
                        log.error("Error rolling back transaction", rbe);
                    }
                }
            } catch (Exception se) {
            }
        }

    }

    private void createGenre(Genre parent, List<TwoTuple<Genre, List<Content>>> tuples) {
        entityManager.persist(parent);
        for (TwoTuple<Genre, List<Content>> tuple : tuples) {
            Genre child = tuple.getFirst();
            entityManager.persist(child);
            List<Content> contentList = tuple.getSecond();
            for (Content content : contentList) {
                content.setGenre(child);
                entityManager.persist(content);
                child.getContentList().add(content);
            }
            parent.getChildren().add(child);
            child.setParent(parent);
        }

    }

    private void persist(List<?> entities) {
        for (Object e : entities) {
            persist(e);
        }
    }

    private void persist(Object entity) {
        // use a try-catch block here so we can capture identity
        // of entity that fails to persist
        try {
            entityManager.persist(entity);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new PersistenceException("Cannot persist invalid entity: " + entity, e);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error persisting entity: " + entity, e);
        }
    }
}
