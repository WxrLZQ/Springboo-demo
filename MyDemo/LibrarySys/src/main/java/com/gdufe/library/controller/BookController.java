package com.gdufe.library.controller;


import com.gdufe.library.daomain.*;
import com.gdufe.library.service.BookService;
import com.gdufe.library.service.UserService;
import com.google.gson.Gson;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("library")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    Gson gson= new Gson();

    /**
     * 1.库存查询
     * @param book_author
     * @param book_press
     * @param book_name
     * @param book_category
     * @return
     */
    @GetMapping("book")
    public Pager<StockBook> getSearch(@RequestParam(name = "current_page",defaultValue = "1")Integer currentpage, String book_author, String book_press, String book_name, Integer book_category){
        System.out.println(book_author+book_press+book_name+book_category);
        System.out.println("我到了  1.库存查询");
        //当都不输入直接查询时查询所有
        if (book_author==""&&book_press==""&&book_name==""&&book_category==0){
            return bookService.getAllStockBook(currentpage);
        }else if (book_category==0){//当用户输入其中一个框时且查询全部分类时
            //把图书编号设为空 以防止查询出错
            book_category=null;
            System.out.println(book_category+"设置图书类型为空  1.库存查询");
            return bookService.getCategory(currentpage,book_author,book_press,book_name,book_category);
        }else {//当用户查询其他分类时
            return bookService.getCategory(currentpage,book_author,book_press,book_name,book_category);
        }
    }

    /**
     * 2.添加图书模板
     * @param stockBook 图书信息
     * @return
     */
    @PostMapping("book")
    public Boolean addStackBook(StockBook stockBook) {
        System.out.println(stockBook+"图书添加  2添加图书模板");
//        //获取上传路径
//        String path = "F:/项目/MyDemo/images/";
//        System.out.println(path);
//        //获取上传文件的名称
//        String name = file.getOriginalFilename();
//        //获取分隔符在字符串中的下标  获取的下标从0开始
//        int index = name.lastIndexOf(".");
//        //获取从当前下标开始之后的所有字符
//        String substring = file.getOriginalFilename().substring(index);
//        //把文件名称设置为唯一值，uuid并拼上文件后缀
//        String uuid= UUID.randomUUID().toString().replace("-","")+substring;
//        System.out.println(uuid);
//        //把该文件名设置为图书的图片封面
//        book.setBook_img(uuid);
//        try {
//            //把文件上传到指定路径
//            FileCopyUtils.copy(file.getInputStream(),new FileOutputStream(new File(path+uuid)));
//        } catch (Exception e) {
//            System.out.println("上传失败");
//            e.printStackTrace();
//        }
        //查询是否该图书库存已存在
        if(bookService.getStackBook(stockBook)!=null){
            return null;
        }else {
            //获取该图书库存中最大的编号
            String bookId = bookService.getBookId();
            //获取该编号的数字再加一
            String str = bookId.substring(1);
            int i = Integer.parseInt(str) + 1;
            System.out.println(str+"获取图书最大编号 2添加图书模板");
            //把生产的编号设置为该图书的库存编号
            stockBook.setBook_id("K"+i);

            //创建一个图书对象集合
            List<Book> books = new ArrayList<>();
            //创建图书对象
            Book book = new Book();
            //设置第一本书的编号 为当库存编号+10001
            String id=stockBook.getBook_id()+"100001";
            book.setBook_id(stockBook.getBook_id());
            book.setBook_bid(id);
            //生成插入时间并添加到图书对象中
            book.setEnter_time(new Timestamp(System.currentTimeMillis()));
            books.add(book);
            //添加图书
            if (bookService.addNumBook(books)){
                //添加入库记录
                bookService.destory(book.getBook_bid(),8,stockBook.getBook_name());
                //添加库存
                return bookService.addBook(stockBook);
            }
            return false;




        }

    }

    /**
     * 3.图书添加
     * @param num 添加数量
     * @param book_id 添加的图书库存id
     * @return
     */
    @PostMapping("book/{book_id}")
    public Boolean addBook(Integer num,@PathVariable("book_id")String book_id,String bookname){
        System.out.println("我来添加图书了 3.图书添加");
        //数字编号
        Long id;
        //图书编号
        String bid;
        //创建一个图书对象集合
        List<Book> books = new ArrayList<>();
        //获取该图书最大的图书编号
        String bookBid = bookService.getBookMaxBid(book_id);
        //当图书已存在库存时
        String str = bookBid.substring(1);
        System.out.println(str + "3.图书添加");
        //转化为数字编号
        id = Long.parseLong(str);

        //创建一个book变量
        Book book = null;
        for (int i=1;i<=num;i++){
            //每添加一本图书编号加一
            id+=1;
            //生产图书编号
            bid="K"+id;
            System.out.println(bid);
            //创建book对象
            book=new Book();
            //把新生产的图书编号添加到图书对象中
            book.setBook_bid(bid);
            //绑定当前图书库存编号
            book.setBook_id(book_id);
            //生成插入时间并添加到图书对象中
            book.setEnter_time(new Timestamp(System.currentTimeMillis()));
            System.out.println(book);
            books.add(book);
            //添加图书入库记录
            bookService.destory(book.getBook_bid(),8,bookname);
        }
//        System.out.println(books);
        return bookService.addNumBook(books);
    }

    /**
     * 4.图书查询
     * @param book_id
     * @return
     */
    @GetMapping("book/{book_id}")
    public List<Book> getBookStock(HttpServletRequest request,@PathVariable("book_id") String book_id){
        //获取登录用户中获取用户身份
        User role = (User) request.getSession().getAttribute("role");
        //获取用户身份
        Integer user_identity = role.getUser_identity();
        System.out.println(book_id+"4.图书查询");
        return bookService.getBook(book_id, user_identity);


    }

    /**
     * 5.更新图书库存信息
     * @param stockBook
     * @return
     */
    @PutMapping("book/{book_id}")
    public Boolean updateStockBook(StockBook stockBook){
        //打印图书信息
        System.out.println(stockBook);
        return bookService.updateBook(stockBook);
    }

    /**
     * 生产七牛云上传token
     * @return
     */
    @GetMapping("token")
    public String getToken(){
        //七牛云AK
        String ACCESS_KEY="Z_7eMJdtj_n4lrAdSs3zVuZ8rn4wZXu75b1gYJbC";
        //七牛云SK
        String SECRET_KEY="QIriVPlgNKoKdjU02q166-7IBPy3z9sQTMn5Ae7R";
        //空间名
        String bucket="light-lib";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        //获取当前空间的token
        String s = auth.uploadToken(bucket);
        //返回获取的token值
        return s;

    }

    /**
     * 6.图书预约
     * @param borrow 图书操作对象
     * @return
     */
    @PostMapping("appointment")
    public Boolean appointment(Borrow borrow){
        System.out.println(borrow+"6.图书预约");
        //先插入图书预约数据
        Boolean appointment = bookService.appointment(borrow);
        if (appointment){//插入成功则修改图书状态
            //修改图书状态为预约中
            bookService.updateState(2,borrow.getBid());
            return appointment;
        }
        return false;
    }

    /**
     * 7.预约后借阅
     * @param id 记录编号
     * @param user_id 操作员编号
     * @param user_name 操作员编号
     * @return
     */
    @PatchMapping("borrow/{id}")
    public Boolean appointment_borrow(@PathVariable("id") Integer id,String user_id,String user_name,String bid){
        System.out.println(id+user_id+user_name+"7.预约后借阅");

        //插入管理员账号和管理员姓名
        Boolean aBoolean = bookService.appointmentBorrow(id, user_id, user_name);
        if (aBoolean){
            //修改图书状态为借出
            bookService.updateState(1,bid);
            return aBoolean;
        }
        return false;


    }

    /**
     * 8.获取已借阅/逾期/预约的图书
     * @param user_id 图书编号
     * @param state  想要获取的图书状态
     * @param currentpage 页数 默认为1
     * @return
     */
    @GetMapping("borrow/{state}")
    public Pager<Borrow> getBorrow( @RequestParam(name = "current_page",defaultValue = "1")Integer currentpage,String user_id,@PathVariable("state") Integer state,String user_name){
        System.out.println(user_id+"======"+state+" 8.获取已借阅/逾期/预约的图书");
        return bookService.getBorrow(state,user_id,currentpage,user_name);
    }


    /**
     * 9.热门图书
     * @return
     */
    @GetMapping("hot")
    public List<StockBook> getHot(){
        return bookService.getHot();

    }


    /**
     * 10.取消图书预约/续借
     * @param id 记录编号
     * @param bid 图书编号
     * @param state 图书状态
     * @return
     */
    @PatchMapping("cancel/{id}")
    public Boolean cancelappointment(@PathVariable("id") Integer id,String bid,Integer state){
        System.out.println(id+"==========="+bid+"10.取消图书预约/续借");
        //先修改记录状态为取消预约
        if (state==6){
            //取消预约
            Boolean aBoolean = bookService.updateBorrowState(state,id);
            if (aBoolean){//取消成功则修改图书的状态为正常状态
                return bookService.updateState(0, bid);
            }
            return aBoolean;
        }else {//续借
            return bookService.updateBorrowState(state,id);
        }
    }

    /**
     * 11.图书总册
     * @return
     */
    @GetMapping("number")
    public Integer allBook(){
        return bookService.getAllBook();
    }

    /**
     * 12.损坏出库
     * @param book_bid 图书编号
     * @return
     */
    @PatchMapping("book/{book_bid}")
    public Boolean updateBookState(@PathVariable("book_bid") String book_bid,String bookname){
        System.out.println(book_bid+bookname);
        if (book_bid!=null&&bookname!=null){
            bookService.destory(book_bid,7,bookname);
            return bookService.updateState(3, book_bid);
        }
      return false;

    }

    /**
     * 13.缴纳罚款
     * @param book_bids  图书编号
     * @param ids 记录号
     * @return
     */
    @PatchMapping("book")
    public Boolean bookfine(@RequestParam String[] book_bids,@RequestParam Integer[] ids,String user_id){
        //批量修改借阅记录 和批量修改图书状态
        System.out.println(user_id);
        for (String n:
             book_bids) {
            System.out.println("图书名称"+n);
        }
        for (Integer n:
                ids) {
            System.out.println("记录id"+n);
        }
       if(bookService.updatebids(book_bids) && bookService.updateids(ids)){
           return userService.updateStae(user_id,0);
       }
       return false;
    }

    /**
     * 14.直接借阅
     * @param borrow
     * @return
     */
    @PutMapping("borrow")
    public Integer insertborrow(Borrow borrow) {
        System.out.println("直接借阅"+borrow);
        Pager<User> userPager = userService.findeByIdentity(0, borrow.getUid(), borrow.getUsername(), null, 1);
        if (userPager.getResult().size()>0){//当存在用户时进行数据插入
            if (bookService.insertborrow(borrow)) {//记录插入成功修改图书的状态
                bookService.updateState(1, borrow.getBid());
                return 0;
            }
        }else {//当用户匹配失败返回标识
            return 2;
        }
        return 1;
    }

    /**
     * 15.图书归还
     * @param id 借阅记录编号
     * @param book_bid 图书编号
     * @return
     */
    @PatchMapping("return")
    public Boolean returnbook(Integer id,String book_bid){
        //修改借阅记录为已归还
        if (bookService.updateBorrowState(3,id)){
            //修改图书状态为正常可借
            return bookService.updateState(0,book_bid);
        }
        return false;
    }

    /**
     * 16.出入库记录
     * @param isBR 出入库标识
     * @param theTime 时间
     * @param currentpage 当前页
     * @param bid 图书编号
     * @return
     */
    @GetMapping("history")
    public Pager<Borrow> history(Integer isBR,Integer theTime,@RequestParam(name = "current_page",defaultValue = "1")Integer currentpage,String bid){
        System.out.println("出入库记录"+isBR+"======="+theTime+"======"+currentpage);
        return bookService.history(isBR, theTime,currentpage,bid);
    }
}
