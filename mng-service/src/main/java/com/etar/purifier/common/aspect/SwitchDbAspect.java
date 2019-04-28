//package com.etar.purifier.common.aspect;
//
//import com.etar.purifier.utils.SwitchDbUtil;
//import io.shardingsphere.api.HintManager;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * @program: fangjia-sjdbc-read-write-springboot
// * @description:
// * @author: Gmq
// * @date: 2019-03-14 11:36
// **/
//@Component
//@Aspect
//public class SwitchDbAspect {
//
//    private final static Logger logger = LoggerFactory.getLogger(SwitchDbAspect.class);
//
//    @Pointcut("execution(public * com.etar.purifier.modules.*.controller.*.*(..))")
//    public void print() {
//
//    }
//
//    @Before("print()")
//    public void before(JoinPoint joinPoint) {
//        int dbSource = SwitchDbUtil.dbSource;
//        HintManager hintManager = HintManager.getInstance();
//        if (1 == dbSource) {
//            logger.info("强制路由主库=========================");
//            hintManager.setMasterRouteOnly();
//        } else {
//            logger.info("按默认配置路由算法路由从库===================");
//        }
//
//    }
//}

