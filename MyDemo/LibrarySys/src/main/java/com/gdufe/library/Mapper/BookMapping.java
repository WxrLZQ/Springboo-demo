package com.gdufe.library.Mapper;

import com.gdufe.library.daomain.Book;
import com.gdufe.library.daomain.Borrow;
import com.gdufe.library.daomain.StockBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapping {


    //1.获取所有图书
    @Select("select count(*) from book")
    public Integer getAllBook();

    //2.图书库存查询
    @Select("select a.*,count(a.book_id) book_num from stockbook a left join book b on a.book_id=b.book_id " +
            "where a.book_name=#{str} or a.book_author=#{str} or a.book_press=#{str} group by a.book_id")
    public List<StockBook> findBook(String str);

    //3.图书库存类型查询
    @Select(
            "<script>" +
                    "select a.*,count(a.book_id) book_num from stockbook a left join book b on a.book_id=b.book_id where 1=1" +
                    "<if test=\"author!=null and author!='' \">and book_author like '%${author}%'</if>" +
                    "<if test=\"press!=null and press!='' \" >and book_press like'%${press}%'</if>" +
                    "<if test=\"book_name!=null and book_name!=''\">and book_name like'%${book_name}%'</if>" +
                    "<if test='category!=null'>and book_category=#{category}</if>" +
                    "group by a.book_id" +
                    "</script>")
    public List<StockBook> getCategory(@Param("author")String author, @Param("press")String press, @Param("book_name")String book_name, @Param("category")Integer category);

    //4.添加图书库存
    @Insert("insert into stockbook values(#{book_id},#{book_name},#{book_category},#{book_press},#{book_author},#{book_price},#{book_pubtime},#{book_img})")
    public boolean addBook(StockBook stockBook);

    //5.修改图书库存信息
    @Update("update stockbook set book_name=#{book_name},book_category=#{book_category},book_press=#{book_press},book_author=#{book_author}," +
            "book_price=#{book_price},book_pubtime=#{book_pubtime},book_img=#{book_img} where book_id=#{book_id}")
    public boolean updateBook(StockBook stockBook);

    //6.获取图书库存的最大库存编号
    @Select("select book_id from stockbook order by book_id desc limit 1;")
    public String getBookId();



    //7.获取该图书的最大图书编号
    @Select("select book_bid from book where book_id=#{book_id} order by book_bid desc limit 1 ")
    public String getBookMaxBid(String book_id);

    //8.批量添加图书
    @Insert({"<script>" +
            "insert into book (book_bid,book_id) values" +
            "<foreach collection='books' item='book' index='index' separator=','>" +
            "(#{book.book_bid},#{book.book_id})" +
            "</foreach>" +
            "</script>"
    })
    public Boolean addNumBook(@Param("books") List<Book> books);

    //9.查询图书
    @Select("<script>" +
            "select * from book where book_id=#{book_id}" +
            "<if test='identity==0'>and book_state!=3</if>" +
            "</script>")
    public List<Book> getBook(String book_id,@Param("identity")Integer identity);

    //10.查询是否已存在该图书库存
    @Select("select * from stockbook where book_name=#{book_name} and book_author=#{book_author} and book_press=#{book_press}")
    public StockBook getStackBook(StockBook stockBook);

    //11.预约/借阅插入数据
    @Insert("insert into borrow(uid,bid,btime,bookname,username,state,operator,oid)" +
            "values(#{uid},#{bid},NOW(),#{bookname},#{username},#{state},#{operator},#{oid})")
    public Boolean appointment(Borrow borrow);

    //12.预约后借阅
    @Update("update borrow set operator=#{operator},oid=#{oid},state=2,btime=NOW(),return_time=ADDDATE(NOW(),60) where id=#{id}")
    public Boolean appointmentBorrow(Integer id,String oid,String operator);

    //13.查询借阅/预约/逾期/已归还
    @Select("<script>" +
            "select *,datediff(now(),a.btime)-60 day from borrow a,stockbook b,book c where a.bid=c.book_bid and b.book_id=c.book_id" +
            "<if test=\"state!=null and state!=2 \">and state=#{state}</if>" +
            "<if test=\"state==2\">and state in(4,2)</if>" +
            "<if test=\"user_id!=null and user_id!=''\">and uid=#{user_id}</if>" +
            "<if test=\"user_name!=null and user_name!=''\">and username=#{user_name}</if>" +
            "</script>")
    public List<Borrow> getBorrow(@Param("state")Integer state,@Param("user_id")String user_id,@Param("user_name")String user_name);

    //14.修改图书状态
    @Update("update book set book_state=#{book_state} where book_bid=#{bid}")
    public Boolean updateState(Integer book_state,String bid);

    //15.热门图书
    @Select("select a.*,count(a.book_id) sum from stockbook a,borrow b,book c " +
            "where datediff(now(),b.btime)<180 and a.book_id=c.book_id and b.bid=c.book_bid and b.state not in(7,8) group by a.book_id order by sum desc limit 10")
    public List<StockBook> getHot();

    //16.取消预约/归还/续借
    @Update("<script>" +
            "<if test='state==6'>update borrow set state=6,return_time=NOW() where id=#{id}</if>" +
            "<if test='state==4'>update borrow set state=4,return_time=ADDDATE(return_time,30) where id=#{id}</if>" +
            "<if test='state==3'>update borrow set state=3,return_time=NOW() where id=#{id}</if>" +
            "</script>")
    public Boolean  updateBorrowState(@Param("state")Integer state, Integer id);

    //17.更新逾期信息
    @Update("<script>" +
            "<if test='state==2'>update borrow set state=5 where datediff(now(),btime)>60 and state=2</if>" +
            "<if test='state==0'>update borrow set state=1,return_time=SUBDATE(return_time,1) where datediff(now(),btime)>10 and state=0</if>" +
            "<if test='state==4'>update borrow set state=5 where datediff(now(),btime)>90 and state=4</if>" +
            "</script>")
    public void updateOver(@Param("state") Integer state);

    //18.批量更新book表的图书状态    （用来处理批量缴纳罚款）
    @Update("<script>" +
            "<foreach collection='bids' item='bid' index='index' separator=';'>" +
            "update book set book_state=0 where book_bid=#{bid}" +
            "</foreach>" +
            "</script>")
    public Boolean updatebids(@Param("bids") String[] book_bids);

    //19.批量更新borrow表的的记录状态
    @Update("<script>" +
            "<foreach collection='ids' item='id' index='index'  separator=';'>" +
            "update borrow set state=3,return_time=NOW() where id=#{id}" +
            "</foreach>" +
            "</script>")
    public Boolean updateids(@Param("ids")Integer[] ids);


    //20.用户直接借阅图书
    @Insert("insert into borrow(uid,bid,btime,operator,oid,bookname,username,return_time,state) " +
            "values(#{uid},#{bid},NOW(),#{operator},#{oid},#{bookname},#{username},ADDDATE(NOW(),60),2)")
    public Boolean insertborrow(Borrow borrow);



    //21.出入库记录
    @Select("<script>" +
            "select bid,btime,return_time,state,bookname,book_press,book_author from borrow a,stockbook b,book c where a.bid=c.book_bid and b.book_id=c.book_id" +
            "<if test='isbr==1'>and state in(1,3,6,8) </if>" +
            "<if test='thetime==0 and isbr==0'>and DATEDIFF(NOW(),a.btime) between 0 and 30</if>" +
            "<if test='thetime==1 and isbr==0'>and DATEDIFF(NOW(),a.btime) between 0 and 90</if>" +
            "<if test='thetime==2 and isbr==0'>and DATEDIFF(NOW(),a.btime) between 0 and 180</if>" +
            "<if test='thetime==3 and isbr==0'>and DATEDIFF(NOW(),a.btime) between 0 and 365</if>" +
            "<if test='thetime==0 and isbr==1'>and DATEDIFF(NOW(),a.return_time) between 0 and 30</if>" +
            "<if test='thetime==1 and isbr==1'>and DATEDIFF(NOW(),a.return_time) between 0 and 90</if>" +
            "<if test='thetime==2 and isbr==1'>and DATEDIFF(NOW(),a.return_time) between 0 and 180</if>" +
            "<if test='thetime==3 and isbr==1'>and DATEDIFF(NOW(),a.return_time) between 0 and 365</if>" +
            "<if test=\"bid!=null and bid!=''\">and a.bid=#{bid}</if>" +
            "</script>")
    public List<Borrow> history(@Param("isbr") Integer isBR,@Param("thetime") Integer theTime,@Param("bid") String bid);

    //22.破损出库记录
    @Insert("<script>" +
            "<if test='state==7'>insert into borrow(bid,btime,state,bookname) values(#{bid},NOW(),#{state},#{bookname})</if>" +
            "<if test='state==8'>insert into borrow(bid,return_time,state,bookname) values(#{bid},NOW(),#{state},#{bookname})</if>" +
            "</script>")
    public Boolean destory(String bid,@Param("state") Integer state,String bookname);

}
