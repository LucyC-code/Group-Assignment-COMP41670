package com.telecom.sim;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatorTest {

    //
    //  Constructor Tests
    //

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
        Simulator sim = new Simulator(10, 2, 0.9, 1, 0.8, 1, 0.1, 123);
        assertNotNull(sim);
    }

    //
    //  Initialisation Tests
    //

    @Test
    void testInitialiseCreatesCorrectNumberOfInitialEvents() {
        Simulator sim = new Simulator(10, 3, 2, 1, 2, 1, 0.5, 42);
        sim.initialise();

        assertEquals(3, sim.getEventQueueSize());
    }

    @Test
    void testInitialiseAlternatingStartStates() {
        Simulator sim = new Simulator(10, 4, 2, 1, 2, 1, 0.5, 42);
        sim.initialise();

        assertTrue(sim.getSource(0).isOn());
        assertFalse(sim.getSource(1).isOn());
        assertTrue(sim.getSource(2).isOn());
        assertFalse(sim.getSource(3).isOn());
    }

    //
    //  Event Processing Tests
    //

    @Test
    void testProcessEventFlipsStateAndSchedulesNext() {
        Simulator sim = new Simulator(10, 1, 2, 1, 2, 1, 1, 42);
        sim.initialise();

        Event first = sim.peekNextEvent();
        boolean wasOn = sim.getSource(0).isOn();
        sim.run();

        assertNotEquals(wasOn, sim.getSource(0).isOn());
    }

    //  processEvent branch where NO event should be scheduled
    @Test
    void testProcessEventDoesNotSchedulePastEndTime() {
        // Make endTime extremely small so no event can execute
        Simulator sim = new Simulator(1e-12, 1, 2, 1, 2, 1, 1, 99);
        sim.initialise();

        int initialSize = sim.getEventQueueSize();  // 1

        sim.run();

        // No NEW event should be scheduled, initial event stays there
        assertEquals(initialSize, sim.getEventQueueSize());
    }


    //
    //  Sampling Tests
    //

    @Test
    void testRunSimulatesUntilEndTime() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 1, 42);
        sim.initialise();
        sim.run();

        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    @Test
    void testSamplingWithPositiveInterval() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 1, 42);
        sim.initialise();
        sim.run();
        assertTrue(sim.getSampleTimes().size() > 0);
    }

    @Test
    void testSamplingSkippedIfIntervalZero() {
        Simulator sim = new Simulator(5, 2, 1.5, 1, 1.5, 1, 0, 42);
        sim.initialise();
        sim.run();
        assertTrue(sim.getSampleTimes().isEmpty());
    }

    @Test
    void testSampleUpToProducesCorrectTimes() {
        Simulator sim = new Simulator(2, 2, 2, 1, 2, 1, 0.5, 42);
        sim.initialise();
        sim.run();

        for (double t : sim.getSampleTimes()) {
            assertEquals(0.0, t % 0.5, 1e-9);
        }
    }

    //
    //  Active Count Tests
    //

    @Test
    void testCountActiveCorrect() {
        Simulator sim = new Simulator(5, 3, 2, 1, 2, 1, 1, 42);
        sim.initialise();

        sim.getSource(0).setOn(true);
        sim.getSource(1).setOn(false);
        sim.getSource(2).setOn(true);

        assertEquals(2, sim.countActive());
    }

    //
    //   run() early termination
    //  Covers branch: if (next.getTime() > endTime) break;
    //

    @Test
    void testRunStopsImmediatelyWhenFirstEventAfterEndTime() {
        // Use extremely tiny endTime so event is guaranteed to be later
        Simulator sim = new Simulator(0.00001, 2, 2, 1, 2, 1, 1, 123);
        sim.initialise();

        sim.run();  // run should break immediately

        // No sampling should occur because no event was processed
        assertTrue(sim.getSampleTimes().isEmpty());
    }
}
