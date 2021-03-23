package com.gdufe.library.daomain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class StockBook {
    //图书库存编号
    private String book_id;
    //图书名称
    private String book_name;
    //图书分类
    private Integer book_category;
    //图书出版社
    private String book_press;
    //图书作者
    private String book_author;
    //图书价格
    private Double book_price;
    //图书出版时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date book_pubtime;
    //图书封面
    private String book_img;
    //图书数量
    private Integer book_num;

}
