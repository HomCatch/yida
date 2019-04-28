
package com.xiaohe.etar.miniprogram.common.validation;

/**
 * @Author wzq
 */
public class XException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int ret = 500;

    public XException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public XException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public XException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.ret = code;
    }

    public XException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.ret = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
