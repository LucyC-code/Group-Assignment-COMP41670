package com.telecom.sim;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final double endTime;
    private double currentTime = 0.0;
    private final int numSources;

    private final double alphaOn, xmOn, alphaOff, xmOff;
    private final double sampleInterval;
    private final long baseSeed;

    private final List<com.telecom.sim.TrafficSource> sources = new ArrayList<>();
    private final EventQueue eventQueue = new EventQueue();

    private double nextSampleTime = 0.0;
    private final List<Double> sampleTimes = new ArrayList<>();
    private final List<Integer> activeCounts = new ArrayList<>();

    public Simulator(double endTime,
                     int numSources,
                     double alphaOn, double xmOn,
                     double alphaOff, double xmOff,
                     double sampleInterval,
                     long baseSeed) {
        if (endTime <= 0) throw new IllegalArgumentException("endTime must be > 0");
        if (numSources < 1) throw new IllegalArgumentException("numSources must be >= 1");
        if (xmOn <= 0 || xmOff <= 0) throw new IllegalArgumentException("xm must be > 0");
        if (alphaOn <= 1.0 || alphaOff <= 1.0)
            System.err.println("Warning: alpha <= 1 gives infinite mean (OK for tests, but be aware).");

        this.endTime = endTime;
        this.numSources = numSources;
        this.alphaOn = alphaOn;   this.xmOn = xmOn;
        this.alphaOff = alphaOff; this.xmOff = xmOff;
        this.sampleInterval = sampleInterval;
        this.baseSeed = baseSeed;
    }

    public void initialize() {
        for (int i = 0; i < numSources; i++) {
            boolean startOn = (i % 2 == 0);
            TrafficSource src = new TrafficSource(i, startOn, alphaOn, xmOn, alphaOff, xmOff, baseSeed + i);
            sources.add(src);

            double dt = startOn ? src.getNextOnDuration() : src.getNextOffDuration();
            EventType firstType = startOn ? EventType.TURN_OFF : EventType.TURN_ON;
            eventQueue.addEvent(new Event(currentTime + dt, i, firstType));
        }
        if (sampleInterval > 0) nextSampleTime = sampleInterval;
    }



    public void run() {
        System.out.println("Starting simulation...");

        // This loop runs as long as there are events within the EventQueue
        while (!eventQueue.isEmpty()) {
            Event next = eventQueue.peek();                          // peek() tracks the next event time, if it is greater than the endTime, the loop stops.
            if (next.getTime() > endTime) break;

            sampleUpTo(next.getTime());   // no. sources are On at regular intervals up this time event, (it is called in before each event)

            Event e = eventQueue.getNextEvent();                     // Moves the simulator clock (currentTime) to the event's time,
            currentTime = e.getTime();                               //  and performs the state transition and scheduling of the next event
            processEvent(e);
        }

        // final sampling to endTime
        sampleUpTo(endTime);
        System.out.println("Simulation finished at t=" + Math.min(currentTime, endTime));
        printResults();
    }

    private void processEvent(Event e) {

        // 1. Locate Traffic source and update object's internal state
        TrafficSource src = sources.get(e.getSourceId());                //locating the traffic source belong to the current event, using SourceId
        boolean turnOn = (e.getType() == EventType.TURN_ON);             // if event type is Turn On, isOn is set to true else it is set to fasle
        src.setOn(turnOn);

        // Log state change
        System.out.printf("t=%.3f: src %d %s%n", currentTime, e.getSourceId(), turnOn ? "ON" : "OFF");

        // 2. Generate its next state duration using the heavy-tailed distribution
        double dt = turnOn ? src.getNextOnDuration() : src.getNextOffDuration();
        double tNext = currentTime + dt;

        // 3. Create and queue the corresponding  future event
        if (tNext <= endTime) {
            eventQueue.addEvent(new Event(
                    tNext,
                    e.getSourceId(),
                    turnOn ? EventType.TURN_OFF : EventType.TURN_ON
            ));
        }
    }

    private void sampleUpTo(double targetTime) {
        if (sampleInterval <= 0) return;
        while (nextSampleTime <= targetTime) {
            sampleTimes.add(nextSampleTime);
            activeCounts.add(countActive());
            nextSampleTime += sampleInterval;
        }
    }

    private int countActive() {
        int c = 0;
        for (TrafficSource s : sources) if (s.isOn()) c++;
        return c;
    }

    private void printResults() {
        if (activeCounts.isEmpty()) {
            System.out.println("No samples collected.");
            return;
        }
        int peak = 0; long sum = 0;
        for (int c : activeCounts) { sum += c; if (c > peak) peak = c; }
        double avg = sum / (double) activeCounts.size();
        System.out.printf("Samples: %d | Avg active: %.2f | Peak active: %d%n",
                activeCounts.size(), avg, peak);
    }

    // getters if you want to export CSV later
    public List<Double> getSampleTimes()  { return sampleTimes; }
    public List<Integer> getActiveCounts(){ return activeCounts; }
}