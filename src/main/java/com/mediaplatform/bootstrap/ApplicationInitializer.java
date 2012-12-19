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

import com.mediaplatform.model.Catalog;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.User;
import com.mediaplatform.util.TwoTuple;
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

/**
 * An alternative bean used to import seed data into the database when the application is being initialized.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
// @Stateless // can't use EJB since they are not yet available for lookup when initialized event is fired
@Alternative
public class ApplicationInitializer {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    @Inject
    private Logger log;

    private final List<User> users = new ArrayList<User>();

    public ApplicationInitializer() {
        users.addAll(Arrays.asList(
                new User("Shane Bryzak", "shane", "shane@example.com", "brisbane"),
                new User("Dan Allen", "dan", "dan@example.com", "laurel"),
                new User("Lincoln Baxter III", "lincoln", "lincoln@example.com", "charlotte"),
                new User("Jose Freitas", "jose", "jose.freitas@example.com", "brazil")));
    }

    /**
     * Import seed data when Seam Servlet fires an event notifying observers that the web application is being initialized.
     */
    public void importData(@Observes @Initialized WebApplication webapp) {
        log.info("Importing seed data for application " + webapp.getName());
        // use manual transaction control since this is a managed bean
        try {
            utx.begin();
            // AS7-2045
            entityManager.createQuery("delete from Booking").executeUpdate();
            entityManager.createQuery("delete from Hotel").executeUpdate();
            entityManager.createQuery("delete from User").executeUpdate();

            entityManager.createQuery("delete from Content").executeUpdate();
            entityManager.createQuery("delete from Catalog").executeUpdate();

            persist(users);

            createCatalog(new Catalog("Видео", "Категория Видео"),
                    Arrays.asList(
                            new TwoTuple<Catalog, List<Content>>(
                                    new Catalog("Документальные фильмы", "Подкатегория Документальные фильмы"),
                                    Arrays.asList(
                                            new Content("Анатомия власти", "DVD рип", null),
                                            new Content("Мировые войны", "DVD рип", null)
                                    )
                            ),
                            new TwoTuple<Catalog, List<Content>>(
                                    new Catalog("Мобильное видео", "Подкатегория Мобильное видео"),
                                    Arrays.asList(
                                            new Content("Видео с концерта Земфиры ", "DVD рип", null)
                                    )
                            )
                    )
            );
            createCatalog(new Catalog("Кино", "Категория Кино"),
                    Arrays.asList(
                            new TwoTuple<Catalog, List<Content>>(
                                    new Catalog("Зарубежные фильмы", "подкатегория Зарубежные фильмы"),
                                    Arrays.asList(new Content("Человек паук", "DVD рип", null))
                            )
                    )
            );
            createCatalog(new Catalog("Анимация", "Категория Анимация"),
                    Arrays.asList(
                            new TwoTuple<Catalog, List<Content>>(
                                    new Catalog("Зарубежные мультфильмы", "подкатегория Зарубежные мультфильмы"),
                                    Arrays.asList(
                                            new Content("Черепашки ниндзя1", "DVD рип", null),
                                            new Content("Черепашки ниндзя2", "DVD рип", null),
                                            new Content("Черепашки ниндзя3", "DVD рип", null),
                                            new Content("Черепашки ниндзя4", "DVD рип", null),
                                            new Content("Черепашки ниндзя5", "DVD рип", null),
                                            new Content("Черепашки ниндзя6", "DVD рип", null),
                                            new Content("Черепашки ниндзя7", "DVD рип", null),
                                            new Content("Черепашки ниндзя8", "DVD рип", null),
                                            new Content("Черепашки ниндзя9", "DVD рип", null),
                                            new Content("Черепашки ниндзя10", "DVD рип", null),
                                            new Content("Черепашки ниндзя11", "DVD рип", null),
                                            new Content("Черепашки ниндзя12", "DVD рип", null),
                                            new Content("Черепашки ниндзя13", "DVD рип", null),
                                            new Content("Черепашки ниндзя14", "DVD рип", null),
                                            new Content("Черепашки ниндзя15", "DVD рип", null),
                                            new Content("Черепашки ниндзя16", "DVD рип", null),
                                            new Content("Черепашки ниндзя17", "DVD рип", null)

                                    )
                            )
                    )
            );

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

    private void createCatalog(Catalog parent, List<TwoTuple<Catalog, List<Content>>> tuples) {
        entityManager.persist(parent);
        for(TwoTuple<Catalog, List<Content>> tuple:tuples){
            Catalog child = tuple.getFirst();
            entityManager.persist(child);
            List<Content> contentList = tuple.getSecond();
            for(Content content:contentList){
                content.setCatalog(child);
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
