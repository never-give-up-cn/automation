package com.never_give_up.automation.Scene;

import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.Service.HomeAssistantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

/**
 * 场景1：根据温湿度和使用习惯自动调整空调
 * 符合个人习惯：预冷(16度最大风)→维持(26度最小风)→定时关闭(早8点)
 */
@Component
public class TemperatureHumidityAutoAdjustScene {

    private final HomeAssistantService homeAssistantService;

    // 配置参数 - 可根据需求调整
    private static final String TEMPERATURE_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_temperature_p_2_1001";
    private static final String HUMIDITY_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_relative_humidity_p_2_1002";
    private static final String AC_ENTITY_ID = "climate.hzyk_cn_2003157372_kt5s01";
    // 人在传感器（后续添加）
    private static final String OCCUPANCY_SENSOR_ID = "binary_sensor.presence_sensor"; // 占位符

    // 温度配置
    private static final double PRECOOL_TEMP = 16.0;        // 预冷温度
    private static final double TARGET_TEMP = 26.0;         // 维持温度
    private static final double PRECOOL_COMPLETE_TEMP = 27.0; // 预冷完成判断温度
    private static final double HIGH_HUMIDITY_THRESHOLD = 70.0; // 高湿度阈值

    // 时间配置
    private static final LocalTime MORNING_OFF_TIME = LocalTime.of(8, 0); // 早上关闭时间
    private static final int CHECK_INTERVAL = 6000; // 检查间隔(毫秒)

    // 风力配置
    private static final String MAX_FAN_MODE = "高风";   // 最大风力
    private static final String MIN_FAN_MODE = "低风";   // 最小风力
    private static final String DEHUMIDIFY_MODE = "dry"; // 除湿模式

    // 状态标记
    private boolean isPrecoolComplete = false; // 预冷是否完成

    public TemperatureHumidityAutoAdjustScene(HomeAssistantService homeAssistantService) {
        this.homeAssistantService = homeAssistantService;
    }

