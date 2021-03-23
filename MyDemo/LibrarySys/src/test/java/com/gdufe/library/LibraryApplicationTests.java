package com.gdufe.library;

import com.gdufe.library.Mapper.BookMapping;
import com.gdufe.library.Mapper.UserMapping;
import com.gdufe.library.daomain.*;
import com.gdufe.library.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class LibraryApplicationTests {
    @Autowired
    UserMapping userMapping;

    @Autowired
    UserService userService;

    @Autowired
    BookMapping bookMapping;


    @Test
    void page(){
//        //使用pageHelper时后面应紧跟查询语句，不然分页会失效
//        PageHelper.startPage(2,3);
//        List<StockBook> all = bookMapping.getAllBook();
//        PageInfo<StockBook> integerPageInfo = new PageInfo<>(all);
////        List list = integerPageInfo.getList();
////        System.out.println(list);
////        Gson gson = new Gson();
////        String s = gson.toJson(integerPageInfo);
//        System.out.println(integerPageInfo);
////        User user = userMapping.findUser("123456", "123456", 0);
////        System.out.println(user);
    }
    @Test
    void User(){
//        User user = new User();
//        user.setUserid("1275096074");
//        user.setPassword("wxr19980304");
        //查询用户
//        User res = userMapping.findUser(user);
//        System.out.println(res);
//        boolean b = userMapping.updateUser(user);
//        System.out.println(b);
//        userMapping.loss("1275096074",1);
//        PageHelper.startPage(1,6);
//        List<StockBook> allStockBook = bookMapping.getAllBook();
//        PageInfo<StockBook> bookPageInfo = new PageInfo<>(allStockBook);
//        List<StockBook> list = bookPageInfo.getList();
//        System.out.println(bookPageInfo.getTotal());
//        System.out.println(bookPageInfo.getPageSize());
//        for (StockBook b:list) {
//            System.out.println(b);
//        }
//        System.out.println(bookPageInfo);
    }

    @Test
    void Book(){
        String str="中信出版集团";
//        List<Book> books = bookMapping.findBook(str);
//        for (Book book:books) {
//            System.out.println(book);
//        }
        List<StockBook> category = bookMapping.getCategory(null, str, "漫威之父斯坦李", null);
        for (StockBook stockBook :category) {
            System.out.println(stockBook);
        }
    }
    @Test
    void getVersion(){
//        System.out.println(SpringVersion.getVersion());
//        String version1 = SpringBootVersion.getVersion();
//        System.out.println(version1);
        //获取上传 路径
        List<Book> k100009 = bookMapping.getBook("K100002", 1);
        System.out.println(k100009);

    }

    @Test
    void setBook(){
        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setBook_bid("K10002100002");
        book.setBook_id("K10002");
        book.setEnter_time(new Timestamp(System.currentTimeMillis()));

        Book book1 = new Book();
        book1.setBook_bid("K10002100003");
        book1.setBook_id("K10002");
        book1.setEnter_time(new Timestamp(System.currentTimeMillis()));
        books.add(book);
        books.add(book1);

        Boolean aBoolean = bookMapping.addNumBook(books);
    }
   @Test
   void getStackBook(){
       StockBook stockBook = new StockBook();
       stockBook.setBook_author("沃尔特·艾萨克森");
       stockBook.setBook_name("史蒂夫 乔布斯传");
       StockBook stockBook1 = bookMapping.getStackBook(stockBook);
       System.out.println(stockBook1);
   }

   @Test
   void updateBook(){
        //图书更新
//       StackBook stackBook = new StackBook();
//       stackBook.setBook_name("京东技术解密");
//       stackBook.setBook_press("电子工业出版社");
//       stackBook.setBook_price(60.0);
//       stackBook.setBook_category(4);
//       stackBook.setBook_pubtime(new Date(System.currentTimeMillis()));
//       stackBook.setBook_img("Fltq7xQdk5phyqLEtJ9k9gC7Ln9q");
//       stackBook.setBook_author("京东研发体系");
//       stackBook.setBook_id("K100011");
//       boolean b = bookMapping.updateBook(stackBook);
       //测试用户查询
       User user = new User();
        user.setUser_id("127509074");
       List<User> users = userMapping.findeByIdentity(0, "123456", "", null);
       System.out.println(users);

       System.out.println(user);
//       System.out.println(stackBook);

   }
   //预约过期
   @Test
   void test1(){
       List<Borrow> overAppoint = userMapping.getOverAppoint("123456",5);
       if (overAppoint.size()>0){
           System.out.println("我不为空");
       }else {
           System.out.println("我为空");
       }
       System.out.println(overAppoint);
   }
   //预约
   @Test
   void test2(){
       Borrow borrow = new Borrow();
       borrow.setBid("K100001100002");
       borrow.setBookname("史蒂夫 乔布斯传");
       borrow.setUid("111111");
       borrow.setUsername("TIMI");

       Boolean appointment = bookMapping.appointment(borrow);
       System.out.println(appointment);
   }
   //预约后插入
   @Test
   void test3(){
       Boolean ab = bookMapping.appointmentBorrow(4, "222222", "管理员");
       System.out.println(ab);
   }
   //查询操作表
    @Test
    void test4(){
//        List<Borrow> borrows = bookMapping.selectBorrow(2,"123456");
//        for (Borrow b:borrows) {
//            System.out.println(b);
//        }
        boolean b = userMapping.updateStae("123456", 3);
        System.out.println(b);
    }
    //查询热门
    @Test
    void test5(){
        List<StockBook> hot = bookMapping.getHot();
        System.out.println(hot);
    }
    @Test
    //重置密码
    void test6(){
        Boolean aBoolean = userMapping.resetPassword("123456");
        System.out.println(aBoolean);
    }
    @Test
    void test7(){
        User user = new User();
        user.setUser_identity(0);
        user.setUser_id("111111");
        user.setUser_name("我是渣渣辉");
        Boolean aBoolean = userMapping.addUser(user);
        System.out.println(aBoolean);
    }

    @Test
    void  test8(){
        Borrow borrow = new Borrow();
        borrow.setUsername("TIMI");
        borrow.setUid("111111");
        borrow.setBookname("周星驰-做人如果没有梦想,跟咸鱼有什么分别");
        borrow.setBid("K100008100001");
        borrow.setOid("222222");
        borrow.setOperator("管理员");
        bookMapping.insertborrow(borrow);
    }
    @Test
    void test9(){
        String[] bb={"K100002100001","K100008100003"};
        bookMapping.updatebids(bb);
    }

    @Test
    void test10(){
        List<Borrow> history = bookMapping.history(0, 0,null);
        for (Borrow b:
             history) {
            System.out.println(b);
        }
    }

    @Test
    void  tes1(){
        User user = new User();
        user.setUser_identity(0);
        user.setUser_id("111111");
        User user1 = userMapping.isUser(user);
        System.out.println(user1);
    }
    @Test
    void  tes2(){
        bookMapping.destory("11111111",8,"嘻嘻嘻哈哈");
    }


}
