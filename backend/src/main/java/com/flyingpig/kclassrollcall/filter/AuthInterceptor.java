package com.flyingpig.kclassrollcall.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyingpig.kclassrollcall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // 在控制器方法执行之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenStr = request.getHeader("Authorization");

        // 不为空
        if (StringUtils.isBlank(tokenStr)) {
            return false;
        }
        try {
            // 判断是否是有效的token,如果是则存储到UserContext
            UserContext.setUser(JwtUtil.parseJwt(tokenStr).getSubject());
            System.out.println(UserContext.getUser());
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true; // 返回 true 继续处理请求，false 则中断请求
    }

    // 在控制器方法调用之后执行，但在视图渲染之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        UserContext.removeUser();
    }
}

