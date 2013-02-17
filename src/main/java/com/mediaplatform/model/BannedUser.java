package com.mediaplatform.model;

import org.apache.commons.lang.StringUtils;
import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: timur
 * Date: 2/15/13
 * Time: 1:17 PM
 */
@Cacheable
@Entity
@Table(name = "platform_banned_user")
@Veto
public class BannedUser extends AbstractEntity {

    private User user;
    private String reason;
    private Date bannedFrom;
    private Date bannedTo;

    public BannedUser(User user, String reason, Date bannedFrom, Date bannedTo) {
        this();
        this.user = user;
        this.reason = reason;
        this.bannedFrom = bannedFrom;
        this.bannedTo = bannedTo;
    }

    public BannedUser() {
        super(EntityType.BANNED_USER);
    }

    @OneToOne
    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getBannedFrom() {
        return bannedFrom;
    }

    public void setBannedFrom(Date bannedFrom) {
        this.bannedFrom = bannedFrom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getBannedTo() {
        return bannedTo;
    }

    public void setBannedTo(Date bannedTo) {
        this.bannedTo = bannedTo;
    }

    @Transient
    public String getBanMessage() {
        if(getBannedTo() == null) return "";
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sb.append("Забанен с ").append(sdf.format(getBannedFrom())).append(" по " + sdf.format(getBannedTo())).append(".");
        if(!StringUtils.isBlank(getReason())){
            sb.append(" Причина:" + getReason());
        }
        return sb.toString();
    }
}
