package com.fullcontact.api.libs.fullcontact4j.entity.cardreader.contactinfo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Skiggz
 * Date: 10/31/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 4448757332560161860L;

    @SerializedName("urlString")
    private String urlString;

    @SerializedName("domain")
    private String domain;

    @SerializedName("userid")
    private String userId;

    @SerializedName("username")
    private String userName;

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
