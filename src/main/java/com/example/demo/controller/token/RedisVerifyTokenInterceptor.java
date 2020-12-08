package com.example.demo.controller.token;

import com.example.demo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Hashtable;

@Component
public class RedisVerifyTokenInterceptor implements HandlerInterceptor {

    @Autowired
    RedisService redisService;

    private volatile Hashtable<String, Integer> statusMap = new Hashtable<>();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        //执行之前
        String token = httpServletRequest.getHeader("X-Auth-Token");// 从 http 请求头中取出 token
        String userId = httpServletRequest.getParameter("userId");

        if (token != null) {
            Integer status = statusMap.get(userId);
            if (status != null && status == 1) {
                //正在执行中
                throw new TokenException();
            }
            statusMap.put(token, 1);
        }

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
                    if (redisService.checkToken(userId, token)) {
                        redisService.renewalToken(userId);  //续签
                        return true;
                    }
                }
                throw new TokenException();
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //执行中
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //执行之后
        String token = request.getHeader("X-Auth-Token");// 从 http 请求头中取出 token
        if (token != null) {
            statusMap.put(token, 2);
        }
    }
}
