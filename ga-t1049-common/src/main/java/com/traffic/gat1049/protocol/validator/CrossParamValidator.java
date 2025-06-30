package com.traffic.gat1049.protocol.validator;

import com.traffic.gat1049.exception.ValidationException;
import com.traffic.gat1049.protocol.model.intersection.CrossParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CrossParam验证器
 * 根据GA/T 1049.2最新规范验证路口参数
 */
public class CrossParamValidator {

    private static final Pattern CROSS_ID_PATTERN = Pattern.compile("^\\d{6}80\\d{4}$");
    private static final Pattern CONFLICT_MATRIX_PATTERN = Pattern.compile("^[01]+$");

    /**
     * 验证CrossParam对象
     */
    public static void validate(CrossParam crossParam) throws ValidationException {
        if (crossParam == null) {
            throw new ValidationException("crossParam", "路口参数不能为空");
        }

        List<String> errors = new ArrayList<>();

        // 1. 验证路口编号
        validateCrossId(crossParam.getCrossId(), errors);

        // 2. 验证路口名称
        validateCrossName(crossParam.getCrossName(), errors);

        // 3. 验证路口形状和等级
        validateFeatureAndGrade(crossParam, errors);

        // 4. 验证必需的列表字段
        validateRequiredLists(crossParam, errors);

        // 5. 验证绿冲突矩阵
        validateGreenConflictMatrix(crossParam, errors);

        // 6. 验证坐标信息
        validateLocationInfo(crossParam, errors);

        // 7. 验证业务逻辑一致性
        validateBusinessLogic(crossParam, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("crossParam", String.join("; ", errors));
        }
    }

    /**
     * 验证路口编号
     */
    private static void validateCrossId(String crossId, List<String> errors) {
        if (crossId == null || crossId.trim().isEmpty()) {
            errors.add("路口编号不能为空");
            return;
        }

        if (crossId.length() > 50) {
            errors.add("路口编号长度不能超过50个字符");
        }

        // 验证格式：前6位机构代码 + "80" + 4位路口代码
        if (!CROSS_ID_PATTERN.matcher(crossId).matches()) {
            errors.add("路口编号格式不正确，应为：6位机构代码+80+4位路口代码");
        }
    }

    /**
     * 验证路口名称
     */
    private static void validateCrossName(String crossName, List<String> errors) {
        if (crossName == null || crossName.trim().isEmpty()) {
            errors.add("路口名称不能为空");
            return;
        }

        if (crossName.length() > 50) {
            errors.add("路口名称长度不能超过50个字符");
        }
    }

    /**
     * 验证路口形状和等级
     */
    private static void validateFeatureAndGrade(CrossParam crossParam, List<String> errors) {
        if (crossParam.getFeature() == null) {
            errors.add("路口形状不能为空");
        }

        if (crossParam.getGrade() == null) {
            errors.add("路口等级不能为空");
        }
    }

    /**
     * 验证必需的列表字段
     */
    private static void validateRequiredLists(CrossParam crossParam, List<String> errors) {
        // 车道序号列表必须至少有1个
        if (crossParam.getLaneNoList() == null || crossParam.getLaneNoList().isEmpty()) {
            errors.add("车道序号列表不能为空，至少包含1个车道序号");
        }

        // 信号灯组序号列表必须至少有1个
        if (crossParam.getLampGroupNoList() == null || crossParam.getLampGroupNoList().isEmpty()) {
            errors.add("信号灯组序号列表不能为空，至少包含1个灯组序号");
        }

        // 信号组序号列表必须至少有1个
        if (crossParam.getSignalGroupNoList() == null || crossParam.getSignalGroupNoList().isEmpty()) {
            errors.add("信号组序号列表不能为空，至少包含1个信号组序号");
        }

        // 阶段号列表必须至少有1个
        if (crossParam.getStageNoList() == null || crossParam.getStageNoList().isEmpty()) {
            errors.add("阶段号列表不能为空，至少包含1个阶段号");
        }

        // 配时方案序号列表必须至少有1个
        if (crossParam.getPlanNoList() == null || crossParam.getPlanNoList().isEmpty()) {
            errors.add("配时方案序号列表不能为空，至少包含1个配时方案序号");
        }

        // 日计划号列表必须至少有1个
        if (crossParam.getDayPlanNoList() == null || crossParam.getDayPlanNoList().isEmpty()) {
            errors.add("日计划号列表不能为空，至少包含1个日计划号");
        }

        // 调度号列表必须至少有1个
        if (crossParam.getScheduleNoList() == null || crossParam.getScheduleNoList().isEmpty()) {
            errors.add("调度号列表不能为空，至少包含1个调度号");
        }
    }

