package com.example.demo.controller.token;

import com.google.gson.Gson;
import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtils {

    private static final long EXPIRE_TIME = (60 * 60 * 1000) * 2;  //Token过期时间  2小时
    private static final String JWT_ID = "1022";  //JWT的Id
    private static final String JWT_KEY = "JKKLJOoasdlfj1";    //JWT的秘钥
    private static final String JWT_AUTHOR = "user";    //JWT的签发者
    private static Gson gson = new Gson();

    /**
     * 签发JWT
     *
     * @return String
     */
    public static String createJWT(String account, Integer userId) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //保存用户数据
        JwtToken jwtToken = new JwtToken();
        jwtToken.setAccount(account);
        jwtToken.setUserId(userId);
        String jsonStr = gson.toJson(jwtToken);

        JwtBuilder builder = Jwts.builder()
                .setId(JWT_ID)
                .setSubject(jsonStr)    // 内容
                .setIssuer(JWT_AUTHOR)     // 签发者
                .setIssuedAt(now)      // 签发时间
                .setExpiration(new Date(nowMillis + EXPIRE_TIME)) // 过期时间
                .signWith(signatureAlgorithm, JWT_KEY); // 签名算法以及密匙

        return builder.compact();
    }


    /**
     * 获取用户数据
     *
     * @param jwtStr
     * @return
     */
    public static JwtToken getJWT(String jwtStr) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(JWT_KEY)
                    .parseClaimsJws(jwtStr)
                    .getBody();
            return gson.fromJson(body.getSubject(), JwtToken.class);
        } catch (Exception e) {
            return null;
        }
    }

}