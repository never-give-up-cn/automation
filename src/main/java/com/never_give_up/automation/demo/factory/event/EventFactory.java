package com.never_give_up.automation.demo.factory.event;

import java.util.ArrayList;
import java.util.List;

public class EventFactory {
    public enum Type { SEND, RECEIVE, TIMEOUT, RETX, DROP, FORWARD, FRAGMENT }

    private final List<Event> events = new ArrayList<>();

    public static class Event {
        public Type type;
        public String message;
        public long time;
    }

    public void emit(Type t, String msg) {
        Event e = new Event();
        e.type = t;
        e.message = msg;
        e.time = System.currentTimeMillis();
        events.add(e);
    }

    public List<Event> getEvents() { return events; }
    public void reset() { events.clear(); }
}