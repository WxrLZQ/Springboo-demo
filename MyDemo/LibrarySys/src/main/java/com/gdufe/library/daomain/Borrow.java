package com.gdufe.library.daomain;

import lombok.Data;

import java.util.Date;

@Data
public class Borrow {
    //记录编号
    private Integer id;
    //用户编号
    private String uid;
    //借阅的用户名称b
    private String username;
    //图书编号
    private String bid;
    //借阅的书名
    private String bookname;
    //图书出版社
    private String book_press;
    //图书作者
    private String book_author;
    //图书类别
    private Integer book_category;
    //借阅时间
    private Date btime;
    //规定的归还时间
    private Date return_time;
    //操作员
    private String operator;
    //操作员账号
    private String oid;
   //逾期状态
    private Integer state;
    //天数
    private Integer day;

}
