package com.telecom.sim;

import java.util.Random;

public class TrafficSource {
    private final int id;
    private boolean isOn;
    private final DurationGenerator generator;

    public TrafficSource(int id,
                         boolean startOn,
                         DurationGenerator generator) {
        this.id = id;
        this.isOn = startOn;
        this.generator = generator;
    }

    public int getId() { return id; }
    public void switchState() {this.isOn = !this.isOn;}

    public boolean isOn() { return isOn; }

    public void setOn(boolean on) { this.isOn = on; }

    public double getNextOnDuration()  { return generator.nextOnDuration(); }

    public double getNextOffDuration() { return generator.nextOffDuration(); }

}