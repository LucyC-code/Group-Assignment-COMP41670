package com.telecom.sim;

import java.util.ArrayList;
import java.util.List;

public class HurstEstimator {

    public static double estimateHurst(double[] data) {
        int N = data.length;

        // Choose segment sizes
        int[] segmentSizes = {16, 32, 64, 128, 256, 512};

        List<Double> logN = new ArrayList<>();
        List<Double> logRS = new ArrayList<>();

        for (int n : segmentSizes) {
            if (n >= N) break;

            int numSegments = N / n;
            double RSsum = 0.0;

            for (int seg = 0; seg < numSegments; seg++) {
                double[] segment = new double[n];
                System.arraycopy(data, seg * n, segment, 0, n);

                RSsum += computeRS(segment);
            }

            double RSavg = RSsum / numSegments;

            logN.add(Math.log(n));
            logRS.add(Math.log(RSavg));
        }

        // Linear regression slope = H
        return linearRegressionSlope(logN, logRS);
    }

    private static double computeRS(double[] segment) {
        int n = segment.length;

        // Compute mean
        double mean = 0;
        for (double v : segment) mean += v;
        mean /= n;

        // Compute cumulative deviate
        double[] Y = new double[n];
        double cumulative = 0;
        for (int i = 0; i < n; i++) {
            cumulative += segment[i] - mean;
            Y[i] = cumulative;
        }

        // R = max(Y) - min(Y)
        double min = Y[0], max = Y[0];
        for (double v : Y) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        double R = max - min;

        // S = std deviation
        double sumSq = 0;
        for (double v : segment) sumSq += Math.pow(v - mean, 2);
        double S = Math.sqrt(sumSq / n);

        if (S == 0) return 0;
        return R / S;
    }

    private static double linearRegressionSlope(List<Double> x, List<Double> y) {
        int n = x.size();
        double sx = 0, sy = 0, sxy = 0, sxx = 0;

        for (int i = 0; i < n; i++) {
            sx += x.get(i);
            sy += y.get(i);
            sxy += x.get(i) * y.get(i);
            sxx += x.get(i) * x.get(i);
        }

        return (n * sxy - sx * sy) / (n * sxx - sx * sx);
    }
}
