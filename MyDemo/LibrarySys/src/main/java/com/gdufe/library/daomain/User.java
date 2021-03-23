package com.gdufe.library.daomain;

import lombok.Data;

@Data
public class User {
    //用户账号
    private String user_id;
    //用户名称
    private String user_name;
    //借阅证状态
    private Integer user_state;
    //账号身份
    private Integer user_identity;

}
