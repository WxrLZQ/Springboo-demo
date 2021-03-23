package com.gdufe.library.service;


import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.Pager;
import com.gdufe.library.daomain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {

     //查询身份查询用户
     User findOne(String userid);

     //用户登录
     User findUser(String user_id,String password,Integer id);

     //修改密码
     boolean updateUser(String user_id,String password);

     //修改用户状态
     boolean updateStae(String userid,Integer state);

     //根据身份码查询用户
     Pager<User> findeByIdentity(Integer user_identity, String user_id, String user_name, Integer  user_state, Integer currentpage);

     //查询逾期或即将逾期的消息
     List<Borrow> getOverAppoint(String user_id, Integer state);

     //密码重置
     Boolean  resetPassword(String user_id);

     //添加用户
     Boolean addUser(User user);

     //判断是否存在该账号
     User isUser(User user);
}
