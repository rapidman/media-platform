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
package com.mediaplatform.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.jboss.solder.core.Veto;

@Cacheable
@Entity
@Table(name = "platform_user", uniqueConstraints = @UniqueConstraint(columnNames={"username"}))
@Veto
@Indexed
public class User extends AbstractEntity {
    private String username;
    private String password;
    private String name;
    private String email;
    private UserInfo userInfo;
    private List<Content> contents;
    private FileEntry avatar;
    private boolean admin;
    private String description;
    private Date createDate = new Date();
    private Gender gender;
    //сообщения для этого юзера
    private List<UserMessage> userMessages;
    private BannedUser bannedUser;

    public User() {
        super(EntityType.USER);
    }

    public User(final String name, final String username, final String email) {
        this();
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public User(final String name, final String username, final String email, final String password, boolean admin) {
        this(name, username, email);
        this.password = password;
        this.admin = admin;
    }

    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    @NotNull
    @Size(min = 1, max = 100)
    @Boost(2)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

//    @NotNull
//    @Size(min = 5, max = 15)
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @NotNull
    @Size(min = 3, max = 15)
    @Pattern(regexp = "^\\w*$")
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

//    @NotNull
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Transient
    public String getEmailWithName() {
        return name + " <" + email + ">";
    }

    @OneToOne
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @OneToMany(mappedBy = "author")
    public List<Content> getContents() {
        if(contents == null){
            contents = new ArrayList<Content>();
        }
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public FileEntry getAvatar() {
        return avatar;
    }

    public void setAvatar(FileEntry avatar) {
        this.avatar = avatar;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Lob
    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    @Boost(3)
    @Type(type="org.hibernate.type.StringClobType")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.DATE)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Enumerated(EnumType.STRING)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @OneToMany
    public List<UserMessage> getUserMessages() {
        if(userMessages == null){
            userMessages = new ArrayList<UserMessage>();
        }
        return userMessages;
    }

    public void setUserMessages(List<UserMessage> userMessages) {
        this.userMessages = userMessages;
    }

    @OneToOne
    public BannedUser getBannedUser() {
        return bannedUser;
    }

    public void setBannedUser(BannedUser bannedUser) {
        this.bannedUser = bannedUser;
    }

    @Transient
    public int getPostCount(){
        int size = 0;
        for(Content content:getContents()){
            if(ModerationStatus.ALLOWED == content.getModerationStatus()){
                size++;
            }
        }
        return size;
    }

    @Transient
    public int getRate(){
        int rate = 0;
        for(Content content:getContents()){
            rate+=content.getRate();
        }
        return rate;
    }

    @Transient
    public String getBanMessage(){
        return bannedUser == null ? "" : bannedUser.getBanMessage();
    }

    @Override
    public String toString() {
        return "User(" + username + ")";
    }

}
