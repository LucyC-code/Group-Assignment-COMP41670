package com.telecom.sim;
import java.util.Random;
/**
 * Alternative ON/OFF generator inspired by Fractional Gaussian Noise.
 *
 * It generates correlated Gaussian samples using an AR(1) process and then
 * maps them to positive durations via an exponential transform:
 *
 *   duration = base * exp(sigma * Z_n)
 *
 * This gives long-range-like dependence and bursty behaviour compared to
 * independent Pareto draws.
 */

public class FGNGenerator implements DurationGenerator {
    private final double baseOn, baseOff;
    private final double sigmaOn, sigmaOff;
    private final double phi;   // 0 < phi < 1, higher = more correlation
    private final Random rng;

    private double prevOnZ  = 0.0;
    private double prevOffZ = 0.0;

    public FGNGenerator(double baseOn, double sigmaOn,
                                 double baseOff, double sigmaOff,
                                 double phi,
                                 long seed) {
        if (phi <= 0 || phi >= 1) {
            throw new IllegalArgumentException("phi must be in (0,1)");
        }
        this.baseOn = baseOn;
        this.sigmaOn = sigmaOn;
        this.baseOff = baseOff;
        this.sigmaOff = sigmaOff;
        this.phi = phi;
        this.rng = new Random(seed);
    }

    @Override
    public double nextOnDuration() {
        prevOnZ = correlatedGaussian(prevOnZ);
        double duration = baseOn * Math.exp(sigmaOn * prevOnZ);
        return Math.max(duration, 1e-6); // avoid zero
    }

    @Override
    public double nextOffDuration() {
        prevOffZ = correlatedGaussian(prevOffZ);
        double duration = baseOff * Math.exp(sigmaOff * prevOffZ);
        return Math.max(duration, 1e-6);
    }

    private double correlatedGaussian(double prevZ) {
        double eps = rng.nextGaussian();                  // N(0,1)
        return phi * prevZ + Math.sqrt(1 - phi * phi) * eps;
    }
}