    /**
     * 定时执行场景逻辑（每6秒检查一次）
     */
    @Scheduled(fixedRate = CHECK_INTERVAL)
    public void executeScene() {
        // 检查连接状态
        if (!homeAssistantService.isConnected()) {
            System.err.println("场景执行失败：未连接到Home Assistant");
            homeAssistantService.reconnect();
            return;
        }

        try {
            // 优先执行定时关闭逻辑（早上8点关闭）
            if (shouldTurnOffInMorning()) {
                homeAssistantService.controlAcPower(AC_ENTITY_ID, false);
                isPrecoolComplete = false; // 重置预冷状态
                return;
            }

            // 获取温湿度传感器数据
            double temperature = getSensorValue(TEMPERATURE_SENSOR_ID);
            double humidity = getSensorValue(HUMIDITY_SENSOR_ID);

            if (temperature == -1 || humidity == -1) {
                System.err.println("温湿度传感器数据获取失败，跳过本次场景执行");
                return;
            }

            System.out.println("\n===== 执行温湿度自动调节场景 =====");
            System.out.println("当前温度：" + temperature + "°C，当前湿度：" + humidity + "%");
            System.out.println("预冷状态：" + (isPrecoolComplete ? "已完成" : "进行中"));

            // 获取空调当前状态
            HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
            if (acState == null) {
                System.err.println("未找到空调设备，场景执行终止");
                return;
            }

            String currentAcMode = acState.getState();
            System.out.println("空调当前模式：" + currentAcMode);

            // 根据温湿度和个人习惯执行调节逻辑
            adjustAcBasedOnHabit(temperature, humidity, currentAcMode, acState);

        } catch (Exception e) {
            System.err.println("场景执行异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据个人习惯调节空调（预冷→维持→关闭）
     */
    private void adjustAcBasedOnHabit(double temperature, double humidity, String currentMode, HaEntityState acState) {
        // 湿度优先处理
        if (humidity > HIGH_HUMIDITY_THRESHOLD) {
            handleHumidityControl(currentMode);
            return;
        }

        // 空调关闭状态处理：开启并进入预冷模式
        if ("off".equals(currentMode)) {
            startPrecoolMode();
            return;
        }

        // 空调开启状态处理
        if (!isPrecoolComplete) {
            // 预冷阶段：16度最大风，直到温度降至27度
            if (temperature > PRECOOL_COMPLETE_TEMP) {
                maintainPrecoolMode(acState);
            } else {
                // 预冷完成，切换到维持模式
                switchToMaintainMode();
                isPrecoolComplete = true;
            }
        } else {
            // 维持阶段：26度最小风
            maintainComfortMode(temperature, acState);
        }
    }

    /**
     * 处理湿度控制
     */
    private void handleHumidityControl(String currentMode) {
        if (!DEHUMIDIFY_MODE.equals(currentMode)) {
            System.out.println("湿度超过阈值，切换到除湿模式");
            homeAssistantService.setAcMode(AC_ENTITY_ID, DEHUMIDIFY_MODE);
        } else {
            System.out.println("已处于除湿模式，无需调整");
        }
    }

    /**
     * 开始预冷模式（16度，最大风）
     */
    private void startPrecoolMode() {
        System.out.println("空调关闭，开启预冷模式（" + PRECOOL_TEMP + "°C，" + MAX_FAN_MODE + "）");
        homeAssistantService.controlAcPower(AC_ENTITY_ID, true);
        try {
            Thread.sleep(2000); // 等待空调启动
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        homeAssistantService.setAcTemperature(AC_ENTITY_ID, PRECOOL_TEMP);
        setFanMode(MAX_FAN_MODE);
        isPrecoolComplete = false;
    }

    /**
     * 维持预冷模式
     */
    private void maintainPrecoolMode(HaEntityState acState) {
        // 检查当前温度是否为预冷温度
        if (!isTargetTemp(acState, PRECOOL_TEMP)) {
            System.out.println("维持预冷模式，设置温度为" + PRECOOL_TEMP + "°C");
            homeAssistantService.setAcTemperature(AC_ENTITY_ID, PRECOOL_TEMP);
        }
        // 检查当前风力是否为最大风
        checkAndSetFanMode(acState, MAX_FAN_MODE);
    }

    /**
     * 切换到维持模式（26度，最小风）
     */
    private void switchToMaintainMode() {
        System.out.println("预冷完成，切换到维持模式（" + TARGET_TEMP + "°C，" + MIN_FAN_MODE + "）");
        homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP);
        setFanMode(MIN_FAN_MODE);
    }

    /**
     * 维持舒适模式
     */
    private void maintainComfortMode(double currentTemp, HaEntityState acState) {
        if (Math.abs(currentTemp - TARGET_TEMP) > 0.5) {
            System.out.println("调节温度至维持温度" + TARGET_TEMP + "°C");
            homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP);
        }
        // 检查当前风力是否为最小风
        checkAndSetFanMode(acState, MIN_FAN_MODE);
    }

    /**
     * 检查并设置风力模式
     */
    private void checkAndSetFanMode(HaEntityState acState, String targetFanMode) {
        Map<String, Object> attributes = acState.getAttributes();
        String currentFanMode = attributes.getOrDefault("fan_mode", "").toString();

        if (!targetFanMode.equals(currentFanMode)) {
            setFanMode(targetFanMode);
        }
    }

    /**
     * 设置风力模式
     */
    private void setFanMode(String fanMode) {
        System.out.println("设置风力模式为：" + fanMode);
        homeAssistantService.callServiceWithData(
                "climate",
                "set_fan_mode",
                AC_ENTITY_ID,
                Map.of("fan_mode", fanMode)
        );
    }

    /**
     * 判断是否需要在早上关闭空调
     */
    private boolean shouldTurnOffInMorning() {
        LocalTime now = LocalTime.now();
        // 检查当前时间是否在早上8点左右（±1分钟）
        return now.isAfter(MORNING_OFF_TIME.minusMinutes(1)) &&
                now.isBefore(MORNING_OFF_TIME.plusMinutes(1));
    }

    /**
     * 获取传感器数值
     */
    private double getSensorValue(String sensorId) {
        HaEntityState sensorState = homeAssistantService.getEntityState(sensorId);
        if (sensorState == null || "unavailable".equals(sensorState.getState())) {
            System.err.println("传感器" + sensorId + "离线或不存在");
            return -1;
        }

        try {
            return Double.parseDouble(sensorState.getState());
        } catch (NumberFormatException e) {
            System.err.println("传感器" + sensorId + "数值解析失败：" + sensorState.getState());
            return -1;
        }
    }

    /**
     * 检查空调当前温度是否已为目标温度
     */
    private boolean isTargetTemp(HaEntityState acState, double targetTemp) {
        Map<String, Object> attributes = acState.getAttributes();
        if (attributes.containsKey("temperature")) {
            Object tempObj = attributes.get("temperature");
            double currentTemp;

            if (tempObj instanceof Integer) {
                currentTemp = ((Integer) tempObj).doubleValue();
            } else if (tempObj instanceof Double) {
                currentTemp = (Double) tempObj;
            } else {
                try {
                    currentTemp = Double.parseDouble(tempObj.toString());
                } catch (NumberFormatException e) {
                    System.err.println("温度值格式异常：" + tempObj);
                    return false;
                }
            }

            return Math.abs(currentTemp - targetTemp) < 0.5;
        }
        return false;
    }
}
