package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 12:26 AM
 */

@Cacheable
@Entity
@Table(name = "user_message")
@Veto
public class UserMessage extends AbstractContent{
}
