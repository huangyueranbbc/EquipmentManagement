package com.hyr.equipment.management.domain;

import java.io.Serializable;
import java.util.Date;

public class TbEqUserEquipmentRecord  implements Serializable{
    private Long recordId;

    private String userStudentNos;

    private Long equipmentId;

    private String equipmentName;

    private String userNames;

    private String userClasses;

    private String room;

    private Integer userNums;

    private Integer equipmentStatus;

    private Date starttime;

    private Date endtime;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUserStudentNos() {
        return userStudentNos;
    }

    public void setUserStudentNos(String userStudentNos) {
        this.userStudentNos = userStudentNos == null ? null : userStudentNos.trim();
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName == null ? null : equipmentName.trim();
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames == null ? null : userNames.trim();
    }

    public String getUserClasses() {
        return userClasses;
    }

    public void setUserClasses(String userClasses) {
        this.userClasses = userClasses == null ? null : userClasses.trim();
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room == null ? null : room.trim();
    }

    public Integer getUserNums() {
        return userNums;
    }

    public void setUserNums(Integer userNums) {
        this.userNums = userNums;
    }

    public Integer getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(Integer equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }
}