package com.example.demo.service.imp;

import com.example.demo.service.RedisService;
import com.example.demo.utils.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String, String> redisToken;

    @Override
    public void generateToken(String userId) {
//        存储到redis并设置过期时间
        String token = UUID.randomUUID().toString();
//        redisToken.boundValueOps(userId).set(token, 72, TimeUnit.HOURS);

        RedisUtil.set(userId, token, (long) 1);
    }

    @Override
    public boolean checkToken(String userId, String token) {
        if (token != null && userId != null) {
            return token.equals(getToken(userId));
        }

        return false;
    }

    @Override
    public String getToken(String userId) {
        return (String) RedisUtil.get(userId);
    }

    @Override
    public void renewalToken(String userId) {
        String token = getToken(userId);
        RedisUtil.expire(token, (long) 1);
//        redisToken.boundValueOps(userId).set(token, 72, TimeUnit.HOURS);
    }

}
