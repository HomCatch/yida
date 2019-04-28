package com.etar.purifier.modules.emq;

import com.etar.purifier.modules.emqclient.service.EmqClientService;
import com.etar.purifier.utils.ConstantUtil;
import entity.emqclient.EmqClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * emq回调接口
 *
 * @author Gmq
 * @date 2019-01-25 10:13
 **/
@RestController
@RequestMapping("/emq")
public class Emqcallback {
    private static Logger log = LoggerFactory.getLogger(Emqcallback.class);

    private final EmqClientService emqClientService;

    @Autowired
    public Emqcallback(EmqClientService emqClientService) {
        this.emqClientService = emqClientService;
    }

    //TODO 转移到MQTT服务里
    @RequestMapping("/on_client_connected")
    public void callback(@RequestBody EmqClient emqClient) {
        log.info("接受到消息·" + emqClient.toString());
        String action = emqClient.getAction();
        if (action != null) {
            //检测上下线
            if (ConstantUtil.CLIENT_CONN.equals(action)) {
                emqClient.setAction("1");
                emqClient.setCreateTime(new Date());
                emqClientService.save(emqClient);
            } else if (ConstantUtil.CLIENT_DISCONN.equals(action)) {
                emqClient.setAction("0");
                emqClient.setCreateTime(new Date());
                emqClientService.save(emqClient);
            }
        }

    }
}
