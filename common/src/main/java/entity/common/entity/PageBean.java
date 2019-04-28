package entity.common.entity;


import java.util.List;

/**
 * 分页对象
 *
 * @author hzh
 * @date 2018/8/9
 */
public class PageBean<T> {
    private Long itemCounts;
    private int curPage;
    private int pageSize;
    private List<T> list;

    public Long getItemCounts() {
        return itemCounts;
    }

    public void setItemCounts(Long itemCounts) {
        this.itemCounts = itemCounts;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
