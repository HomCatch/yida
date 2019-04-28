package com.etar.purifier.modules.cache;

public class CaptchaInfo {

    String id;
    String captcha;

    public CaptchaInfo(String id, String captcha){
        this.id =  id;
        this.captcha = captcha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
