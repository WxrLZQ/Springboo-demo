package com.gdufe.library.daomain;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.List;


@Data
public class Pager<T>{
    //返回的总记录数
    private Long total;
    //返回的结果集
    private List<T> result;


}
