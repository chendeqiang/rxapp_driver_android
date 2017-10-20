package com.mxingo.driver.module.base.http;

import com.mxingo.driver.utils.TextUtil;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouwei on 16/9/24.
 */

public class SignUtil {

    public static String getSign(Map<String, Object> map, String version) {
        StringBuilder parameter = new StringBuilder();
        if (map != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                Object parameterObject = map.get(key);
                Object object = parameterObject == null ? "" : parameterObject;
                if (TextUtil.isEmpty(parameter.toString())) {
                    parameter.append(key + "=" +object);
                } else {
                    parameter.append("&" + key + "=" +object);
               }
            }
        }
        try {
            String sign = signReqData(MD5,parameter.toString(), Charset.forName("UTF-8"),version);
            return sign;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String SIGN_KEY = "07500o0wQt19000000l30U0P0XvgH093Y0XaA050620zZ50r0h7O1094qD0x";


   private static String byteArray2Hex(byte[] hash) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1)
                buf.append('0');
            buf.append(hex);
        }
        return buf.toString();
    }

    private static String messageDigest(String method, byte[] content, Charset charset) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        try {
            if (charset == null) {
                charset = UTF_8;
            }
            messageDigest = MessageDigest.getInstance(method);
            messageDigest.update(content);
            return formatDigest(messageDigest.digest());
        } finally {
            if (messageDigest != null) {
                messageDigest.reset();
            }
        }
    }

    public static String md5Digest(String content) throws NoSuchAlgorithmException {
        return messageDigest(MD5, content.getBytes(), UTF_8);
    }


    public static String sha1Digest(String content) throws NoSuchAlgorithmException {
        return messageDigest(SHA1, content.getBytes(), UTF_8);
    }

    private static String formatDigest(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(Integer.toHexString(b & 0xff).toUpperCase());
        }
        return buf.toString();
    }


    /**
     * <p>
     * 签名请求数据
     * </p>
     *
     * @param method
     * @param content
     * @param charset
     * @param version
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String signReqData(String method, String content, Charset charset, String version)
            throws NoSuchAlgorithmException {
        if (version == null || "".equals(version))
            return "";

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(method);
            md.update((content + version + SIGN_KEY).getBytes(charset));
            return byteArray2Hex(md.digest()).toLowerCase();
        } finally {
            md.reset();
        }

    }

}
