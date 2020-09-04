package com.akka.tcp.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="TB_USER")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SUSER_ID")
    private Long userId;
    @Column(name="SLOGN_ID")
    private String loginId;
    @Column(name="SUSER_NM")
    private String userName;
    @Column(name="SUSER_PW")
    private String userPassword;
    @Column(name="SINST_DT")
    private String insertedDateTime;
    @Column(name="SUPDT_DT")
    private String updatedDateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getInsertedDateTime() {
        return insertedDateTime;
    }

    public void setInsertedDateTime(String insertedDateTime) {
        this.insertedDateTime = insertedDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}