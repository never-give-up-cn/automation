package com.never_give_up.automation.Scene;

import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.Service.HomeAssistantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 场景：工作日（周一至周五）早8点至晚19点自动开启指定设备   监控室内开关
 * 设备ID：switch.cuco_cn_944233652_v3_on_p_2_1（监控室内开关）
 */
@Component
public class WorkdayDeviceScheduleScene {

    private final HomeAssistantService homeAssistantService;

    // 目标设备ID（监控室内开关）
    private static final String TARGET_DEVICE_ID = "switch.cuco_cn_944233652_v3_on_p_2_1";

    // 时间配置（24小时制）
    private static final int START_HOUR = 8;   // 开启时间：早上8点
    private static final int END_HOUR = 19;    // 关闭时间：晚上19点

    public WorkdayDeviceScheduleScene(HomeAssistantService homeAssistantService) {
        this.homeAssistantService = homeAssistantService;
    }

    /**
     * 每10分钟检查一次状态（确保在时间范围内设备处于开启状态）
     * cron表达式含义：每分钟的0秒，每10分钟执行一次，每天、每月、每周都执行
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkAndControlDevice() {
        // 检查连接状态
        if (!homeAssistantService.isConnected()) {
            System.err.println("设备定时场景执行失败：未连接到Home Assistant");
            homeAssistantService.reconnect();
            return;
        }

        try {
            // 1. 判断当前是否为工作日（周一至周五）
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC); // 使用UTC时间，与Home Assistant保持一致
            DayOfWeek dayOfWeek = now.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                // 周末：确保设备关闭
                controlDevice(false);
                return;
            }

            // 2. 工作日：判断当前时间是否在[8:00, 19:00]范围内
            int currentHour = now.getHour();
            boolean isInTimeRange = currentHour >= START_HOUR && currentHour < END_HOUR;

            // 3. 根据时间范围控制设备
            controlDevice(isInTimeRange);

        } catch (Exception e) {
            System.err.println("设备定时场景执行异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 控制设备开关状态
     *
     * @param shouldTurnOn 是否应该开启设备
     */
    private void controlDevice(boolean shouldTurnOn) {
        HaEntityState deviceState = homeAssistantService.getEntityState(TARGET_DEVICE_ID);

        // 检查设备状态
        if (deviceState == null) {
            System.err.println("设备" + TARGET_DEVICE_ID + "不存在，无法控制");
            return;
        }
        if ("unavailable".equals(deviceState.getState())) {
            System.err.println("设备" + TARGET_DEVICE_ID + "离线，无法控制");
            return;
        }

        String currentState = deviceState.getState();
        // 判断是否需要执行操作
        if (shouldTurnOn && "off".equals(currentState)) {
            // 需要开启且当前为关闭状态
            homeAssistantService.callService("switch", "turn_on", TARGET_DEVICE_ID);
            System.out.println("已开启设备：" + TARGET_DEVICE_ID);
        } else if (!shouldTurnOn && "on".equals(currentState)) {
            // 需要关闭且当前为开启状态
            homeAssistantService.callService("switch", "turn_off", TARGET_DEVICE_ID);
            System.out.println("已关闭设备：" + TARGET_DEVICE_ID);
        } else {
            // 状态已符合预期，无需操作
            System.out.println("设备" + TARGET_DEVICE_ID + "状态符合预期（当前：" + currentState + "）");
        }
    }
}
