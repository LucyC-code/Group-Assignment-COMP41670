import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private double currentTime;
    private double endTime;
    private List<TrafficSource> sources;
    private EventQueue eventQueue;

    public Simulator(double endTime, int numSources, double alpha, double xm) {
        this.endTime = endTime;
        this.sources = new ArrayList<>();
        this.eventQueue = new EventQueue();

        // Create sources and seed first events
        for (int i = 0; i < numSources; i++) {
            TrafficSource src = new TrafficSource(i, true, alpha, xm);
            sources.add(src);
            double nextTime = src.getNextOnDuration();
            eventQueue.addEvent(new Event(nextTime, i, "OFF"));
        }
    }

    public void run() {
        System.out.println("Starting simulation...");

        while (!eventQueue.isEmpty() && currentTime < endTime) {
            Event e = eventQueue.getNextEvent();
            currentTime = e.getTime();
            if (currentTime > endTime) break;

            TrafficSource src = sources.get(e.getSourceId());
            src.switchState();
            System.out.printf("Time %.2f: Source %d turned %s%n",
                    currentTime, e.getSourceId(), e.getType());

            // Schedule the next event for this source
            double duration = src.isOn() ? src.getNextOnDuration() : src.getNextOffDuration();
            eventQueue.addEvent(new Event(currentTime + duration, e.getSourceId(),
                    src.isOn() ? "OFF" : "ON"));
        }

        System.out.println("Simulation finished at time " + currentTime);
    }
}
