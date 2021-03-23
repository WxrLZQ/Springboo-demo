package com.gdufe.library.config;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class TokenProccessor {
    private TokenProccessor(){};

    private static final TokenProccessor instance = new TokenProccessor();



    public static TokenProccessor getInstance() {

        return instance;

    }



    /**

     * 生成Token

     * @return

     */

    public String makeToken() {

        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";

        try {
            //MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest md = MessageDigest.getInstance("md5");

            byte md5[] =  md.digest(token.getBytes());


            BASE64Encoder encoder = new BASE64Encoder();
            //encode方法使出 byte对象 自动生成String对象
            return encoder.encode(md5);

        } catch (NoSuchAlgorithmException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return null;

    }

}
