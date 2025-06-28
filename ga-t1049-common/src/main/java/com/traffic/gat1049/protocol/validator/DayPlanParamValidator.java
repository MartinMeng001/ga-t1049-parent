package com.traffic.gat1049.protocol.validator;

import com.traffic.gat1049.protocol.model.signal.DayPlanParam;
import com.traffic.gat1049.protocol.model.signal.Period;
import com.traffic.gat1049.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 日计划参数验证工具类
 * 更新版本 - 符合最新协议定义
 */
public class DayPlanParamValidator {

    private static final Logger logger = LoggerFactory.getLogger(DayPlanParamValidator.class);

    /**
     * 验证日计划参数的完整性和正确性
     *
     * @param dayPlanParam 日计划参数
     * @throws ValidationException 验证失败时抛出异常
     */
    public static void validateDayPlanParam(DayPlanParam dayPlanParam) throws ValidationException {
        if (dayPlanParam == null) {
            throw new ValidationException("dayPlanParam", "日计划参数不能为空");
        }

        // 验证基本属性
        validateBasicProperties(dayPlanParam);

        // 验证时段列表
        validatePeriodList(dayPlanParam.getPeriodList());

        // 验证时段配置的合理性
        validatePeriodConfiguration(dayPlanParam.getPeriodList());

        logger.debug("日计划参数验证通过: crossId={}, dayPlanNo={}",
                dayPlanParam.getCrossId(), dayPlanParam.getDayPlanNo());
    }

    /**
     * 验证基本属性
     */
    private static void validateBasicProperties(DayPlanParam dayPlanParam) throws ValidationException {
        // 验证路口编号
        if (dayPlanParam.getCrossId() == null || dayPlanParam.getCrossId().trim().isEmpty()) {
            throw new ValidationException("crossId", "路口编号不能为空");
        }

        // 验证日计划号
        if (dayPlanParam.getDayPlanNo() == null) {
            throw new ValidationException("dayPlanNo", "日计划号不能为空");
        }

        if (dayPlanParam.getDayPlanNo() < 1 || dayPlanParam.getDayPlanNo() > 999) {
            throw new ValidationException("dayPlanNo", "日计划号必须在1-999之间");
        }

        // 验证日计划名称
        if (dayPlanParam.getDayPlanName() != null && dayPlanParam.getDayPlanName().length() > 50) {
            throw new ValidationException("dayPlanName", "日计划名称最大长度为50");
        }
    }

    /**
     * 验证时段列表
     */
    private static void validatePeriodList(List<Period> periodList) throws ValidationException {
        if (periodList == null || periodList.isEmpty()) {
            throw new ValidationException("periodList", "时段信息列表不能为空，必须包含至少1个时段信息");
        }

        // 验证每个时段的有效性
        for (int i = 0; i < periodList.size(); i++) {
            Period period = periodList.get(i);
            validatePeriod(period, i);
        }

        // 验证时段按开始时间升序排列
        validatePeriodOrder(periodList);
    }

    /**
     * 验证单个时段
     */
    private static void validatePeriod(Period period, int index) throws ValidationException {
        if (period == null) {
            throw new ValidationException("period[" + index + "]", "时段信息不能为空");
        }

        // 验证开始时间
        if (period.getStartTime() == null || period.getStartTime().trim().isEmpty()) {
            throw new ValidationException("period[" + index + "].startTime", "开始时间不能为空");
        }

        if (!Period.isValidTimeFormat(period.getStartTime())) {
            throw new ValidationException("period[" + index + "].startTime",
                    "开始时间格式必须为HH24:MM: " + period.getStartTime());
        }

        // 验证配时方案序号
        if (period.getPlanNo() == null) {
            throw new ValidationException("period[" + index + "].planNo", "配时方案序号不能为空");
        }

        if (period.getPlanNo() < 0 || period.getPlanNo() > 9999) {
            throw new ValidationException("period[" + index + "].planNo",
                    "配时方案序号必须在0-9999之间: " + period.getPlanNo());
        }

        // 验证控制方式
        if (period.getCtrlMode() == null || period.getCtrlMode().trim().isEmpty()) {
            throw new ValidationException("period[" + index + "].ctrlMode", "控制方式不能为空");
        }

        if (!Period.isValidCtrlMode(period.getCtrlMode())) {
            throw new ValidationException("period[" + index + "].ctrlMode",
                    "无效的控制方式: " + period.getCtrlMode());
        }

        // 验证方案号与控制方式的一致性
        validatePlanNoCtrlModeConsistency(period, index);
    }

    /**
     * 验证方案号与控制方式的一致性
     */
    private static void validatePlanNoCtrlModeConsistency(Period period, int index) throws ValidationException {
        String ctrlMode = period.getCtrlMode();
        Integer planNo = period.getPlanNo();

        // 特殊控制方式（关灯、全红、黄闪）时，方案号应该为0
        if (isSpecialCtrlMode(ctrlMode)) {
            if (planNo != 0) {
                logger.warn("特殊控制方式{}时建议方案号为0，当前为{}", ctrlMode, planNo);
                // 这里可以选择自动修正或抛出异常，目前只记录警告
            }
        } else {
            // 非特殊控制方式时，方案号应该大于0
            if (planNo == 0) {
                throw new ValidationException("period[" + index + "].planNo",
                        "控制方式" + ctrlMode + "需要有效的配时方案号（大于0）");
            }
        }
    }

