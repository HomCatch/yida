package entity.user;

import javax.validation.constraints.NotNull;

/**
 * 微信用户相关信息
 *
 * @author hzh
 * @date 2018/10/9
 */
public class WxUser {
    @NotNull(message = "用户id不能为空")
    private Integer userId;
    private String nickName;
    /**
     * 微信头像地址
     */
    private String avatarUrl;
    private String country;
    private String province;
    private String city;
    /**
     * 性别 1男性
     */
    private String gender;
    private String language;
    @NotNull(message = "用户openId不能为空")
    private String openId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
