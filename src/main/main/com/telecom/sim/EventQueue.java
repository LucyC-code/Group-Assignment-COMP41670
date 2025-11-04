package com.telecom.sim;

import java.util.PriorityQueue;

public class EventQueue {
    private final PriorityQueue<Event> queue = new PriorityQueue<>();

    public void addEvent(Event e) { queue.add(e); }
    public Event getNextEvent() { return queue.poll(); }
    public Event peek() { return queue.peek(); }
    public boolean isEmpty() { return queue.isEmpty(); }
    public int size() { return queue.size(); }
    public void clear() { queue.clear(); }
}
