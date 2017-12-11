package com.hyr.equipment.management.domain;

import java.io.Serializable;

/**
 * 用户登录扩展类
 *
 * @author huangyueran
 */
public class TbUserExt implements Serializable {

    private String user_login_token;

    private TbEqUserInfo user;

    public String getUser_login_token() {
        return user_login_token;
    }

    public void setUser_login_token(String user_login_token) {
        this.user_login_token = user_login_token;
    }

    public TbEqUserInfo getUser() {
        return user;
    }

    public void setUser(TbEqUserInfo user) {
        this.user = user;
    }

}
