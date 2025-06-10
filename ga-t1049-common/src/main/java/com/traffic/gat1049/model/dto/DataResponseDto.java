package com.traffic.gat1049.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 数据响应DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponseDto<T> extends BaseResponseDto {

    /**
     * 响应数据
     */
    @JsonProperty("data")
    private T data;

    /**
     * 数据总数
     */
    @JsonProperty("total")
    private Long total;

    public DataResponseDto() {
        super();
    }

    public DataResponseDto(String code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public DataResponseDto(T data) {
        super("0000", "成功");
        this.data = data;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "DataResponseDto{" +
                "data=" + data +
                ", total=" + total +
                "} " + super.toString();
    }
}
