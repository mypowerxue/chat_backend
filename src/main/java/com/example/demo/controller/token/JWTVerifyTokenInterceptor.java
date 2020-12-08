package com.example.demo.controller.token;

import com.example.demo.mapper.bean.TestUser;
import com.example.demo.service.RedisService;
import com.example.demo.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class JWTVerifyTokenInterceptor implements HandlerInterceptor {

    @Autowired
    TestUserService testUserService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        String token = httpServletRequest.getHeader("X-Auth-Token");// 从 http 请求头中取出 token

        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        //检查是否有PassToken注解 如果有就跳过Token验证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }

        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(VerifyToken.class)) {
            VerifyToken VerifyToken = method.getAnnotation(VerifyToken.class);
            if (VerifyToken.required()) {
                // 执行验证Token验证
                if (token != null && !token.isEmpty()) {
                    JwtToken jwt = JwtUtils.getJWT(token);
                    if (jwt != null) {
                        String account = jwt.getAccount();
                        TestUser testUser = testUserService.selectByAccount(account);
                        if (testUser != null) {
                            //执行验证Token验证
                            //是否重新签发
                            return testUser.getUserId().equals(jwt.getUserId());
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }
}
