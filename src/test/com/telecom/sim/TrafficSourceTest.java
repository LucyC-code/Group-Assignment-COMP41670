package com.telecom.sim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TrafficSourceTest {

    // -------- Constructor + Getters --------

    @Test
    void testConstructorAndGetters() {
        DurationGenerator gen = new ParetoGenerator(2.0, 1.0, 3.0, 2.0, 12345L);
        TrafficSource src = new TrafficSource(1, true, gen);

        assertEquals(1, src.getId());
        assertTrue(src.isOn());
    }

    // -------- State switching --------

    @Test
    void testSwitchState() {
        DurationGenerator gen = new ParetoGenerator(2.0, 1.0, 3.0, 2.0, 12345L);
        TrafficSource src = new TrafficSource(1, true, gen);

        boolean before = src.isOn();
        src.switchState();
        assertNotEquals(before, src.isOn());
        src.switchState();
        assertEquals(before, src.isOn());
    }

    @Test
    void testSetOn() {
        DurationGenerator gen = new ParetoGenerator(2.0, 1.0, 3.0, 2.0, 12345L);
        TrafficSource src = new TrafficSource(1, true, gen);

        src.setOn(false);
        assertFalse(src.isOn());
        src.setOn(true);
        assertTrue(src.isOn());
    }

    // -------- ON/OFF Duration Wrapper Tests (Pareto) --------

    @Test
    void testGetNextOnDurationPositive_Pareto() {
        DurationGenerator gen = new ParetoGenerator(2.0, 1.0, 3.0, 2.0, 111L);
        TrafficSource src = new TrafficSource(1, true, gen);

        for (int i = 0; i < 10; i++) {
            assertTrue(src.getNextOnDuration() > 0);
        }
    }

    @Test
    void testGetNextOffDurationPositive_Pareto() {
        DurationGenerator gen = new ParetoGenerator(2.0, 1.0, 3.0, 2.0, 999L);
        TrafficSource src = new TrafficSource(1, true, gen);

        for (int i = 0; i < 10; i++) {
            assertTrue(src.getNextOffDuration() > 0);
        }
    }

    // -------- FGN integration tests --------

    @Test
    void testFGNGeneratorWorksWithTrafficSource() {
        DurationGenerator gen = new FGNGenerator(1.0, 1.0, 1.0, 1.0, 0.9, 123L);
        TrafficSource src = new TrafficSource(2, true, gen);

        double on  = src.getNextOnDuration();
        double off = src.getNextOffDuration();

        assertTrue(on > 0);
        assertTrue(off > 0);
    }

    @Test
    void testTrafficSourceUsesInjectedGenerator() {
        // Fake generator for controlled output
        DurationGenerator fakeGen = new DurationGenerator() {
            @Override
            public double nextOnDuration() { return 5.5; }
            @Override
            public double nextOffDuration() { return 7.7; }
        };

        TrafficSource src = new TrafficSource(1, true, fakeGen);

        assertEquals(5.5, src.getNextOnDuration(), 1e-9);
        assertEquals(7.7, src.getNextOffDuration(), 1e-9);
    }
}