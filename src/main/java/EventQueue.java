import java.util.PriorityQueue;

public class EventQueue {
    private PriorityQueue<Event> queue;

    public EventQueue() {
        queue = new PriorityQueue<>();
    }

    public void addEvent(Event e) {
        queue.add(e);
    }

    public Event getNextEvent() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
