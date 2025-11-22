package com.telecom.sim;
import java.util.Random;

/**
 * Heavy-tailed ON/OFF generator using Pareto distributions.
 */
public class ParetoGenerator implements DurationGenerator {
    private final double alphaOn, xmOn;
    private final double alphaOff, xmOff;
    private final Random rng;

    public ParetoGenerator(double alphaOn, double xmOn,
                                double alphaOff, double xmOff,
                                long seed) {
        if (xmOn <= 0 || xmOff <= 0) {
            throw new IllegalArgumentException("xm must be > 0");
        }
        if (alphaOn <= 0 || alphaOff <= 0) {
            throw new IllegalArgumentException("alpha must be > 0");
        }
        this.alphaOn = alphaOn;
        this.xmOn = xmOn;
        this.alphaOff = alphaOff;
        this.xmOff = xmOff;
        this.rng = new Random(seed);
    }

    @Override
    public double nextOnDuration() {
        return pareto(xmOn, alphaOn);
    }

    @Override
    public double nextOffDuration() {
        return pareto(xmOff, alphaOff);
    }

    private double pareto(double xm, double alpha) {
        double u = 1.0 - rng.nextDouble();   // U in (0,1]
        return xm / Math.pow(u, 1.0 / alpha);
    }
}
