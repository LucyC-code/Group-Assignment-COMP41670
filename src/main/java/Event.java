public class Event implements Comparable<Event> {
    private double time;
    private int sourceId;
    private String type;  // "ON" or "OFF"
// hello
    public Event(double time, int sourceId, String type) {
        this.time = time;
        this.sourceId = sourceId;
        this.type = type;
    }

    public double getTime() {
        return time;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getType() {
        return type;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }
}
