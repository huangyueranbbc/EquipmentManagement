package com.hyr.equipment.management.domain;

import java.io.Serializable;

public class TbEqAdminInfo implements Serializable{
    private Long adminId;

    private String adminUsername;

    private String adminLab;

    private String adminName;

    private String adminPassword;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername == null ? null : adminUsername.trim();
    }

    public String getAdminLab() {
        return adminLab;
    }

    public void setAdminLab(String adminLab) {
        this.adminLab = adminLab == null ? null : adminLab.trim();
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName == null ? null : adminName.trim();
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword == null ? null : adminPassword.trim();
    }
}