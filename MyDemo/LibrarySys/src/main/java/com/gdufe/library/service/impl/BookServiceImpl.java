package com.gdufe.library.service.impl;

import com.gdufe.library.Mapper.BookMapping;
import com.gdufe.library.daomain.Book;
import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.StockBook;
import com.gdufe.library.daomain.Pager;
import com.gdufe.library.service.BookService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("bookservice")
public class BookServiceImpl implements BookService {

    @Autowired
    BookMapping bookMapping;

    Pager<StockBook> pager=null;

    @Override
    public Integer getAllBook() {
        return bookMapping.getAllBook();
    }

    //获取所有图书
    @Override
    public Pager<StockBook> getAllStockBook(Integer currentpage) {
        pager=new Pager<StockBook>();
        //分页  当前页数 ，显示条数
        PageHelper.startPage(currentpage,24);
        //获取当前查询的所有
        List<StockBook> all = bookMapping.getCategory(null,null,null,null);
        PageInfo<StockBook> integerPageInfo = new PageInfo<>(all);
        //把分页结果集添加到分页对象中
        pager.setResult(integerPageInfo.getList());
        //把分页总数添加到分页对象中
        pager.setTotal(integerPageInfo.getTotal());
        return pager;
    }

    //图书查询
    @Override
    public List<StockBook> findBook(String str) {
        return bookMapping.findBook(str);
    }

    //图书类型查询
    @Override
    public Pager<StockBook> getCategory(Integer currentpage, String author, String press, String name, Integer id) {
        pager=new Pager<StockBook>();
        //分页  当前页数 ，显示条数
        PageHelper.startPage(currentpage,24);
        //获取当前查询的集合
        List<StockBook> category = bookMapping.getCategory(author, press, name, id);
        PageInfo<StockBook> integerPageInfo = new PageInfo<>(category);
        //把分页结果集添加到分页对象中
        pager.setResult(integerPageInfo.getList());
        //把分页总数添加到分页对象中
        pager.setTotal(integerPageInfo.getTotal());
        return pager;

    }

    //添加图书
    @Override
    public boolean addBook(StockBook stockBook) {
        return bookMapping.addBook(stockBook);
    }

    //修改图书状态
    @Override
    public Boolean updateState(Integer book_state, String bid) {
        return bookMapping.updateState(book_state,bid);
    }

    //修改图书信息
    @Override
    public boolean updateBook(StockBook stockBook) {
        return bookMapping.updateBook(stockBook);
    }

    //获取图书库存的最大库存编号
    @Override
    public String getBookId() {
        return bookMapping.getBookId();
    }

    //获取该图书的最大图书编号
    @Override
    public String getBookMaxBid(String book_id) {
        return bookMapping.getBookMaxBid(book_id);
    }

    //批量添加图书
    @Override
    public Boolean addNumBook(List<Book> books) {
        return bookMapping.addNumBook(books);
    }

    //图书查询
    @Override
    public List<Book> getBook(String book_id,Integer identity) {
        return bookMapping.getBook(book_id,identity);
    }

    //查询是否已存在该图书库存
    @Override
    public StockBook getStackBook(StockBook stockBook) {
        return bookMapping.getStackBook(stockBook);
    }

    //预约/插入数据
    @Override
    public Boolean appointment(Borrow borrow) {
        return bookMapping.appointment(borrow);
    }

    //预约后借阅
    @Override
    public Boolean appointmentBorrow(Integer id, String oid, String operator) {
        return bookMapping.appointmentBorrow(id,oid,operator);
    }

    //查询借阅/预约/逾期
    @Override
    public Pager<Borrow> getBorrow(Integer state, String user_id,Integer currentpage,String user_name) {
        Pager<Borrow> pager=new Pager<Borrow>();
        //分页  当前页数 ，显示条数
        PageHelper.startPage(currentpage,24);
        //获取当前查询的集合
        List<Borrow> borrow = bookMapping.getBorrow(state,user_id,user_name);
        PageInfo<Borrow> integerPageInfo = new PageInfo<>(borrow);
        //把分页结果集添加到分页对象中
        pager.setResult(integerPageInfo.getList());
        //把分页总数添加到分页对象中
        pager.setTotal(integerPageInfo.getTotal());
        return pager;
    }


    //查询热门图书
    @Override
    public List<StockBook> getHot() {
        return bookMapping.getHot();
    }

    //取消预约
    @Override
    public Boolean updateBorrowState(Integer state,Integer id) {
        return bookMapping.updateBorrowState(state,id);
    }

    //更新逾期信息
    @Override
    public void updateOver(Integer state) {
        bookMapping.updateOver(state);
    }

    //批量更新book表的图书状态
    @Override
    public Boolean updatebids(String[] book_bids) {
        return bookMapping.updatebids(book_bids);
    }

    //批量更新borrow表的的记录状态
    @Override
    public Boolean updateids(Integer[] ids) {
        return bookMapping.updateids(ids);
    }

    //用户直接借阅图书
    @Override
    public Boolean insertborrow(Borrow borrow) {
        return bookMapping.insertborrow(borrow);
    }

    //出入库记录
    @Override
    public Pager<Borrow> history(Integer isBR, Integer theTime,Integer currentpage,String bid) {
        Pager<Borrow> pager=new Pager<Borrow>();
        //分页  当前页数 ，显示条数
        PageHelper.startPage(currentpage,12);
        //获取当前查询的集合
        List<Borrow> history = bookMapping.history(isBR, theTime,bid);
        PageInfo<Borrow> integerPageInfo = new PageInfo<>(history);
        //把分页结果集添加到分页对象中
        pager.setResult(integerPageInfo.getList());
        //把分页总数添加到分页对象中
        pager.setTotal(integerPageInfo.getTotal());
        return pager;
    }

    //破损出库记录
    @Override
    public Boolean destory(String bid, Integer state,String bookname) {
        return bookMapping.destory(bid,state,bookname);
    }


}
