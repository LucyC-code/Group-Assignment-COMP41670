package com.telecom.sim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the {@link TrafficSource} class.
 * <p>
 * These tests verify correct construction, state switching,
 * random duration generation, deterministic seeding, and
 * internal Pareto distribution behavior.
 * All tests are designed for 100% line and branch coverage.
 */

class TrafficSourceTest {

    private TrafficSource source;

    @BeforeEach
    void setUp() {
        source = new TrafficSource(
                1,          // id
                true,       // startOn
                2.0, 1.0,   // alphaOn, xmOn
                3.0, 2.0,   // alphaOff, xmOff
                12345L      // seed
        );
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, source.getId());
        assertTrue(source.isOn());
    }

    @Test
    void testSwitchStateTogglesCorrectly() {
        boolean initial = source.isOn();
        source.switchState();
        assertNotEquals(initial, source.isOn());
        source.switchState();
        assertEquals(initial, source.isOn()); // toggles back
    }

    @Test
    void testGetNextOnDurationProducesPositiveValues() {
        for (int i = 0; i < 10; i++) {
            double val = source.getNextOnDuration();
            assertTrue(val > 0, "Expected positive ON duration");
        }
    }

    @Test
    void testGetNextOffDurationProducesPositiveValues() {
        for (int i = 0; i < 10; i++) {
            double val = source.getNextOffDuration();
            assertTrue(val > 0, "Expected positive OFF duration");
        }
    }

    @Test
    void testParetoDistributionFormulaIsUsed() throws Exception {
        // Access private pareto() method via reflection
        var method = TrafficSource.class.getDeclaredMethod("pareto", double.class, double.class);
        method.setAccessible(true);

        double result = (double) method.invoke(source, 1.0, 2.0);
        assertTrue(result > 0, "Pareto result should be positive");

        // Check that the output changes with RNG
        double result2 = (double) method.invoke(source, 1.0, 2.0);
        assertNotEquals(result, result2, "Two calls should differ due to RNG");
    }

    @Test
    void testRandomnessUsesProvidedSeed() throws Exception {
        // Two instances with same seed should produce same values
        TrafficSource s1 = new TrafficSource(1, true, 2.0, 1.0, 3.0, 2.0, 999L);
        TrafficSource s2 = new TrafficSource(2, true, 2.0, 1.0, 3.0, 2.0, 999L);

        double v1 = s1.getNextOnDuration();
        double v2 = s2.getNextOnDuration();
        assertEquals(v1, v2, 1e-12, "Same seed → same random sequence");
    }
}
