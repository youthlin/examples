package com.youthlin.demo.mvc.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 创建： youthlin.chen
 * 时间： 2017-08-19 18:01.
 */
@SuppressWarnings("WeakerAccess")
public class MD5Util {
    private static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignore) {
        }
        return "";
    }

    public static String getImgUrl(String email) {
        return "https://cn.gravatar.com/avatar/" + md5(email) + "?s=24";
    }
}
