package com.telecom.sim;

import java.util.Random;

/**
 * Generates ON and OFF durations using a correlated stochastic process inspired
 * by Fractional Gaussian Noise (FGN).
 * <p>
 * Correlation between successive durations is introduced using an AR(1) process,
 * and durations are mapped to positive values using an exponential transform.
 * This produces bursty traffic with temporal dependence.
 * </p>
 */
public class FGNGenerator implements DurationGenerator {

    /** Base scale for ON durations */
    private final double baseOn;

    /** Base scale for OFF durations */
    private final double baseOff;

    /** Variability of ON durations */
    private final double sigmaOn;

    /** Variability of OFF durations */
    private final double sigmaOff;

    /** Correlation coefficient (0 < phi < 1) */
    private final double phi;

    /** Random number generator */
    private final Random rng;

    /** Previous Gaussian value for ON durations */
    private double prevOnZ = 0.0;

    /** Previous Gaussian value for OFF durations */
    private double prevOffZ = 0.0;

    /**
     * Creates a new FGN-like duration generator.
     *
     * @param baseOn   base ON duration scale
     * @param sigmaOn  ON duration variability
     * @param baseOff  base OFF duration scale
     * @param sigmaOff OFF duration variability
     * @param phi      correlation coefficient (0 < phi < 1)
     * @param seed     random seed
     * @throws IllegalArgumentException if {@code phi} is not in (0,1)
     */
    public FGNGenerator(double baseOn, double sigmaOn,
                        double baseOff, double sigmaOff,
                        double phi, long seed) {

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

    /**
     * Generates the next ON duration.
     *
     * @return next ON duration (always positive)
     */
    @Override
    public double nextOnDuration() {
        prevOnZ = correlatedGaussian(prevOnZ);
        double duration = baseOn * Math.exp(sigmaOn * prevOnZ);
        return Math.max(duration, 1e-6);
    }

    /**
     * Generates the next OFF duration.
     *
     * @return next OFF duration (always positive)
     */
    @Override
    public double nextOffDuration() {
        prevOffZ = correlatedGaussian(prevOffZ);
        double duration = baseOff * Math.exp(sigmaOff * prevOffZ);
        return Math.max(duration, 1e-6);
    }

    /**
     * Generates a correlated Gaussian sample using an AR(1) process.
     *
     * @param prevZ previous Gaussian value
     * @return correlated Gaussian value
     */
    private double correlatedGaussian(double prevZ) {
        double eps = rng.nextGaussian();
        return phi * prevZ + Math.sqrt(1 - phi * phi) * eps;
    }
}
