package com.hyr.equipment.management.domain;

import java.io.Serializable;

public class TbEqUserInfo  implements Serializable{
    private Long userId;

    private String userStudentNo;

    private String userClass;

    private String userName;

    private String userPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserStudentNo() {
        return userStudentNo;
    }

    public void setUserStudentNo(String userStudentNo) {
        this.userStudentNo = userStudentNo == null ? null : userStudentNo.trim();
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass == null ? null : userClass.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }
}