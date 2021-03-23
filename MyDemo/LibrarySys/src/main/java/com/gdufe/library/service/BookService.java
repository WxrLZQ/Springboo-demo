package com.gdufe.library.service;

import com.gdufe.library.daomain.Book;
import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.StockBook;
import com.gdufe.library.daomain.Pager;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface BookService {

    //获取所有图书总数
    Integer getAllBook();

    //获取所有库存图书
    Pager<StockBook> getAllStockBook(Integer currentpage);

    //图书模糊查询
    List<StockBook> findBook(String str);

    //图书类型查询
    Pager<StockBook> getCategory(Integer currentpage, String author, String press, String name, Integer id);

    //添加图书
    boolean addBook(StockBook stockBook);

    //修改图书状态
    Boolean updateState(Integer book_state,String bid);

    //修改图书信息
    boolean updateBook(StockBook stockBook);

    //获取图书库存的最大库存编号
    String  getBookId();

    //获取该图书的最大图书编号
    String getBookMaxBid(String book_id);

    //批量添加图书
    Boolean addNumBook( List<Book> books);

    //查询图书
    List<Book> getBook(String book_id,Integer identity);

    //查询是否已存在该图书库存
    StockBook getStackBook(StockBook stockBook);

    //预约/插入数据
    Boolean appointment(Borrow borrow);

    //预约后借阅
    Boolean appointmentBorrow(Integer id,String oid,String operator);

    //查询借阅/预约/逾期
    Pager<Borrow> getBorrow(Integer state,String user_id,Integer currentpage,String user_name);

    //热门图书
    List<StockBook> getHot();

    //修改状态
    Boolean  updateBorrowState(Integer state,Integer id);

    //更新逾期信息
    void updateOver(Integer state);

    //批量更新book表的图书状态
    Boolean updatebids(String[] book_bids);

    //批量更新borrow表的的记录状态
    public Boolean updateids(Integer[] ids);

    //用户直接借阅图书
    Boolean insertborrow(Borrow borrow);

    //出入库记录
    Pager<Borrow> history(Integer isBR,Integer theTime,Integer currentpage,String bid);

    //破损出库记录
    public Boolean destory(String bid,Integer state,String bookname);
}
