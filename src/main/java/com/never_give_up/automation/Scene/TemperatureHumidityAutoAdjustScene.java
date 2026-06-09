package com.never_give_up.automation.Scene;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.never_give_up.automation.Component.SMSComponent;
import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.Service.HomeAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 场景1：根据温湿度和使用习惯自动调整空调
 * 核心规则：仅定时开启(晚18:40)→预冷(16度最大风)→维持(26度最小风)→仅定时关闭(早7:50)
 * 其他时间：不主动开启空调，不关闭已运行的空调，仅维持已开启状态的温湿度调节
 * 新增规则：门窗传感器未关闭状态持续超过10分钟，自动关闭空调
 */
@Component
public class TemperatureHumidityAutoAdjustScene {

    @Autowired
    private SMSComponent smsComponent;

    private final HomeAssistantService homeAssistantService;

    // 配置参数 - 可根据需求调整
    private static final String TEMPERATURE_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_temperature_p_2_1001";
    private static final String HUMIDITY_SENSOR_ID = "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_relative_humidity_p_2_1002";
    private static final String AC_ENTITY_ID = "climate.hzyk_cn_2003157372_kt5s01";
    private static final String OCCUPANCY_SENSOR_ID = "binary_sensor.presence_sensor"; // 占位符
    private static final String DOOR_WINDOW_SENSOR_ID = "binary_sensor.isa_cn_blt_3_1muf5m5bg4s00_dw2hl_contact_state_p_2_2"; // 新增：门窗传感器ID

    // 温度配置
    private static final double PRECOOL_TEMP = 16.0;        // 预冷温度
    private static final double TARGET_TEMP = 26.0;         // 维持温度
    private static final double PRECOOL_COMPLETE_TEMP = 27.0; // 预冷完成判断温度
    private static final double HIGH_HUMIDITY_THRESHOLD = 70.0; // 高湿度阈值

    // 定时配置（核心：仅这两个时段执行开/关，其他时间不干预启停）
    private static final LocalTime EVENING_ON_TIME = LocalTime.of(18, 40);  // 唯一开启时间
    private static final LocalTime MORNING_OFF_TIME = LocalTime.of(7, 50);   // 唯一关闭时间
    private static final int TIME_WINDOW_MINUTES = 1; // 定时执行窗口（避免调度延迟漏执行）
    private static final int CHECK_INTERVAL = 6000; // 状态检查间隔(毫秒)

    // 风力配置
    private static final String MAX_FAN_MODE = "高风";   // 最大风力
    private static final String MIN_FAN_MODE = "低风";   // 最小风力
    private static final String DEHUMIDIFY_MODE = "dry"; // 除湿模式

    // 新增：门窗监控配置
    private static final long DOOR_WINDOW_OPEN_DURATION_THRESHOLD = 2; // 门窗开启超时阈值（分钟）
    private ZonedDateTime doorWindowOpenStartTime; // 记录门窗开始开启的时间戳
    private boolean hasDoorWindowTimeoutTriggered = false; // 标记门窗超时关闭是否已触发（避免重复执行）

    // 原有状态标记
    private boolean isPrecoolComplete = false; // 预冷是否完成
    private boolean hasEveningOnTriggered = false; // 晚间开启是否已触发
    private boolean hasMorningOffTriggered = false; // 晨间关闭是否已触发

    public TemperatureHumidityAutoAdjustScene(HomeAssistantService homeAssistantService) {
        this.homeAssistantService = homeAssistantService;
    }

