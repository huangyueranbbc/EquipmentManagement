package com.hyr.equipment.management.domain;

import java.io.Serializable;

public class TbEqEquipmentInfo  implements Serializable{
    private Long equipmentId;

    private String equipmentName;

    private String equipmentParam;

    private String equipmentDescribe;

    private String equipmentRoomName;

    private Integer equipmentStatus;

    private Integer equipmentUsedStatus;

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

    public String getEquipmentParam() {
        return equipmentParam;
    }

    public void setEquipmentParam(String equipmentParam) {
        this.equipmentParam = equipmentParam == null ? null : equipmentParam.trim();
    }

    public String getEquipmentDescribe() {
        return equipmentDescribe;
    }

    public void setEquipmentDescribe(String equipmentDescribe) {
        this.equipmentDescribe = equipmentDescribe == null ? null : equipmentDescribe.trim();
    }

    public String getEquipmentRoomName() {
        return equipmentRoomName;
    }

    public void setEquipmentRoomName(String equipmentRoomName) {
        this.equipmentRoomName = equipmentRoomName == null ? null : equipmentRoomName.trim();
    }

    public Integer getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(Integer equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public Integer getEquipmentUsedStatus() {
        return equipmentUsedStatus;
    }

    public void setEquipmentUsedStatus(Integer equipmentUsedStatus) {
        this.equipmentUsedStatus = equipmentUsedStatus;
    }
}