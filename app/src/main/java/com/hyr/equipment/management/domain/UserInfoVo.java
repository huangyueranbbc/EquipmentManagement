package com.hyr.equipment.management.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @category 用户信息界面传输对象
 * @author Administrator
 *
 */
public class UserInfoVo implements Serializable{

	TbEqUserInfo user; // 用户信息

	List<TbEqUserEquipmentRecord> eqUserEquipmentRecords; // 用户当前设备使用记录信息

	public TbEqUserInfo getUser() {
		return user;
	}

	public void setUser(TbEqUserInfo user) {
		this.user = user;
	}

	public List<TbEqUserEquipmentRecord> getEqUserEquipmentRecords() {
		return eqUserEquipmentRecords;
	}

	public void setEqUserEquipmentRecords(List<TbEqUserEquipmentRecord> eqUserEquipmentRecords) {
		this.eqUserEquipmentRecords = eqUserEquipmentRecords;
	}

}