    /**
     * 判断是否为特殊控制方式
     */
    private static boolean isSpecialCtrlMode(String ctrlMode) {
        return "11".equals(ctrlMode) || "12".equals(ctrlMode) || "13".equals(ctrlMode);
    }

    /**
     * 验证时段顺序
     */
    private static void validatePeriodOrder(List<Period> periodList) throws ValidationException {
        for (int i = 0; i < periodList.size() - 1; i++) {
            Period current = periodList.get(i);
            Period next = periodList.get(i + 1);

            if (Period.compareTime(current.getStartTime(), next.getStartTime()) >= 0) {
                throw new ValidationException("periodOrder",
                        String.format("时段必须按开始时间升序排列，时段%d(%s) >= 时段%d(%s)",
                                i + 1, current.getStartTime(), i + 2, next.getStartTime()));
            }
        }
    }

    /**
     * 验证时段配置的合理性
     */
    private static void validatePeriodConfiguration(List<Period> periodList) throws ValidationException {
        // 验证第一个时段是否从00:00开始
        if (!periodList.get(0).getStartTime().equals("00:00")) {
            throw new ValidationException("firstPeriod",
                    "第一个时段必须从00:00开始，当前为: " + periodList.get(0).getStartTime());
        }

        // 验证是否有重复的开始时间
        validateDuplicateStartTimes(periodList);

        // 验证时段覆盖范围（可选，根据业务需要）
        validatePeriodCoverage(periodList);
    }

    /**
     * 验证重复的开始时间
     */
    private static void validateDuplicateStartTimes(List<Period> periodList) throws ValidationException {
        Set<String> startTimes = new HashSet<>();

        for (int i = 0; i < periodList.size(); i++) {
            String startTime = periodList.get(i).getStartTime();
            if (startTimes.contains(startTime)) {
                throw new ValidationException("duplicateStartTime",
                        "存在重复的开始时间: " + startTime);
            }
            startTimes.add(startTime);
        }
    }

    /**
     * 验证时段覆盖范围
     */
    private static void validatePeriodCoverage(List<Period> periodList) throws ValidationException {
        // 检查是否有较大的时间间隙（超过4小时的间隙可能不合理）
        for (int i = 0; i < periodList.size() - 1; i++) {
            Period current = periodList.get(i);
            Period next = periodList.get(i + 1);

            int currentMinutes = Period.timeToMinutes(current.getStartTime());
            int nextMinutes = Period.timeToMinutes(next.getStartTime());
            int gap = nextMinutes - currentMinutes;

            if (gap > 4 * 60) { // 超过4小时
                logger.warn("时段{}到时段{}之间存在较大时间间隙: {}小时",
                        i + 1, i + 2, gap / 60.0);
            }
        }

        // 检查最后一个时段到24:00的间隙
        Period lastPeriod = periodList.get(periodList.size() - 1);
        int lastMinutes = Period.timeToMinutes(lastPeriod.getStartTime());
        int endGap = 24 * 60 - lastMinutes;

        if (endGap > 4 * 60) { // 超过4小时
            logger.warn("最后一个时段到24:00之间存在较大时间间隙: {}小时", endGap / 60.0);
        }
    }