    /**
     * 验证绿冲突矩阵
     */
    private static void validateGreenConflictMatrix(CrossParam crossParam, List<String> errors) {
        String matrix = crossParam.getGreenConflictMatrix();
        List<String> signalGroups = crossParam.getSignalGroupNoList();

        if (matrix == null || matrix.trim().isEmpty()) {
            errors.add("绿冲突矩阵不能为空");
            return;
        }

        // 验证矩阵只包含0和1
        if (!CONFLICT_MATRIX_PATTERN.matcher(matrix).matches()) {
            errors.add("绿冲突矩阵只能包含字符0和1");
            return;
        }

        // 验证矩阵长度
        if (signalGroups != null && !signalGroups.isEmpty()) {
            int expectedLength = signalGroups.size() * signalGroups.size();
            if (matrix.length() != expectedLength) {
                errors.add(String.format("绿冲突矩阵长度应为%d（信号组数量的平方），实际为%d",
                        expectedLength, matrix.length()));
            }
        }

        // 验证矩阵对称性（冲突关系应该是对称的）
        if (signalGroups != null && !signalGroups.isEmpty()) {
            int size = signalGroups.size();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        char conflict1 = matrix.charAt(i * size + j);
                        char conflict2 = matrix.charAt(j * size + i);
                        if (conflict1 != conflict2) {
                            errors.add(String.format("绿冲突矩阵不对称：信号组%d与%d的冲突关系不一致", i+1, j+1));
                        }
                    }
                }
            }
        }

        // 验证对角线元素（自己与自己不应该冲突）
        if (signalGroups != null && !signalGroups.isEmpty()) {
            int size = signalGroups.size();
            for (int i = 0; i < size; i++) {
                char diagonalElement = matrix.charAt(i * size + i);
                if (diagonalElement != '0') {
                    errors.add(String.format("绿冲突矩阵对角线元素应为0：信号组%d不应与自己冲突", i+1));
                }
            }
        }
    }

    /**
     * 验证坐标信息
     */
    private static void validateLocationInfo(CrossParam crossParam, List<String> errors) {
        Double longitude = crossParam.getLongitude();
        Double latitude = crossParam.getLatitude();
        Integer altitude = crossParam.getAltitude();

        if (longitude == null) {
            errors.add("路口中心位置经度不能为空");
        } else {
            // 经度范围验证：-180 到 180
            if (longitude < -180.0 || longitude > 180.0) {
                errors.add("经度值应在-180到180度之间");
            }
        }

        if (latitude == null) {
            errors.add("路口中心位置纬度不能为空");
        } else {
            // 纬度范围验证：-90 到 90
            if (latitude < -90.0 || latitude > 90.0) {
                errors.add("纬度值应在-90到90度之间");
            }
        }

        if (altitude == null) {
            errors.add("路口位置海拔高度不能为空");
        } else {
            // 海拔高度合理性验证（地球表面合理范围）
            if (altitude < -500 || altitude > 9000) {
                errors.add("海拔高度值不合理，应在-500到9000米之间");
            }
        }
    }

    /**
     * 验证业务逻辑一致性
     */
    private static void validateBusinessLogic(CrossParam crossParam, List<String> errors) {
        // 验证人行横道序号不能与车道序号重复
        if (crossParam.getLaneNoList() != null && crossParam.getPedestrianNoList() != null) {
            for (Integer pedestrianNo : crossParam.getPedestrianNoList()) {
                if (crossParam.getLaneNoList().contains(pedestrianNo)) {
                    errors.add(String.format("人行横道序号%d与车道序号重复", pedestrianNo));
                }
            }
        }

        // 验证信号组数量与灯组数量的合理性
        if (crossParam.getSignalGroupNoList() != null && crossParam.getLampGroupNoList() != null) {
            if (crossParam.getSignalGroupNoList().size() > crossParam.getLampGroupNoList().size() * 2) {
                errors.add("信号组数量与灯组数量比例不合理，可能存在配置错误");
            }
        }

        // 验证路口形状与车道数量的合理性
        if (crossParam.getFeature() != null && crossParam.getLaneNoList() != null) {
            int laneCount = crossParam.getLaneNoList().size();
            switch (crossParam.getFeature()) {
                case T_Y_SHAPE:
                    if (laneCount < 3) {
                        errors.add("T形或Y形路口车道数量不应少于3条");
                    }
                    break;
                case CROSS:
                    if (laneCount < 4) {
                        errors.add("十字形路口车道数量不应少于4条");
                    }
                    break;
                case FIVE_WAY:
                    if (laneCount < 5) {
                        errors.add("五岔路口车道数量不应少于5条");
                    }
                    break;
                case SIX_WAY:
                    if (laneCount < 6) {
                        errors.add("六岔路口车道数量不应少于6条");
                    }
                    break;
            }
        }
    }

    /**
     * 快速验证方法，只验证关键字段
     */
    public static void quickValidate(CrossParam crossParam) throws ValidationException {
        if (crossParam == null) {
            throw new ValidationException("crossParam", "路口参数不能为空");
        }

        if (crossParam.getCrossId() == null || crossParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        if (crossParam.getCrossName() == null || crossParam.getCrossName().trim().isEmpty()) {
            throw new ValidationException("crossName", "路口名称不能为空");
        }

        if (crossParam.getFeature() == null) {
            throw new ValidationException("feature", "路口形状不能为空");
        }

        if (crossParam.getGrade() == null) {
            throw new ValidationException("grade", "路口等级不能为空");
        }
    }
}