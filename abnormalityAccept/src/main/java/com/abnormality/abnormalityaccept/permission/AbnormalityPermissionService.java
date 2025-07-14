package com.abnormality.abnormalityaccept.permission;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AbnormalityPermissionService {

    // 用户等级常量
    private static final int USER_LEVEL_D = 1; // D级
    private static final int USER_LEVEL_C = 2; // C级
    private static final int USER_LEVEL_B = 3; // B级
    private static final int USER_LEVEL_A = 4; // A级
    private static final int USER_LEVEL_O5 = 5; // O5级

    /**
     * 获取添加异想体时的可操作数据
     * @param inputData 前端传入的原始数据
     * @param userLevel 当前用户等级（整数）
     * @return 过滤后的可操作数据
     */
    public Map<String, Object> getAddAbnormalityData(Map<String, Object> inputData, Integer userLevel) {
        Map<String, Object> allowedData = new HashMap<>();

        // 所有用户都可设置名称和描述
        allowedData.put("name", inputData.getOrDefault("name", ""));
        allowedData.put("description", inputData.getOrDefault("description", ""));

        // 处理等级字段
        Object levelValue = inputData.get("level");
        int requestedLevel = 0;

        if (levelValue != null) {
            try {
                requestedLevel = (levelValue instanceof Number) ?
                        ((Number) levelValue).intValue() :
                        Integer.parseInt(levelValue.toString());
            } catch (NumberFormatException e) {
                // 保持默认值0
            }
        }

        // 验证等级权限
        if (requestedLevel > 0) {
            // D级和C级用户只能设置未评估
            if (userLevel == USER_LEVEL_D || userLevel == USER_LEVEL_C) {
                throw new SecurityException("权限不足：D级和C级用户只能设置未评估状态");
            }

            // B级用户只能设置C级(1)和B级(2)
            if (userLevel == USER_LEVEL_B && (requestedLevel < 1 || requestedLevel > 2)) {
                throw new SecurityException("权限不足：B级用户只能评估C级(1)和B级(2)异想体");
            }

            // A级用户只能设置B级(2)和A级(3)
            if (userLevel == USER_LEVEL_A && (requestedLevel < 2 || requestedLevel > 3)) {
                throw new SecurityException("权限不足：A级用户只能评估B级(2)和A级(3)异想体");
            }

            // O5级用户可以设置所有等级
            if (userLevel == USER_LEVEL_O5 && (requestedLevel < 0 || requestedLevel > 4)) {
                throw new IllegalArgumentException("异想体等级必须在0-4范围内");
            }
        }

        allowedData.put("level", requestedLevel);

        // 添加其他允许字段
        addIfPresent(inputData, allowedData, "manageMethod");
        addIfPresent(inputData, allowedData, "notes");
        addIfPresent(inputData, allowedData, "facilityId");

        return allowedData;
    }

    /**
     * 获取更新异想体时的可操作数据
     * @param inputData 前端传入的原始数据
     * @param original 数据库中的原始异想体数据
     * @param userLevel 当前用户等级（整数）
     * @return 过滤后的可操作数据
     */
    public Map<String, Object> getUpdateAbnormalityData(
            Map<String, Object> inputData,
            Map<String, Object> original,
            Integer userLevel) {

        Map<String, Object> allowedData = new HashMap<>();

        // 验证基本权限
        int originalLevel = (int) original.getOrDefault("level", 0);
        validateBasicPermission(originalLevel, userLevel);

        // 特殊字段权限验证（等级和名称）
        validateSpecialFields(inputData, original, userLevel);

        // 更新允许修改的字段
        addIfPresent(inputData, allowedData, "description");
        addIfPresent(inputData, allowedData, "manageMethod");
        addIfPresent(inputData, allowedData, "notes");
        addIfPresent(inputData, allowedData, "facilityId");

        // 特殊字段在通过验证后添加
        if (inputData.containsKey("level") && userLevel == USER_LEVEL_O5) {
            allowedData.put("level", inputData.get("level"));
        }
        if (inputData.containsKey("name") && userLevel == USER_LEVEL_O5) {
            allowedData.put("name", inputData.get("name"));
        }

        return allowedData;
    }

    private void validateBasicPermission(int abnormalityLevel, Integer userLevel) {
        if (userLevel == null) {
            throw new SecurityException("无效的用户等级");
        }

        // O5用户拥有所有权限
        if (userLevel == USER_LEVEL_O5) {
            return;
        }

        // 用户等级必须 ≥ 异想体等级
        if (userLevel < abnormalityLevel) {
            throw new SecurityException("权限不足：用户等级低于异想体等级");
        }
    }

    private void validateSpecialFields(
            Map<String, Object> inputData,
            Map<String, Object> original,
            Integer userLevel) {

        // 检查是否尝试修改等级
        if (inputData.containsKey("level")) {
            Object newLevel = inputData.get("level");
            Object oldLevel = original.get("level");

            if (!Objects.equals(newLevel, oldLevel)) {
                if (userLevel != USER_LEVEL_O5) {
                    throw new SecurityException("权限不足：只有O5级用户可修改异想体等级");
                }
            }
        }

        // 检查是否尝试修改名称
        if (inputData.containsKey("name")) {
            Object newName = inputData.get("name");
            Object oldName = original.get("name");

            if (!Objects.equals(newName, oldName)) {
                if (userLevel != USER_LEVEL_O5) {
                    throw new SecurityException("权限不足：只有O5级用户可修改异想体名称");
                }
            }
        }
    }

    private void addIfPresent(
            Map<String, Object> source,
            Map<String, Object> target,
            String key) {

        if (source.containsKey(key)) {
            target.put(key, source.get(key));
        }
    }

    /**
     * 获取当前用户可设置的等级选项
     * @param userLevel 当前用户等级（整数）
     * @return 可设置的等级选项列表
     */
    public Map<Integer, String> getAllowedLevelOptions(Integer userLevel) {
        Map<Integer, String> options = new HashMap<>();
        options.put(0, "未评估");

        if (userLevel == null) {
            return options;
        }

        switch (userLevel) {
            case USER_LEVEL_B:
                options.put(1, "C级");
                options.put(2, "B级");
                break;

            case USER_LEVEL_A:
                options.put(2, "B级");
                options.put(3, "A级");
                break;

            case USER_LEVEL_O5:
                options.put(1, "C级");
                options.put(2, "B级");
                options.put(3, "A级");
                options.put(4, "最高级");
                break;
        }

        return options;
    }
}