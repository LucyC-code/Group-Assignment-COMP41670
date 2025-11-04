package com.telecom.sim;

import java.util.Random;

public class TrafficSource {
    private final int id;
    private boolean isOn;
    private final Random rng;
    private final double alphaOn, xmOn;
    private final double alphaOff, xmOff;

    // Constructor
    public TrafficSource(int id, boolean startOn,
                         double alphaOn, double xmOn,
                         double alphaOff, double xmOff,
                         long seed) {
        this.id = id;
        this.isOn = startOn;
        this.alphaOn = alphaOn;
        this.xmOn = xmOn;
        this.alphaOff = alphaOff;
        this.xmOff = xmOff;
        this.rng = new Random(seed);
    }

    public int getId() { return id; }
    public boolean isOn() { return isOn; }
    public void switchState() { isOn = !isOn; }
    public void setOn(boolean on) {this.isOn = on;}

    public double getNextOnDuration()  { return pareto(xmOn,  alphaOn); }
    public double getNextOffDuration() { return pareto(xmOff, alphaOff); }

    private double pareto(double xm, double alpha) {
        // X = xm / U^(1/alpha), U ~ (0,1]
        double u = 1.0 - rng.nextDouble();
        return xm / Math.pow(u, 1.0 / alpha);
    }
}