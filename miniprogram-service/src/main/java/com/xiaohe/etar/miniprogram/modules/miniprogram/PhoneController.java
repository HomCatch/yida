package com.xiaohe.etar.miniprogram.modules.miniprogram;

import com.alibaba.fastjson.JSONObject;
import com.xiaohe.etar.miniprogram.common.LogOperate;
import com.xiaohe.etar.miniprogram.common.validation.XException;
import com.xiaohe.etar.miniprogram.modules.users.UserFeignService;
import com.xiaohe.etar.miniprogram.modules.verificationCode.service.VerificationCodeService;
import com.xiaohe.etar.miniprogram.utils.ConstantUtil;
import com.xiaohe.etar.miniprogram.utils.DateUtil;
import com.xiaohe.etar.miniprogram.utils.RandomCode;
import com.xiaohe.etar.miniprogram.utils.SmsUtil;
import entity.common.entity.Result;
import entity.user.User;
import entity.verificationcode.VerificationCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utils.ResultCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 手机注册
 *
 * @author hzh
 * @date 2019/01/11
 */
@RestController
@RequestMapping(value = "yida/miniProgram/phone/")
@Validated
public class PhoneController {
    private static Logger log = LoggerFactory.getLogger(MiniProgramController.class);

    private final UserFeignService userService;
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public PhoneController(UserFeignService userService, VerificationCodeService verificationCodeService) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * 获取是否绑定手机号
     *
     * @param userId 用户手机
     */
    @GetMapping(value = "/bindingStatus")
    public Result phone(@NotNull(message = "用户id不能为空") @RequestParam(value = "userId") String userId) {
        Result result = new Result();
        if (ConstantUtil.NULL_STR.equals(userId)) {
            return result.error(ResultCode.USER_ID_NULL);
        }
        try {
            User user = userService.findById(Integer.valueOf(userId));
            if (user == null) {
                return result.error(ResultCode.USER_NOT_EXIST);
            }
            if (StringUtils.isBlank(user.getPhone())) {
                return result.error(ResultCode.USER_UNBIND_PHONE);
            }
            result.ok();
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 发送消息
     *
     * @param phone 用户手机
     */
    @GetMapping(value = "/sendSms")
    @LogOperate(description = "发送验证码")
    public Result sendSms(@NotBlank(message = "手机号码不能为空") @RequestParam(value = "phone") String phone) {
        Result result = new Result();
        try {
            User byPhone;
            try {
                byPhone = userService.findByPhone(phone);
            } catch (Exception e) {
                throw new XException("网络异常，查找用户失败，请稍后重试");
            }
            if (byPhone != null) {
                return result.error(ResultCode.USER_BIND_PHONE);
            }
            String nonceStr = RandomCode.getNonceStr();
            result = SmsUtil.sendMsg(phone, nonceStr);
            log.info(JSONObject.toJSONString(result));
            if (ConstantUtil.MSG_OK.equals(result.getMsg()) && 0 == result.getRet()) {
                VerificationCode vc = verificationCodeService.findByPhone(phone);
                if (vc == null) {
                    vc = new VerificationCode();
                    vc.setPhone(phone);
                }
                vc.setCreateTime(new Date());
                vc.setCode(nonceStr);
                verificationCodeService.save(vc);
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }

    /**
     * 验证注册码
     *
     * @param phone 用户手机
     */
    @GetMapping(value = "/validCode")
    @LogOperate(description = "验证注册码")
    public Result validCode(@NotBlank(message = "手机号不能为空") @RequestParam(value = "phone") String phone,
                            @NotBlank(message = "验证码不能为空") @RequestParam(value = "code") String code,
                            @NotNull(message = "用户id不能为空") @RequestParam(value = "userId") Integer userId) {
        Result result = new Result();
        try {
            VerificationCode verificationCode = verificationCodeService.findByPhone(phone);
            if (verificationCode == null) {
                return result.error(ResultCode.PHONE_CODE_IS_NULL);
            }
            if (verificationCode.getCode().equals(code)) {
                //保存手机号
                Date createTime = verificationCode.getCreateTime();
                //判断是否超时5分钟
                boolean isFiveMin = DateUtil.isInFiveMin(createTime, new Date());
                if (isFiveMin) {
                    User byId = null;
                    try {
                        byId = userService.findById(userId);
                    } catch (Exception e) {
                        throw new XException("网络异常，查找用户失败，请稍后重试");
                    }
                    if (byId != null) {
                        byId.setPhone(phone);
                        try {
                            userService.save(byId);
                        } catch (Exception e) {
                            throw new XException("网络异常，保存用户失败，请稍后重试");
                        }
                    }
                } else {
                    return result.error(ResultCode.PHONE_CODE_OUT_TIME);
                }
                result.ok();
            } else {
                return result.error(ResultCode.PHONE_CODE_FAIL);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return result.error(ResultCode.ERROR);
        }
        return result;
    }


}
