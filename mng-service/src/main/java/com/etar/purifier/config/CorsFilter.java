package com.etar.purifier.config;

import com.etar.purifier.modules.advertising.controller.AdvertisingController;
import entity.common.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * 跨域过滤器
 *
 * @author meng
 * @since 2016年6月19日
 */
@Component
public class CorsFilter implements Filter {
    //    private final List<String> allowedOrigins = Arrays.asList( "http://10.0.1.231:7001", "http://localhost:7001", "http://localhost:9192");
    //线上环境
//    private final List<String> allowedOrigins = Arrays.asList("http://iotsvr.zsxiaohe.com:7001", "http://iotsvr.zsxiaohe.com:7000", "https://iotsvr.zsxiaohe.com:443", "http://123.57.148.29:7001", "http://123.57.148.29:7000", "https://123.57.148.29:443");
    //测试环境
    private final List<String> allowedOrigins = Arrays.asList("http://iotsvr.he-live.com:8787", "http://iotsvr.he-live.com:8989", "http://iotsvr.he-live.com:8092","http://iotsvr.he-live.com:7001", "http://iotsvr.he-live.com:7000", "https://iotsvr.he-live.com:443", "http://10.0.1.223:7001", "http://39.108.158.31:8787");


    private static Logger log = LoggerFactory.getLogger(AdvertisingController.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException, IllegalStateException {


        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            // Access-Control-Allow-Origin
            String origin = request.getHeader("Origin");
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
            response.setHeader("Vary", "Origin");

            // Access-Control-Max-Age
            response.setHeader("Access-Control-Max-Age", "3600");

            // Access-Control-Allow-Credentials
            response.setHeader("Access-Control-Allow-Credentials", "true");

            // Access-Control-Allow-Methods
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");

            // Access-Control-Allow-Headers
            response.setHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Authorization," + "X-CSRF-TOKEN");

            if ("OPTIONS".equals(request.getMethod())) {
                response.setStatus(HttpStatus.OK.value());
                return;
            }

        }


        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
