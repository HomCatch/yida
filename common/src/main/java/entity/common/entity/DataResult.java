package entity.common.entity;

/**
 * 返回结果（数据）
 *
 * @author hzh
 * @date 2018/8/9  sam.lei
 */
public class DataResult extends Result {

    private Object datas;

    public Object getDatas() {
        return datas;
    }

    public void setDatas(Object datas) {
        this.datas = datas;
    }
}
