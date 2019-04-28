package entity.common.entity;


import utils.ResultCode;

/**
 * 返回结果（结果码和信息）
 *
 * @author hzh
 * @date 2018/8/9
 */
public class Result {
    private Integer ret;
    private String msg;

    public Result ok() {
        ret = 0;
        msg = "SUCCESS";
        return this;
    }

    public Result error(ResultCode code) {
        this.ret = code.code();
        this.msg = code.message();
        return this;

    }

    public Result error(int code, String msg) {
        this.ret = code;
        this.msg = msg;
        return this;
    }

    public static boolean isOk(Result result) {
        return "SUCCESS".equals(result.getMsg()) && 0 == result.getRet();
    }

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
