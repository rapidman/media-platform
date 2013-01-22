package com.mediaplatform.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * User: timur
 * Date: 1/23/13
 * Time: 12:11 AM
 */
@Embeddable
public class RateInfo {
    private Long userId;
    private boolean up;

    public RateInfo(){

    }

    public RateInfo(Long userId, boolean up) {
        this.userId = userId;
        this.up = up;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RateInfo rateInfo = (RateInfo) o;

        if (userId != null ? !userId.equals(rateInfo.userId) : rateInfo.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
