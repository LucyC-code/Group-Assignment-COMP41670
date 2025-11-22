package com.telecom.sim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TrafficSourceTest {

    private TrafficSource source;

    @BeforeEach
    void setup() {
        source = new TrafficSource(
                1,
                true,
                2.0, 1.0,       // alphaOn, xmOn
                3.0, 2.0,       // alphaOff, xmOff
                12345L          // seed
        );
    }

    // -------- Constructor + Getters --------

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, source.getId());
        assertTrue(source.isOn());
    }

    // -------- State switching --------

    @Test
    void testSwitchState() {
        boolean before = source.isOn();
        source.switchState();
        assertNotEquals(before, source.isOn());
        source.switchState();
        assertEquals(before, source.isOn());
    }

    @Test
    void testSetOn() {
        source.setOn(false);
        assertFalse(source.isOn());
        source.setOn(true);
        assertTrue(source.isOn());
    }

    // -------- Pareto Behaviour Tests --------

    @Test
    void testParetoValuesArePositive() throws Exception {
        Method m = TrafficSource.class.getDeclaredMethod(
                "pareto", double.class, double.class
        );
        m.setAccessible(true);

        double v1 = (double) m.invoke(source, 1.0, 2.0);
        double v2 = (double) m.invoke(source, 1.0, 2.0);

        assertTrue(v1 > 0);
        assertTrue(v2 > 0);

        // Should differ because RNG moves forward
        assertNotEquals(v1, v2);
    }

    @Test
    void testDeterministicSeedProducesSameOutput() throws Exception {
        TrafficSource s1 = new TrafficSource(1, true, 2,1, 3,2, 999L);
        TrafficSource s2 = new TrafficSource(1, true, 2,1, 3,2, 999L);

        Method m = TrafficSource.class.getDeclaredMethod(
                "pareto", double.class, double.class
        );
        m.setAccessible(true);

        double a = (double) m.invoke(s1, 1.0, 2.0);
        double b = (double) m.invoke(s2, 1.0, 2.0);

        assertEquals(a, b, 1e-12);
    }

    // -------- ON/OFF Duration Wrapper Tests --------

    @Test
    void testGetNextOnDurationPositive() {
        for (int i = 0; i < 10; i++) {
            assertTrue(source.getNextOnDuration() > 0);
        }
    }

    @Test
    void testGetNextOffDurationPositive() {
        for (int i = 0; i < 10; i++) {
            assertTrue(source.getNextOffDuration() > 0);
        }
    }
}
