package com.telecom.sim;

public final class Event implements Comparable<Event> {
    private final double time;
    private final int sourceId;
    private final EventType type;

    public Event(double time, int sourceId, EventType type) {
        this.time = time;
        this.sourceId = sourceId;
        this.type = type;
    }

    public double getTime() { return time; }
    public int getSourceId() { return sourceId; }
    public EventType getType() { return type; }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }

    @Override
    public String toString() {
        return "Event{t=" + time + ", src=" + sourceId + ", " + type + "}";
    }
}