package entity.common.entity;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量操作接受id集合对象
 *
 * @author hzh
 * @date 2018/10/11
 */
public class BatchReqVo {
    @NotEmpty(message = "ID集合不能为空")
    private List<Integer> idList;

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }
}
