package com.hyr.equipment.management.domain;

import java.io.Serializable;

public class TbEqAdminInfo implements Serializable{
    private Long adminId;

    private Long adminUsername;

    private String adminPassword;

    private String adminName;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(Long adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword == null ? null : adminPassword.trim();
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName == null ? null : adminName.trim();
    }
}