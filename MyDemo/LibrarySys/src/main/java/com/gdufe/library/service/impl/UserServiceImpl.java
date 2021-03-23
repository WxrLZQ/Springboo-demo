package com.gdufe.library.service.impl;

import com.gdufe.library.Mapper.UserMapping;
import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.Pager;
import com.gdufe.library.daomain.StockBook;
import com.gdufe.library.daomain.User;
import com.gdufe.library.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapping userMapping;

    //查询单个用户
    @Override
    public User findOne(String userid) {
        return userMapping.findOne(userid);
    }



    //用户登录
    @Override
    public User findUser(String user_id,String password,Integer id) {
        return userMapping.findUser(user_id,password,id);
    }

    //修改密码
    @Override
    public boolean updateUser(String user_id,String password) {
        return userMapping.updateUser(user_id,password);
    }

    //修改用户状态
    @Override
    public boolean updateStae(String userid, Integer state) {
        return userMapping.updateStae(userid,state);
    }

    ////根据身份码查询用户
    @Override
    public  Pager<User> findeByIdentity(Integer user_identity,String user_id,String user_name,Integer user_state,Integer currentpage) {
        Pager<User> pager=new Pager<User>();
        //分页  当前页数 ，显示条数
        PageHelper.startPage(currentpage,24);
        //获取当前查询的集合
        List<User> users = userMapping.findeByIdentity(user_identity, user_id, user_name, user_state);

        PageInfo<User> integerPageInfo = new PageInfo<>(users);
        //把分页结果集添加到分页对象中
        pager.setResult(integerPageInfo.getList());
        //把分页总数添加到分页对象中
        pager.setTotal(integerPageInfo.getTotal());
        return pager;
    }

    //查询逾期或即将逾期的消息
    @Override
    public List<Borrow> getOverAppoint(String user_id, Integer state) {
        return  userMapping.getOverAppoint(user_id,state);
    }

    //重置密码
    @Override
    public Boolean resetPassword(String user_id) {
        return userMapping.resetPassword(user_id);
    }

    //添加用户或者管理员
    @Override
    public Boolean addUser(User user) {
        return userMapping.addUser(user);
    }

    @Override
    public User isUser(User user) {
        return userMapping.isUser(user);
    }
}
