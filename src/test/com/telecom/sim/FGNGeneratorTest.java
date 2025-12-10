package com.telecom.sim;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class FGNGeneratorTest {

    @Test
    void generatesPositiveDurations() {
        DurationGenerator gen =
                new FGNGenerator(1.0, 1.0, 1.0, 1.0, 0.9, 123L);

        for (int i = 0; i < 200; i++) {
            double on  = gen.nextOnDuration();
            double off = gen.nextOffDuration();
            assertTrue(on  > 0.0, "ON duration should be > 0");
            assertTrue(off > 0.0, "OFF duration should be > 0");
        }
    }

    @Test
    void deterministicForSameSeed() {
        DurationGenerator gen1 =
                new FGNGenerator(1.0, 1.0, 1.0, 1.0, 0.9, 999L);
        DurationGenerator gen2 =
                new FGNGenerator(1.0, 1.0, 1.0, 1.0, 0.9, 999L);

        for (int i = 0; i < 100; i++) {
            assertEquals(gen1.nextOnDuration(),  gen2.nextOnDuration(),  1e-9);
            assertEquals(gen1.nextOffDuration(), gen2.nextOffDuration(), 1e-9);
        }
    }

    @Test
    void onDurationsHavePositiveCorrelation() {
        DurationGenerator gen =
                new FGNGenerator(1.0, 1.0, 1.0, 1.0, 0.9, 321L);

        List<Double> xs = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            xs.add(gen.nextOnDuration());
        }

        double corr = correlation(xs);
        assertTrue(corr > 0.1, "Expected positive correlation between successive ON durations");
    }

    // simple Pearson correlation of x[i] and x[i+1]
    private static double correlation(List<Double> xs) {
        int n = xs.size() - 1;
        double meanX = 0.0, meanY = 0.0;
        for (int i = 0; i < n; i++) {
            meanX += xs.get(i);
            meanY += xs.get(i + 1);
        }
        meanX /= n;
        meanY /= n;

        double num = 0.0, denX = 0.0, denY = 0.0;
        for (int i = 0; i < n; i++) {
            double dx = xs.get(i)     - meanX;
            double dy = xs.get(i + 1) - meanY;
            num  += dx * dy;
            denX += dx * dx;
            denY += dy * dy;
        }
        return num / Math.sqrt(denX * denY);
    }
}
