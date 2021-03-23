package com.gdufe.library.daomain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Book {
    //图书编号
    private String book_bid;
    //库存编号
    private String book_id;
    //图书状态
    private String book_state;
    //入库时间
    private Timestamp enter_time;
    //出库时间
    private Timestamp outer_time;
}
