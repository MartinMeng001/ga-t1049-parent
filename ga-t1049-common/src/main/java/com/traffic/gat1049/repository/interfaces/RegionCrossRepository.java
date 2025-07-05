package com.traffic.gat1049.repository.interfaces;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traffic.gat1049.repository.entity.RegionCross;
import com.traffic.gat1049.repository.entity.RegionSubRegion;
import org.springframework.stereotype.Repository;
/**
 * 区域路口关联Repository
 */
@Repository
public interface RegionCrossRepository extends BaseMapper<RegionCross> {
}
