package com.traffic.gat1049.model.dto;

/**
 * 分页请求DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码，从1开始
     */
    @JsonProperty("pageNum")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @JsonProperty("pageSize")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    @JsonProperty("orderBy")
    private String orderBy;

    /**
     * 排序方向：ASC/DESC
     */
    @JsonProperty("orderDirection")
    private String orderDirection = "ASC";

    // Getters and Setters
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public String toString() {
        return "PageRequestDto{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirection='" + orderDirection + '\'' +
                '}';
    }
}
