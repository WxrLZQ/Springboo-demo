package com.gdufe.library.Mapper;

import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapping {


    //1.根据账号查询用户
    @Select("select * from user where user_id=#{user_id}")
    public User findOne(String userid);

    //2.根据身份码查询用户
    @Select("<script>" +
            "select * from user where user_identity=#{user_identity}" +
            "<if test=\"user_id!=null and user_id!='' \">and user_id=#{user_id}</if>" +
            "<if test=\"user_name!=null and user_name!='' \">and user_name=#{user_name}</if>" +
            "<if test=\"user_state!=null \">and user_state=#{user_state}</if>" +
            "</script>")
    public List<User> findeByIdentity(@Param("user_identity") Integer user_identity,@Param("user_id")String user_id
            ,@Param("user_name")String user_name,@Param("user_state")Integer user_state);

    //3.登录
    @Select("select * from user where user_id=#{user_id} and password=#{password} and user_identity=#{id}")
    public User findUser(String user_id,String password,Integer id);

    //4.修改密码
    @Update("update user set password=#{password} where user_id=#{user_id}")
    public boolean updateUser(String user_id,String password);

    //5.修改用户状态
    @Update("update user set user_state=#{state} where user_id=#{userid}")
    public boolean updateStae(String userid,Integer state);

    //6.查询即将过期的的预约或过期的借阅
    @Select("<script>" +
            "<if test='state==0'>select *,10-DATEDIFF(NOW(),btime) day from borrow where DATEDIFF(NOW(),btime)>6 and state=0</if>" +
            "<if test='state==2'>select *,60-DATEDIFF(NOW(),btime) day from borrow where DATEDIFF(NOW(),btime)>52 and state=2</if>" +
            "<if test=\"user_id!=null and user_id!=''\">and uid=#{user_id}</if>" +
            "</script>")
    public List<Borrow> getOverAppoint(String user_id,@Param("state") Integer state);

    //7.密码重置
    @Update("update user set password='111111' where user_id=#{user_id} ")
    public Boolean  resetPassword(String user_id);

    //8.添加用户
    @Insert("insert into user values(#{user_id},'111111',#{user_name},0,#{user_identity},now())")
    public Boolean addUser(User user);

    //9.判断是否存在该账号
    @Select("select * from user where user_id=#{user_id} and user_identity=#{user_identity}")
    public User isUser(User user);
}
