package com.never_give_up.automation.Scene;

import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.Service.HomeAssistantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 场景1：根据温湿度自动调整空调温度
 * 功能：当室内温度或湿度超过阈值时，自动调节空调温度或模式
 */
@Component
public class TemperatureHumidityAutoAdjustScene {

    private final HomeAssistantService homeAssistantService;

    // 配置参数 - 可根据需求调整
    private static final String TEMPERATURE_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_temperature1"; // 温度传感器ID
    private static final String HUMIDITY_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggdb23g4g00_mini_relative_humidity1"; // 湿度传感器ID
    private static final String AC_ENTITY_ID = "climate.hzyk_cn_2003157372_kt5s011"; // 空调设备ID

    // 温度阈值配置
    private static final double HIGH_TEMP_THRESHOLD = 28.0; // 高温阈值，超过则降温
    private static final double LOW_TEMP_THRESHOLD = 24.0;  // 低温阈值，低于则升温
    private static final double TARGET_TEMP_HIGH = 25.0;    // 高温时目标温度
    private static final double TARGET_TEMP_LOW = 26.0;     // 低温时目标温度

    // 湿度阈值配置
    private static final double HIGH_HUMIDITY_THRESHOLD = 70.0; // 高湿度阈值
    private static final String DEHUMIDIFY_MODE = "dry";        // 除湿模式

    public TemperatureHumidityAutoAdjustScene(HomeAssistantService homeAssistantService) {
        this.homeAssistantService = homeAssistantService;
    }

    /**
     * 定时执行场景逻辑（每3分钟检查一次）
     */
    @Scheduled(fixedRate = 180000) // 180000毫秒 = 3分钟
    public void executeScene() {
        // 检查连接状态
        if (!homeAssistantService.isConnected()) {
            System.err.println("场景执行失败：未连接到Home Assistant");
            homeAssistantService.reconnect();
            return;
        }

        try {
            // 获取温湿度传感器数据
            double temperature = getSensorValue(TEMPERATURE_SENSOR_ID);
            double humidity = getSensorValue(HUMIDITY_SENSOR_ID);

            if (temperature == -1 || humidity == -1) {
                System.err.println("温湿度传感器数据获取失败，跳过本次场景执行");
                return;
            }

            System.out.println("\n===== 执行温湿度自动调节场景 =====");
            System.out.println("当前温度：" + temperature + "°C，当前湿度：" + humidity + "%");

            // 获取空调当前状态
            HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
            if (acState == null) {
                System.err.println("未找到空调设备，场景执行终止");
                return;
            }

            String currentAcMode = acState.getState();
            System.out.println("空调当前模式：" + currentAcMode);

            // 根据温湿度执行调节逻辑
            adjustAcBasedOnEnvironment(temperature, humidity, currentAcMode,acState);

        } catch (Exception e) {
            System.err.println("场景执行异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据环境温湿度调节空调
     */
    private void adjustAcBasedOnEnvironment(double temperature, double humidity, String currentMode, HaEntityState acState) {
        // 湿度优先处理：如果湿度过高，切换到除湿模式
        if (humidity > HIGH_HUMIDITY_THRESHOLD) {
            if (!DEHUMIDIFY_MODE.equals(currentMode)) {
                System.out.println("湿度超过阈值(" + HIGH_HUMIDITY_THRESHOLD + "%)，切换到除湿模式");
                homeAssistantService.setAcMode(AC_ENTITY_ID, DEHUMIDIFY_MODE);
                return; // 湿度优先，处理后返回
            } else {
                System.out.println("已处于除湿模式，无需调整");
                return;
            }
        }

        // 如果湿度正常，根据温度调节
        if (!"off".equals(currentMode)) { // 空调处于开启状态时才调节温度
            if (temperature > HIGH_TEMP_THRESHOLD && !isTargetTemp(acState, TARGET_TEMP_HIGH)) {
                System.out.println("温度超过高温阈值(" + HIGH_TEMP_THRESHOLD + "°C)，调节至" + TARGET_TEMP_HIGH + "°C");
                homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP_HIGH);
            } else if (temperature < LOW_TEMP_THRESHOLD && !isTargetTemp(acState, TARGET_TEMP_LOW)) {
                System.out.println("温度低于低温阈值(" + LOW_TEMP_THRESHOLD + "°C)，调节至" + TARGET_TEMP_LOW + "°C");
                homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP_LOW);
            } else {
                System.out.println("温湿度在正常范围内，无需调节空调");
            }
        } else {
            // 空调处于关闭状态，但温度过高时自动开启
            if (temperature > HIGH_TEMP_THRESHOLD + 2) {
                System.out.println("温度过高且空调关闭，自动开启空调并调节至" + TARGET_TEMP_HIGH + "°C");
                homeAssistantService.controlAcPower(AC_ENTITY_ID, true);
                // 延迟一小段时间确保空调开启
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP_HIGH);
            }
        }
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
            double currentTemp = (Double) attributes.get("temperature");
            return Math.abs(currentTemp - targetTemp) < 0.5; // 允许±0.5°C的误差
        }
        return false;
    }
}

