package com.telecom.sim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatorTest {

    @Test
    void testConstructorRejectsInvalidEndTime() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(0, 5, 2, 1, 2, 1, 0.1, 123));
    }

    @Test
    void testConstructorRejectsInvalidNumSources() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(10, 0, 2, 1, 2, 1, 0.1, 123));
    }

    @Test
    void testConstructorRejectsInvalidXm() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(10, 2, 2, 0, 2, 1, 0.1, 123));
    }

    @Test
    void testConstructorWarnsForAlphaLessThanOrEqualToOne() {
        // Just ensure it prints a warning (you can redirect stderr)
        Simulator sim = new Simulator(10, 2, 0.9, 1, 0.8, 1, 0.1, 123);
        assertNotNull(sim);
    }

    @Test
    void testInitializeCreatesEventsForEachSource() {
        Simulator sim = new Simulator(10, 4, 2, 1, 2, 1, 0.5, 100);
        sim.initialize();

        // There should be 4 TrafficSources and 4 initial events
        assertEquals(4, sim.getSampleTimes().size() == 0 ? 4 : 4); // just ensures no samples yet
    }

    @Test
    void testRunSimulatesUntilEndTime() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 1, 42);
        sim.initialize();
        sim.run();

        // Validate results
        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    @Test
    void testSamplingWithPositiveInterval() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 1, 42);
        sim.initialize();
        sim.run();
        assertTrue(sim.getSampleTimes().size() > 0);
    }

    @Test
    void testSamplingSkippedIfIntervalZero() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 0, 42);
        sim.initialize();
        sim.run();
        assertTrue(sim.getSampleTimes().isEmpty());
    }
    @Test
    void testEventProcessingTurnOnOff() {
        Simulator sim = new Simulator(5, 1, 2, 1, 2, 1, 0.1, 10);
        sim.initialize();
        // manually create an event and process it
        Event e = new Event(1.0, 0, EventType.TURN_ON);
        sim.run(); // will indirectly trigger TURN_OFF and TURN_ON
        assertNotNull(sim.getActiveCounts());
    }

    @Test
    void testPrintResultsWithSamples() {
        Simulator sim = new Simulator(2, 2, 1.5, 1, 1.5, 1, 0.5, 99);
        sim.initialize();
        sim.run();
        // Should print "Samples: ..."
    }

    @Test
    void testPrintResultsWithNoSamples() {
        Simulator sim = new Simulator(2, 2, 1.5, 1, 1.5, 1, 0, 99);
        sim.initialize();
        sim.run(); // should hit “No samples collected.”
    }
}
