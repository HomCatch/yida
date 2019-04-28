package com.etar.purifier.config;


import com.etar.purifier.modules.advStatic.service.AdvStaticService;
import com.etar.purifier.modules.advertising.service.AdvertisingService;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.filterinfo.service.FilterInfoService;
import com.etar.purifier.modules.users.service.UserService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.BeansException;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * @Author wzq
 */
@Component
@EnableAutoConfiguration
public class ApplicationContextUtils implements ApplicationContextAware {

    public static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        applicationContext = arg0;
    }


    public static DeviceService getDeviceService() {
        return ApplicationContextUtils.applicationContext.getBean(DeviceService.class);
    }

    public static FilterInfoService getFilterInfoService() {
        return ApplicationContextUtils.applicationContext.getBean(FilterInfoService.class);
    }

    public static AdvStaticService getAdvStaticService() {
        return ApplicationContextUtils.applicationContext.getBean(AdvStaticService.class);
    }

    public static AdvertisingService getAdvertisingService() {
        return ApplicationContextUtils.applicationContext.getBean(AdvertisingService.class);
    }

    public static UserService getUserService() {
        return ApplicationContextUtils.applicationContext.getBean(UserService.class);
    }

    public static Cache getCache() {
        EhCacheCacheManager cacheCacheManager = ApplicationContextUtils.applicationContext.getBean(EhCacheCacheManager.class);
        CacheManager cacheManager = cacheCacheManager.getCacheManager();
        return cacheManager.getCache("captcha");
    }
}
