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
                new Simulator(
                        0.0,          // endTime (invalid)
                        5,            // numSources
                        2.0, 1.0,     // alphaOn, xmOn
                        2.0, 1.0,     // alphaOff, xmOff
                        0.1,          // sampleInterval
                        123L,         // baseSeed
                        1             // modelChoice = 1 (Pareto)
                ));
    }

    @Test
    void testConstructorRejectsInvalidNumSources() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(
                        10.0,
                        0,            // invalid numSources
                        2.0, 1.0,
                        2.0, 1.0,
                        0.1,
                        123L,
                        1
                ));
    }

    @Test
    void testConstructorRejectsInvalidXm() {
        assertThrows(IllegalArgumentException.class, () ->
                new Simulator(
                        10.0,
                        2,
                        2.0, 0.0,     // xmOn invalid <= 0
                        2.0, 1.0,
                        0.1,
                        123L,
                        1
                ));
    }


    @Test
    void testConstructorWarnsForAlphaLessThanOrEqualToOne() {
        Simulator sim = new Simulator(
                10.0,
                2,
                0.9, 1.0,        // alphaOn <= 1
                0.8, 1.0,        // alphaOff <= 1
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
                10.0,
                3,
                2.0, 1.0,
                2.0, 1.0,
                0.5,
                42L,
                1              // Pareto model
        );
        sim.initialise();

        assertEquals(3, sim.getEventQueueSize());
        assertNotNull(sim.peekNextEvent());
    }

    @Test
    void testInitialiseAlternatingStartStates() {
        Simulator sim = new Simulator(
                10.0,
                4,
                2.0, 1.0,
                2.0, 1.0,
                0.5,
                42L,
                1
        );
        sim.initialise();

        assertTrue(sim.getSource(0).isOn());
        assertFalse(sim.getSource(1).isOn());
        assertTrue(sim.getSource(2).isOn());
        assertFalse(sim.getSource(3).isOn());
    }

    @Test
    void testInitialiseCreatesCorrectNumberOfInitialEvents_FGN() {
        Simulator sim = new Simulator(
                10.0,
                3,
                1.5, 1.0,
                1.5, 1.0,
                0.5,
                42L,
                2              // FGN-like model
        );
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
                5.0,
                2,
                1.5, 1.0,
                1.5, 1.0,
                1.0,          // sample every 1s
                42L,
                1
        );
        sim.initialise();
        sim.run();

        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    @Test
    void testRunSimulatesUntilEndTime_FGN() {
        Simulator sim = new Simulator(
                5.0,
                2,
                1.5, 1.0,
                1.5, 1.0,
                1.0,
                42L,
                2
        );
        sim.initialise();
        sim.run();

        assertFalse(sim.getSampleTimes().isEmpty());
        assertEquals(sim.getSampleTimes().size(), sim.getActiveCounts().size());
    }

    // branch where next event time is after endTime (breaks immediately)
    @Test
    void testRunStopsImmediatelyWhenFirstEventAfterEndTime() {
        Simulator sim = new Simulator(
                1e-9,           // extremely tiny endTime
                2,
                2.0, 1.0,
                2.0, 1.0,
                1.0,
                123L,
                1
        );
        sim.initialise();
        sim.run();

        // no sampling should occur because no event processed
        assertTrue(sim.getSampleTimes().isEmpty());
    }

    //
    //  Sampling Tests
    //

    @Test
    void testSamplingWithPositiveInterval() {
        Simulator sim = new Simulator(
                5.0,
                2,
                1.5, 1.0,
                1.5, 1.0,
                1.0,
                42L,
                1
        );
        sim.initialise();
        sim.run();

        assertTrue(sim.getSampleTimes().size() > 0);
    }

    @Test
    void testSamplingSkippedIfIntervalZero() {
        Simulator sim = new Simulator(
                5.0,
                2,
                1.5, 1.0,
                1.5, 1.0,
                0.0,           // no sampling
                42L,
                1
        );
        sim.initialise();
        sim.run();

        assertTrue(sim.getSampleTimes().isEmpty());
    }

    @Test
    void testSampleUpToProducesCorrectTimes() {
        Simulator sim = new Simulator(
                2.0,
                2,
                2.0, 1.0,
                2.0, 1.0,
                0.5,          // sample every 0.5s
                42L,
                1
        );
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
                5.0,
                3,
                2.0, 1.0,
                2.0, 1.0,
                1.0,
                42L,
                1
        );
        sim.initialise();

        sim.getSource(0).setOn(true);
        sim.getSource(1).setOn(false);
        sim.getSource(2).setOn(true);

        assertEquals(2, sim.countActive());
    }
}
