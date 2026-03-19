package com.supermarket.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        startTimeThreadLocal.set(System.currentTimeMillis());
        logger.info("请求开始: URL={}, Method={}, IP={}",
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        Long startTime = startTimeThreadLocal.get();
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("请求结束: URL={}, Method={}, Status={}, 耗时={}ms",
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus(),
                    duration);
            startTimeThreadLocal.remove();
        }

        if (ex != null) {
            logger.error("请求异常: URL={}, Method={}, 错误信息={}",
                    request.getRequestURI(),
                    request.getMethod(),
                    ex.getMessage(), ex);
        }
    }
}