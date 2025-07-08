// ================================================================
// 3. 视图数据转换器接口
// ================================================================
package com.traffic.gat1049.data.converter.interfaces;

import java.util.Map;

/**
 * 视图数据转换器接口
 * 专门用于处理数据库视图返回的Map数据
 */
public interface ViewDataConverter<T> {

    /**
     * 从视图数据转换为指定类型对象
     */
    T fromViewData(Map<String, Object> viewData);
}