    /**
     * 快速验证（只验证基本格式，不验证业务逻辑）
     */
    public static boolean quickValidate(DayPlanParam dayPlanParam) {
        try {
            if (dayPlanParam == null) return false;
            if (dayPlanParam.getCrossId() == null || dayPlanParam.getCrossId().trim().isEmpty()) return false;
            if (dayPlanParam.getDayPlanNo() == null ||
                    dayPlanParam.getDayPlanNo() < 1 || dayPlanParam.getDayPlanNo() > 999) return false;
            if (dayPlanParam.getPeriodList() == null || dayPlanParam.getPeriodList().isEmpty()) return false;

            // 简单验证时段格式
            for (Period period : dayPlanParam.getPeriodList()) {
                if (!period.isValid()) return false;
            }

            return true;
        } catch (Exception e) {
            logger.debug("快速验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证日计划名称唯一性（需要传入现有的日计划列表）
     */
    public static void validateNameUniqueness(DayPlanParam dayPlanParam, List<DayPlanParam> existingDayPlans)
            throws ValidationException {

        if (dayPlanParam.getDayPlanName() == null || dayPlanParam.getDayPlanName().trim().isEmpty()) {
            return; // 名称为空时不验证唯一性
        }

        String targetName = dayPlanParam.getDayPlanName().trim();
        String targetCrossId = dayPlanParam.getCrossId();
        Integer targetDayPlanNo = dayPlanParam.getDayPlanNo();

        for (DayPlanParam existing : existingDayPlans) {
            // 跳过自己
            if (existing.getCrossId().equals(targetCrossId) &&
                    existing.getDayPlanNo().equals(targetDayPlanNo)) {
                continue;
            }

            // 检查同一路口下是否有重名
            if (existing.getCrossId().equals(targetCrossId) &&
                    existing.getDayPlanName() != null &&
                    existing.getDayPlanName().trim().equals(targetName)) {
                throw new ValidationException("dayPlanName",
                        String.format("日计划名称'%s'在路口%s中已存在（日计划号%d）",
                                targetName, targetCrossId, existing.getDayPlanNo()));
            }
        }
    }

    /**
     * 验证日计划号唯一性
     */
    public static void validateDayPlanNoUniqueness(DayPlanParam dayPlanParam, List<DayPlanParam> existingDayPlans)
            throws ValidationException {

        String targetCrossId = dayPlanParam.getCrossId();
        Integer targetDayPlanNo = dayPlanParam.getDayPlanNo();

        for (DayPlanParam existing : existingDayPlans) {
            if (existing.getCrossId().equals(targetCrossId) &&
                    existing.getDayPlanNo().equals(targetDayPlanNo)) {
                throw new ValidationException("dayPlanNo",
                        String.format("日计划号%d在路口%s中已存在", targetDayPlanNo, targetCrossId));
            }
        }
    }

    /**
     * 自动修复时段配置
     * 修复一些常见的配置问题
     */
    public static DayPlanParam autoFixPeriodConfiguration(DayPlanParam dayPlanParam) {
        if (dayPlanParam == null || dayPlanParam.getPeriodList() == null) {
            return dayPlanParam;
        }

        List<Period> periods = dayPlanParam.getPeriodList();
        boolean hasChanges = false;

        // 修复时间格式
        for (Period period : periods) {
            if (period.getStartTime() != null) {
                String originalTime = period.getStartTime();
                String normalizedTime = Period.normalizeTimeFormat(originalTime);
                if (!originalTime.equals(normalizedTime)) {
                    period.setStartTime(normalizedTime);
                    hasChanges = true;
                    logger.info("修复时间格式: {} -> {}", originalTime, normalizedTime);
                }
            }
        }

        // 排序时段
        periods.sort((p1, p2) -> Period.compareTime(p1.getStartTime(), p2.getStartTime()));

        // 确保第一个时段从00:00开始
        if (!periods.isEmpty() && !periods.get(0).getStartTime().equals("00:00")) {
            logger.warn("第一个时段不是从00:00开始，建议检查配置");
        }

        if (hasChanges) {
            logger.info("自动修复日计划配置: crossId={}, dayPlanNo={}",
                    dayPlanParam.getCrossId(), dayPlanParam.getDayPlanNo());
        }

        return dayPlanParam;
    }

    /**
     * 获取验证报告
     */
    public static ValidationReport getValidationReport(DayPlanParam dayPlanParam) {
        ValidationReport report = new ValidationReport();

        try {
            validateDayPlanParam(dayPlanParam);
            report.setValid(true);
            report.addInfo("日计划参数验证通过");
        } catch (ValidationException e) {
            report.setValid(false);
            report.addError(e.getMessage());
        }

        // 添加警告信息
        if (dayPlanParam != null && dayPlanParam.getPeriodList() != null) {
            addWarningsToReport(dayPlanParam, report);
        }

        return report;
    }

    /**
     * 添加警告信息到报告
     */
    private static void addWarningsToReport(DayPlanParam dayPlanParam, ValidationReport report) {
        List<Period> periods = dayPlanParam.getPeriodList();

        // 检查时段数量
        if (periods.size() > 10) {
            report.addWarning("时段数量较多(" + periods.size() + ")，可能影响执行效率");
        }

        // 检查特殊控制方式的使用
        long specialCtrlCount = periods.stream()
                .filter(p -> isSpecialCtrlMode(p.getCtrlMode()))
                .count();

        if (specialCtrlCount > 0) {
            report.addWarning("包含" + specialCtrlCount + "个特殊控制时段");
        }

        // 检查方案号的连续性
        Set<Integer> planNos = new HashSet<>();
        for (Period period : periods) {
            if (period.getPlanNo() > 0) {
                planNos.add(period.getPlanNo());
            }
        }

        if (planNos.size() > 5) {
            report.addWarning("使用了较多的配时方案(" + planNos.size() + "个)");
        }
    }

    /**
     * 验证报告类
     */
    public static class ValidationReport {
        private boolean valid;
        private List<String> errors = new java.util.ArrayList<>();
        private List<String> warnings = new java.util.ArrayList<>();
        private List<String> infos = new java.util.ArrayList<>();

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public List<String> getInfos() { return infos; }

        public void addError(String error) { errors.add(error); }
        public void addWarning(String warning) { warnings.add(warning); }
        public void addInfo(String info) { infos.add(info); }

        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        public boolean hasInfos() { return !infos.isEmpty(); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationReport{valid=").append(valid);

            if (hasErrors()) {
                sb.append(", errors=").append(errors);
            }
            if (hasWarnings()) {
                sb.append(", warnings=").append(warnings);
            }
            if (hasInfos()) {
                sb.append(", infos=").append(infos);
            }

            sb.append("}");
            return sb.toString();
        }
    }
}
