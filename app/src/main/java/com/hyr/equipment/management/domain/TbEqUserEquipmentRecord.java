package com.hyr.equipment.management.domain;

import java.io.Serializable;
import java.util.Date;

public class TbEqUserEquipmentRecord implements Serializable {
	private Long recordId;

	private Long userId;

	private String userStudentNo;

	private Long equipmentId;

	private String equipmentName;

	private String userName;

	private Integer equipmentStatus;

	private Date starttime;

	private Date endtime;

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
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