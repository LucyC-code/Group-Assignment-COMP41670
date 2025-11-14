package com.telecom.sim;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            int numSources = (int) readNumber(sc, "Number of sources (or q to quit): ");
            double endTime = readNumber(sc, "Simulation duration (s): ");

            double alphaOn = readNumber(sc, "Pareto alpha ON: ");
            if (alphaOn <= 1.0) {
                System.out.println("alphaOn must be > 1 for a finite mean. Please enter a new value.");
                alphaOn = readNumber(sc, "Pareto alpha ON: ");
            }

            double xmOn    = readNumber(sc, "Pareto xm ON: ");

            double alphaOff = readNumber(sc, "Pareto alpha OFF: ");
            double xmOff    = readNumber(sc, "Pareto xm OFF: ");

            double sampleInterval = readNumber(sc, "Sampling interval (<=0 to disable): ");

            long baseSeed = 42L; // could also prompt if varying runs

            Simulator sim = new Simulator(
                    endTime, numSources,
                    alphaOn, xmOn, alphaOff, xmOff,
                    sampleInterval, baseSeed
            );
            sim.initialize();
            sim.run();
        } catch (Exception ex) {
            System.err.println("Input error: " + ex.getMessage());
        }
    }

    private static double readNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.next().trim();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                System.out.println("Exiting...");
                System.exit(0);
            }

            try {
                return Double.parseDouble(input);   // works for int or double
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number or 'q' to quit.");
            }
        }
    }

}
