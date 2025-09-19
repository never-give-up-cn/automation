package com.never_give_up.automation.Scheduler;

import com.never_give_up.automation.Service.HomeAssistantService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时全量同步设备状态调度器
 * 负责定期从Home Assistant拉取所有设备状态，更新本地缓存
 */
@Component
public class StateSyncScheduler {

    private final HomeAssistantService homeAssistantService;

    // 刷新间隔（毫秒）：30分钟 = 1800000毫秒
    private static final long REFRESH_INTERVAL = 1800;

    public StateSyncScheduler(HomeAssistantService homeAssistantService) {
        this.homeAssistantService = homeAssistantService;
    }

    /**
     * 定时执行全量状态刷新
     * 采用fixedRate确保两次执行间隔稳定（不受执行时间影响）
     */
    @Scheduled(fixedRate = REFRESH_INTERVAL)
    public void scheduledFullStateRefresh() {
        // 检查连接状态
        if (!homeAssistantService.isConnected()) {
            System.err.println("【状态同步】未连接到Home Assistant，尝试重连...");
            homeAssistantService.reconnect();
            // 重连后再次检查
            if (!homeAssistantService.isConnected()) {
                System.err.println("【状态同步】重连失败，本次刷新取消");
                return;
            }
        }

        try {
            System.out.println("【状态同步】开始全量刷新设备状态...");
            long startTime = System.currentTimeMillis();

            // 调用服务类方法拉取全量状态
            homeAssistantService.getStates();

            long costTime = System.currentTimeMillis() - startTime;
            System.out.println("【状态同步】全量刷新完成，耗时 " + costTime + "ms");
        } catch (Exception e) {
            System.err.println("【状态同步】全量刷新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
