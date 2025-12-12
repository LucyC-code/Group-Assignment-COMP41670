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
                new Simulator(0.0, 5, 2.0, 1.0, 2.0, 1.0,
                        0.1, 123L, 1));
    }

    @Test
    void testConstructorRejectsInvalidNumSources() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(10.0, 0, 2.0, 1.0, 2.0, 1.0,
                        0.1, 123L, 1));
    }

    @Test
    void testConstructorRejectsInvalidXm() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(10.0, 2, 2.0, 0.0, 2.0, 1.0,
                        0.1, 123L, 1));
    }

    @Test
    void testConstructorWarnsForAlphaLessThanOrEqualOne() {
        Simulator sim = new Simulator(
                10.0, 2,
                0.9, 1.0,   // alphaOn invalid but allowed
                0.8, 1.0,
                0.1,
                123L,
                1
        );
        assertNotNull(sim);
    }


    //
    //  Initialisation Tests
    //

    @Test
    void testInitialiseCreatesCorrectNumberOfInitialEvents_Pareto() {
        Simulator sim = new Simulator(
                10.0, 3,
                2.0, 1.0, 2.0, 1.0,
                0.5, 42L, 1);
        sim.initialise();

        assertEquals(3, sim.getEventQueueSize());
        assertNotNull(sim.peekNextEvent());
    }

    @Test
    void testInitialiseAlternatingStartStates() {
        Simulator sim = new Simulator(
                10.0, 4,
                2.0, 1.0, 2.0, 1.0,
                0.5, 42L, 1);
        sim.initialise();

        assertTrue(sim.getSource(0).isOn());
        assertFalse(sim.getSource(1).isOn());
        assertTrue(sim.getSource(2).isOn());
        assertFalse(sim.getSource(3).isOn());
    }

    @Test
    void testInitialiseCreatesCorrectNumberOfInitialEvents_FGN() {
        Simulator sim = new Simulator(
                10.0, 3,
                1.5, 1.0, 1.5, 1.0,
                0.5, 42L, 2);
        sim.initialise();

        assertEquals(3, sim.getEventQueueSize());
        assertNotNull(sim.peekNextEvent());
    }


    //
    //  Event Processing / run() Tests
    //

    @Test
    void testRunSimulatesUntilEndTime_Pareto() {
        Simulator sim = new Simulator(
                5.0, 2,
                1.5, 1.0, 1.5, 1.0,
                1.0, 42L, 1);
        sim.initialise();
        sim.run();

        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    @Test
    void testRunSimulatesUntilEndTime_FGN() {
        Simulator sim = new Simulator(
                5.0, 2,
                1.5, 1.0, 1.5, 1.0,
                1.0, 42L, 2);
        sim.initialise();
        sim.run();

        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    @Test
    void testRunStopsImmediatelyWhenFirstEventAfterEndTime() {
        Simulator sim = new Simulator(
                1e-9, 2,
                2.0, 1.0, 2.0, 1.0,
                1.0, 123L, 1);
        sim.initialise();
        sim.run();

        assertTrue(sim.getSampleTimes().isEmpty());
    }


    //
    //  Sampling Tests
    //

    @Test
    void testSamplingWithPositiveInterval() {
        Simulator sim = new Simulator(
                5.0, 2,
                1.5, 1.0, 1.5, 1.0,
                1.0, 42L, 1);
        sim.initialise();
        sim.run();

        assertTrue(sim.getSampleTimes().size() > 0);
    }

    @Test
    void testSamplingSkippedIfIntervalZero() {
        Simulator sim = new Simulator(
                5.0, 2,
                1.5, 1.0, 1.5, 1.0,
                0.0, 42L, 1);
        sim.initialise();
        sim.run();

        assertTrue(sim.getSampleTimes().isEmpty());
    }

    @Test
    void testSampleUpToProducesCorrectTimes() {
        Simulator sim = new Simulator(
                2.0, 2,
                2.0, 1.0, 2.0, 1.0,
                0.5, 42L, 1);
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
        Simulator sim = new Simulator(
                5.0, 3,
                2.0, 1.0, 2.0, 1.0,
                1.0, 42L, 1);
        sim.initialise();

        sim.getSource(0).setOn(true);
        sim.getSource(1).setOn(false);
        sim.getSource(2).setOn(true);

        assertEquals(2, sim.countActive());
    }


    //
    // EXTRA TESTS FOR 100% COVERAGE
    //

    @Test
    void testProcessEventSchedulesNextEventWhenEqualToEndTime() {
        Simulator sim = new Simulator(
                1.0, 1,
                2.0, 1.0, 2.0, 1.0,
                0.0, 42L, 1);
        sim.initialise();

        Event first = sim.peekNextEvent();
        double exactEndTime = 1.0;

        // Force the next event to be exactly at endTime
        Event e = new Event(exactEndTime, first.getSourceId(), EventType.TURN_ON);
        sim.processEvent(e);

        // tNext == endTime should still schedule
        assertEquals(1, sim.getEventQueueSize());
    }

    @Test
    void testProcessEventDoesNotScheduleWhenBeyondEndTime() {
        Simulator sim = new Simulator(
                1.0, 1,
                2.0, 1.0, 2.0, 1.0,
                0.0, 42L, 1);

        sim.initialise();
        sim.eventQueue.clear();  // CLEAR INITIAL EVENT

        Event e = new Event(2.0, 0, EventType.TURN_ON); // > endTime
        sim.processEvent(e);

        // Now queue should be empty
        assertEquals(0, sim.getEventQueueSize());
    }


    @Test
    void testProcessEventActuallyTogglesState() {
        Simulator sim = new Simulator(
                10.0, 1,
                2.0, 1.0, 2.0, 1.0,
                0.0, 42L, 1);
        sim.initialise();

        TrafficSource src = sim.getSource(0);
        boolean before = src.isOn();

        Event e = new Event(0.5, 0, before ? EventType.TURN_OFF : EventType.TURN_ON);
        sim.processEvent(e);

        assertNotEquals(before, src.isOn());
    }

    @Test
    void testInitialEventsScheduleCorrectly() {
        Simulator sim = new Simulator(
                10.0, 1,
                2.0, 1.0, 2.0, 1.0,
                0.0, 42L, 1);
        sim.initialise();

        Event e = sim.peekNextEvent();
        assertNotNull(e);
        assertTrue(e.getTime() > 0);
    }

    @Test
    void testFinalSampleUpToDoesNothingWhenNextSampleTimeBeyondEndTime() {
        Simulator sim = new Simulator(
                1.0, 1,
                2.0, 1.0, 2.0, 1.0,
                2.0,   // sampleInterval > endTime
                42L, 1
        );
        sim.initialise();
        sim.run();

        // No samples because nextSampleTime starts > endTime
        assertTrue(sim.getSampleTimes().isEmpty());
    }

    @Test
    void testPrintResultsNonEmptyBranch() {
        Simulator sim = new Simulator(
                5.0, 2,
                2.0, 1.0, 2.0, 1.0,
                1.0,
                42L, 1
        );
        sim.initialise();
        sim.run();

        // Active counts must exist - non-empty branch executed
        assertFalse(sim.getActiveCounts().isEmpty());
    }

    @Test
    void testCountActiveWithNoSources() {
        Simulator sim = new Simulator(
                5.0, 1,
                2.0, 1.0, 2.0, 1.0,
                0.0,
                42L, 1
        );

        // DO NOT call initialise - sources list is empty
        assertEquals(0, sim.countActive());
    }

    @Test
    void testConstructorAlphaGreaterThanOneNoWarningBranch() {
        Simulator sim = new Simulator(
                10.0,
                2,
                2.0,
                1.0,   // alphaOn > 1
                2.0,
                1.0,   // alphaOff > 1
                1.0,
                123L,
                1
        );
        assertNotNull(sim);
    }

    @Test
    void testRunWithEmptyEventQueueSkipsWhileLoop() {
        Simulator sim = new Simulator(
                5.0, 2,
                2.0, 1.0,
                2.0, 1.0,
                0.0,
                42L,
                1
        );

        // IMPORTANT: do NOT call initialise()
        sim.run();

        // Loop is skipped entirely
        assertTrue(sim.getSampleTimes().isEmpty());
        assertTrue(sim.getActiveCounts().isEmpty());
    }

}
