package com.wzh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wzh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author wzh
 * @date 2023年03月30日 14:09
 * Description:检查用户是否已经登录
 */                                        //拦截所有路径的求情
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求:{}", requestURI);

        //设置无需处理的请求
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //2 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3 如果不需要处理 直接放行
        if (check) {
            log.info("请求{}无需处理,直接放行", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4 判断登录状态 如果已经登录就放行
        HttpSession session = request.getSession();
        Object employee = session.getAttribute("employee");
        if (employee != null) {
            log.info("用户以登录,用户id{}", session.getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        //5 如果未登录 返回未登录结果 通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配: 检查本次请求是否需要放行
     *
     * @param urls       不需要过滤的路径
     * @param requestURI 本次请求的路径
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }
}