    /**
     * 定时执行场景逻辑（每6秒检查一次，优先处理紧急关闭逻辑，再处理定时和维持逻辑）
     */
    @Scheduled(fixedRate = CHECK_INTERVAL)
    public void executeScene() {
        // 检查连接状态（连接失败时重试，不执行其他逻辑）
        if (!homeAssistantService.isConnected()) {
            System.err.println("场景执行失败：未连接到Home Assistant，尝试重连...");
            homeAssistantService.reconnect();
            return;
        }

        try {
            // 新增：1. 优先处理门窗超时关闭逻辑（紧急节能场景，优先级最高）
            handleDoorWindowTimeoutShutdown();

            // 原有：2. 处理定时启停逻辑
            // 晨间7:50关闭逻辑（仅在时间窗口内且未触发过时执行）
            if (shouldTurnOffInMorning() && !hasMorningOffTriggered) {
                turnOffAcInMorning();
                return;
            }
            // 晚间18:40开启逻辑（仅在时间窗口内且未触发过时执行）
            if (shouldTurnOnInEvening() && !hasEveningOnTriggered) {
                turnOnAcInEvening();
                return;
            }

            // 原有：3. 重置定时标记
            resetDailyFlags();

            // 原有：4. 获取空调当前状态，非开启状态则退出
            HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
            if (acState == null) {
                System.err.println("未找到空调设备，场景执行终止");
                return;
            }
            String currentAcMode = acState.getState();

            // 关键规则：其他时间（非18:40）若空调未开启，直接退出，不主动开启
            if ("off".equals(currentAcMode)) {
                System.out.println("当前非定时开启时段，空调已关闭，不主动开启，跳过本次执行");
                return;
            }

            // 原有：5. 空调已开启时，执行温湿度维持逻辑
            double temperature = getSensorValue(TEMPERATURE_SENSOR_ID);
            double humidity = getSensorValue(HUMIDITY_SENSOR_ID);
            if (temperature == -1 || humidity == -1) {
                System.err.println("温湿度传感器数据获取失败，跳过本次维持逻辑");
                return;
            }

            // 打印当前状态（便于调试）
            System.out.println("\n===== 执行空调维持调节逻辑 =====");
            System.out.println("当前温度：" + temperature + "°C，当前湿度：" + humidity + "%");
            System.out.println("预冷状态：" + (isPrecoolComplete ? "已完成" : "进行中"));
            System.out.println("空调当前模式：" + currentAcMode);

            // 原有：6. 温湿度调节逻辑
            adjustAcBasedOnHabit(temperature, humidity, currentAcMode, acState);

        } catch (Exception e) {
            System.err.println("场景执行异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------ 新增：门窗超时关闭逻辑 ------------------------------
    /**
     * 处理门窗未关闭超时自动关闭空调逻辑
     */
    private void handleDoorWindowTimeoutShutdown() {
        System.out.println("处理门窗超时关闭逻辑...");
        SendSmsResponse sendSmsResponse = smsComponent.smsResponse("17338031465", "123456");
        System.out.println("短信发送结果：" + sendSmsResponse.body.getMessage());
        // 1. 获取门窗传感器状态
        HaEntityState doorWindowState = homeAssistantService.getEntityState(DOOR_WINDOW_SENSOR_ID);
        System.out.println("门窗传感器原始状态：" + doorWindowState);
        if (doorWindowState == null || "unavailable".equals(doorWindowState.getState())) {
            System.err.println("门窗传感器" + DOOR_WINDOW_SENSOR_ID + "离线或不存在，跳过门窗监控逻辑");
            return;
        }

        // 2. 获取空调状态（仅空调开启时才需要监控）
        HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
        if (acState == null || "off".equals(acState.getState())) {
            System.out.println("空调当前未开启，重置门窗监控状态");
            // 空调已关闭时，重置门窗监控状态
            resetDoorWindowMonitorState();
            return;
        }

        String doorWindowStateStr = doorWindowState.getState();
        System.out.println("当前门窗状态：" + doorWindowStateStr + "（on=开启，off=关闭）");
        // 3. 适配实际传感器状态：state=on 表示开启，state=off 表示关闭
        boolean isDoorWindowOpen = "on".equals(doorWindowStateStr);

        if (isDoorWindowOpen) {
            // 门窗开启状态：记录开始时间或检查超时
            if (doorWindowOpenStartTime == null) {
                // 首次检测到开启，记录时间戳
                doorWindowOpenStartTime = ZonedDateTime.now();
                System.out.println("检测到门窗开启，开始计时：" + doorWindowOpenStartTime);
                hasDoorWindowTimeoutTriggered = false; // 重置触发标记
            } else {
                // 计算开启时长
                long openMinutes = Duration.between(doorWindowOpenStartTime, ZonedDateTime.now()).toMinutes();
                System.out.println("门窗已开启时长：" + openMinutes + "分钟（阈值：" + DOOR_WINDOW_OPEN_DURATION_THRESHOLD + "分钟）");

                // 超时且未触发过关闭操作，执行关闭
                if (openMinutes >= DOOR_WINDOW_OPEN_DURATION_THRESHOLD && !hasDoorWindowTimeoutTriggered) {
                    System.out.println("===== 门窗开启超时" + openMinutes + "分钟，自动关闭空调 =====");
                    homeAssistantService.controlAcPower(AC_ENTITY_ID, false);
                    hasDoorWindowTimeoutTriggered = true; // 标记已触发，避免重复关闭
                    isPrecoolComplete = false; // 重置预冷状态（为下次开启做准备）
                    // 同时重置定时开启标记（避免当天重复触发定时开启）
                    hasEveningOnTriggered = true;
                }
            }
        } else {
            // 门窗已关闭，重置监控状态
            resetDoorWindowMonitorState();
        }
    }

    /**
     * 重置门窗监控状态（门窗关闭或空调关闭时调用）
     */
    private void resetDoorWindowMonitorState() {
        if (doorWindowOpenStartTime != null) {
            System.out.println("门窗已关闭或空调已关闭，重置门窗监控计时");
            doorWindowOpenStartTime = null;
            hasDoorWindowTimeoutTriggered = false;
        }
    }

    // ------------------------------ 原有逻辑保持不变 ------------------------------
    /**
     * 空调已开启时的调节逻辑（预冷→维持，不包含关闭逻辑）
     */
    private void adjustAcBasedOnHabit(double temperature, double humidity, String currentMode, HaEntityState acState) {
        // 湿度优先：若湿度超标，切换除湿模式（无论预冷/维持阶段）
        if (humidity > HIGH_HUMIDITY_THRESHOLD) {
            handleHumidityControl(currentMode);
            return;
        }

        // 预冷阶段：未完成预冷时，维持16℃高风，直到温度降至27℃以下
        if (!isPrecoolComplete) {
            if (temperature > PRECOOL_COMPLETE_TEMP) {
                maintainPrecoolMode(acState);
            } else {
                switchToMaintainMode();
                isPrecoolComplete = true; // 标记预冷完成，后续进入维持阶段
            }
        }
        // 维持阶段：预冷完成后，维持26℃低风
        else {
            maintainComfortMode(temperature, acState);
        }
    }

    /**
     * 判断是否处于晨间关闭时间窗口（7:49-7:51）且空调已开启
     */
    private boolean shouldTurnOffInMorning() {
        LocalTime now = LocalTime.now();
        LocalTime windowStart = MORNING_OFF_TIME.minusMinutes(TIME_WINDOW_MINUTES);
        LocalTime windowEnd = MORNING_OFF_TIME.plusMinutes(TIME_WINDOW_MINUTES);
        // 仅在窗口内且空调已开启时，才允许关闭
        HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
        boolean isAcOn = acState != null && !"off".equals(acState.getState());
        return now.isAfter(windowStart) && now.isBefore(windowEnd) && isAcOn;
    }

    /**
     * 执行晨间关闭（仅在7:50窗口内触发，关闭后重置预冷状态）
     */
    private void turnOffAcInMorning() {
        System.out.println("===== 触发晨间定时关闭：" + MORNING_OFF_TIME + " =====");
        homeAssistantService.controlAcPower(AC_ENTITY_ID, false);
        isPrecoolComplete = false; // 重置预冷状态（为次日开启做准备）
        hasMorningOffTriggered = true; // 标记已触发，避免1分钟内重复关闭
        resetDoorWindowMonitorState(); // 同步重置门窗监控状态
    }

    /**
     * 判断是否处于晚间开启时间窗口（18:39-18:41）且空调已关闭
     */
    private boolean shouldTurnOnInEvening() {
        LocalTime now = LocalTime.now();
        LocalTime windowStart = EVENING_ON_TIME.minusMinutes(TIME_WINDOW_MINUTES);
        LocalTime windowEnd = EVENING_ON_TIME.plusMinutes(TIME_WINDOW_MINUTES);
        // 仅在窗口内且空调已关闭时，才允许开启
        HaEntityState acState = homeAssistantService.getEntityState(AC_ENTITY_ID);
        boolean isAcOff = acState != null && "off".equals(acState.getState());
        return now.isAfter(windowStart) && now.isBefore(windowEnd) && isAcOff;
    }

    /**
     * 执行晚间开启（仅在18:40窗口内触发，开启后直接进入预冷模式）
     */
    private void turnOnAcInEvening() {
        System.out.println("===== 触发晚间定时开启：" + EVENING_ON_TIME + " =====");
        homeAssistantService.controlAcPower(AC_ENTITY_ID, true);
        try {
            Thread.sleep(2000); // 等待空调启动（避免指令发送过快失败）
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 开启后直接进入预冷模式
        homeAssistantService.setAcTemperature(AC_ENTITY_ID, PRECOOL_TEMP);
        setFanMode(MAX_FAN_MODE);
        isPrecoolComplete = false; // 标记预冷未完成
        hasEveningOnTriggered = true; // 标记已触发，避免1分钟内重复开启
        resetDoorWindowMonitorState(); // 同步重置门窗监控状态
    }

    /**
     * 重置每日定时标记（非时间窗口时重置，确保次日可重新触发）
     */
    private void resetDailyFlags() {
        LocalTime now = LocalTime.now();
        // 晨间关闭窗口结束后（7:51后），重置关闭标记
        LocalTime morningWindowEnd = MORNING_OFF_TIME.plusMinutes(TIME_WINDOW_MINUTES);
        if (now.isAfter(morningWindowEnd) && hasMorningOffTriggered) {
            hasMorningOffTriggered = false;
        }
        // 晚间开启窗口结束后（18:41后），重置开启标记
        LocalTime eveningWindowEnd = EVENING_ON_TIME.plusMinutes(TIME_WINDOW_MINUTES);
        if (now.isAfter(eveningWindowEnd) && hasEveningOnTriggered) {
            hasEveningOnTriggered = false;
        }
    }

    /**
     * 湿度超标时切换除湿模式
     */
    private void handleHumidityControl(String currentMode) {
        if (!DEHUMIDIFY_MODE.equals(currentMode)) {
            System.out.println("湿度超过" + HIGH_HUMIDITY_THRESHOLD + "%，切换到除湿模式");
            homeAssistantService.setAcMode(AC_ENTITY_ID, DEHUMIDIFY_MODE);
        } else {
            System.out.println("已处于除湿模式，湿度正常，无需调整");
        }
    }

    /**
     * 维持预冷模式（16℃高风）
     */
    private void maintainPrecoolMode(HaEntityState acState) {
        // 检查温度是否为预冷温度，不是则调整
        if (!isTargetTemp(acState, PRECOOL_TEMP)) {
            System.out.println("维持预冷模式，设置温度为" + PRECOOL_TEMP + "°C");
            homeAssistantService.setAcTemperature(AC_ENTITY_ID, PRECOOL_TEMP);
        }
        // 检查风力是否为高风，不是则调整
        checkAndSetFanMode(acState, MAX_FAN_MODE);
    }

    /**
     * 切换到维持模式（26℃低风）
     */
    private void switchToMaintainMode() {
        System.out.println("预冷完成（温度≤" + PRECOOL_COMPLETE_TEMP + "℃），切换到维持模式（" + TARGET_TEMP + "℃，" + MIN_FAN_MODE + "）");
        homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP);
        setFanMode(MIN_FAN_MODE);
    }

    /**
     * 维持舒适模式（26℃低风，温度波动±0.5℃内不调整）
     */
    private void maintainComfortMode(double currentTemp, HaEntityState acState) {
        if (Math.abs(currentTemp - TARGET_TEMP) > 0.5) {
            System.out.println("维持温度波动，调节至" + TARGET_TEMP + "℃");
            homeAssistantService.setAcTemperature(AC_ENTITY_ID, TARGET_TEMP);
        }
        // 检查风力是否为低风，不是则调整
        checkAndSetFanMode(acState, MIN_FAN_MODE);
    }

    /**
     * 检查并设置风力模式（仅在当前模式与目标模式不一致时发送指令）
     */
    private void checkAndSetFanMode(HaEntityState acState, String targetFanMode) {
        Map<String, Object> attributes = acState.getAttributes();
        String currentFanMode = attributes.getOrDefault("fan_mode", "").toString();
        if (!targetFanMode.equals(currentFanMode)) {
            setFanMode(targetFanMode);
        }
    }

    /**
     * 发送风力模式设置指令
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
     * 获取传感器数值（失败时返回-1）
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
     * 检查空调当前温度是否为目标温度（波动±0.5℃内视为达标）
     */
    private boolean isTargetTemp(HaEntityState acState, double targetTemp) {
        Map<String, Object> attributes = acState.getAttributes();
        if (attributes.containsKey("temperature")) {
            Object tempObj = attributes.get("temperature");
            double currentTemp;
            // 处理整数/浮点型温度值
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