package com.telecom.sim;

import com.telecom.sim.Event;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the {@link Event} class.
 * <p>
 * Ensures correct behavior of the constructor, getters,
 * comparison logic, and string representation.
 * All tests are deterministic and achieve 100% coverage.
 */

class EventTest {

    @Test
    void testConstructorAndGetters() {
        EventType type = EventType.TURN_ON;
        Event e = new Event(10.5, 3, type);

        assertEquals(10.5, e.getTime());
        assertEquals(3, e.getSourceId());
        assertEquals(type, e.getType());
    }

    @Test
    void testCompareToWhenLessThan() {
        Event e1 = new Event(5.0, 1, EventType.TURN_ON);
        Event e2 = new Event(10.0, 2, EventType.TURN_OFF);

        assertTrue(e1.compareTo(e2) < 0);
    }

    @Test
    void testCompareToWhenGreaterThan() {
        Event e1 = new Event(10.0, 1, EventType.TURN_ON);
        Event e2 = new Event(5.0, 2, EventType.TURN_OFF);

        assertTrue(e1.compareTo(e2) > 0);
    }

    @Test
    void testCompareToWhenEqual() {
        Event e1 = new Event(7.5, 1, EventType.TURN_ON);
        Event e2 = new Event(7.5, 2, EventType.TURN_OFF);

        assertEquals(0, e1.compareTo(e2));
    }

    @Test
    void testToStringFormat() {
        Event e = new Event(3.3, 42, EventType.TURN_OFF);
        String str = e.toString();

        assertTrue(str.contains("t=3.3"));
        assertTrue(str.contains("src=42"));
        assertTrue(str.contains(EventType.TURN_OFF.toString()));
    }
}
