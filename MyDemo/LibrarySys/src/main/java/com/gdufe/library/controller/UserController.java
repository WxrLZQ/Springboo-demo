package com.gdufe.library.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.List;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.gdufe.library.config.TokenProccessor;
import com.gdufe.library.config.VerifyCodefig;
import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.Pager;
import com.gdufe.library.daomain.User;
import com.gdufe.library.service.BookService;
import com.gdufe.library.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("library")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    BookService bookService;

    //转化Json格式
    Gson gson=new Gson();

    /**
     *1.生产验证码
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "VerifyCode")
    public void VerifyCode(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {

            int width=100;

            int height=38;

            BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

            //生成对应宽高的初始图片
            String randomText = VerifyCodefig.drawRandomText(width,height,verifyImg);
            System.out.println(randomText);


            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            request.getSession().setAttribute("verifyCode", randomText);

            String code = (String) request.getSession().getAttribute("verifyCode");
            System.out.println(code);
            response.setContentType("image/jpeg");//必须设置响应内容类型为图片，否则前台不识别
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            OutputStream os = response.getOutputStream(); //获取文件输出流

            ImageIO.write(verifyImg,"jpeg",os);//输出图片流

            os.flush();//刷新缓存
            os.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    /**
     * 2登录
     * @param httpServletRequest
     * @param id 用户身份
     * @param user_id   用户账号
     * @param password  用户密码
     * @param code  验证码
     * @return
     */
    @PostMapping("login/{id}")
    public String Login(HttpServletRequest httpServletRequest,@PathVariable("id") Integer id,
                        String user_id,String password,String code) {
        System.out.println(id+user_id+password);
        //获取session里的验证码
        String rightCode=(String)httpServletRequest.getSession().getAttribute("verifyCode");
        //创建List集合返回数据
        List list = new ArrayList();
        //验证验证码是否正确
        if (code.equals(rightCode)) {
//        System.out.println(user);
            //每次登录都更新数据库已经过期的预约和借阅/续借
            bookService.updateOver(2);
            bookService.updateOver(0);
            bookService.updateOver(4);
            //查询当前用户是否匹配
            User res = userService.findUser(user_id,password,id);
            System.out.println(res);
            if (res != null) {
                switch (res.getUser_state()) {
                    //状态为挂失
                    case 1:
                    //状态正常
                    case 0:
                        //获取生产的token
                        String s = TokenProccessor.getInstance().makeToken();
                        //把生产的token添加到session域中
                        httpServletRequest.getSession().setAttribute("token", s);
                        //把token添加到list集合中
                        list.add(s);
                        list.add(res);
                        //把查询的用户存储到session中
                        httpServletRequest.getSession().setAttribute("role",res);
                        //查询即将过期的预约
                        List<Borrow> overAppoint = userService.getOverAppoint(res.getUser_id(), 0);
                        httpServletRequest.getSession().setAttribute("overAppoint",overAppoint);
                        //查询即将逾期的借阅
                        List<Borrow> overBorrow = userService.getOverAppoint(res.getUser_id(), 2);
                        httpServletRequest.getSession().setAttribute("overBorrow",overBorrow);

                        if (overAppoint.size()>0 || overBorrow.size()>0){
                            list.add(true);
                        }else {
                            list.add(false);
                        }
                        return gson.toJson(list);
                    //状态为冻结
                    case 2:
                        return "4";
                    //账号已注销
                    case 3:
                        return "5";
                }
            } else {
                //密码或账号错误
                return "2";
            }
        } else {
            //验证码错误
            return "1";
        }
        return null;
    }


    /**
     * 3.修改密码
     * @param user_id   用户账号
     * @param password  用户旧密码
     * @param new_password   用户新密码
     * @param id    用户身份
     * @return
     */
    @PatchMapping("user")
    public Integer updatepassword( String user_id,String password,String new_password,Integer id){
        User res = userService.findUser(user_id,password,id);
        System.out.println(res);
        if (res!=null){
            //设置新密码
            //修改密码
            boolean b = userService.updateUser(user_id,new_password);
            System.out.println(b);
            return 0;//修改成功
        }else {
            //返回 1表示密码错误
            return 1;
        }
    }

    /**
     * 4.根据身份码查询用户
     * @param user_identity 用户身份码
     * @param user_id   用户账号
     * @param user_name 用户名称
     * @param user_state 用户状态
     * @param currentpage 当前页
     * @return
     */
    @GetMapping("user/{user_identity}")
    public Pager<User> selectId(@PathVariable("user_identity")Integer user_identity, String user_id, String user_name,
                                Integer user_state, @RequestParam(name = "current_page",defaultValue = "1")Integer currentpage){
        System.out.println(user_identity+user_id+user_name+user_state);
        return userService.findeByIdentity(user_identity,user_id,user_name,user_state,currentpage);
    }
    /**
     * 5.修改账号状态
     * @param user_id 用户账号
     * @param user_state 修改状态
     * @param password 用户密码
     * @param user_identity 用户身份
     * @return
     */
    @PatchMapping("user/{user_id}")
    public Boolean updateState(@PathVariable("user_id") String user_id,Integer user_state,String password,Integer user_identity){
        System.out.println(user_id+"===="+password+"===="+user_identity);
        if (user_identity==0){//判断是否为用户
            User user = userService.findUser(user_id, password, user_identity);//判断输入的密码是否正确
            if (user!=null){//密码正确 修改用户状态
                return userService.updateStae(user_id, user_state);
            }else {//密码错误 返回false
                return false;
            }
        }else {//管理员直接修改用户状态
            return userService.updateStae(user_id,user_state);
        }
    }

    /**
     * 6.消息通知
     * @param request
     * @return
     */
    @GetMapping("message")
    public String getMessage(HttpServletRequest request){
        //获取信息
        // 预约信息
        List<Borrow> overAppoint = (List<Borrow>) request.getSession().getAttribute("overAppoint");
        //借阅信息
        List<Borrow> overBorrow = (List<Borrow>) request.getSession().getAttribute("overBorrow");
        //创建集合
        List list= new ArrayList<>();
        list.add(overAppoint);
        list.add(overBorrow);

        return gson.toJson(list);

    }

    /**
     * 7.重置密码
     * @param user_id
     * @return
     */
    @PatchMapping("reset/{user_id}")
    public Boolean resetPassword(@PathVariable("user_id")String user_id){
        return userService.resetPassword(user_id);

    }

    /**
     * 8.注销用户或管理员
     * @param user_id 账号
     * @return
     */
    @PatchMapping("cancelaccount/{user_id}")
    public Boolean Cancelaccount(@PathVariable("user_id") String user_id){
        System.out.println(user_id);
        return userService.updateStae(user_id, 3);
    }

    /**
     * 9.添加用户或者管理员
     * @param user 用户对象
     * @return
     */
    @PostMapping("user")
    public Boolean addUser(User user){
        if (userService.isUser(user)==null){
            System.out.println(user);
            return userService.addUser(user);
        }else {
            return false;
        }

    }

}