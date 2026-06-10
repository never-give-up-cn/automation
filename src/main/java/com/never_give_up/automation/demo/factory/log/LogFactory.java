package com.never_give_up.automation.demo.factory.log;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LogFactory {
    private final List<String> logs = new ArrayList<>();

    public void log(String content) {
        String time = LocalTime.now().withNano(0).toString();
        logs.add("[" + time + "] " + content);
    }

    public List<String> getLogs() { return logs; }
    public void reset() { logs.clear(); }
}