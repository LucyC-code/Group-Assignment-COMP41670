package com.telecom.sim;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Number of sources: ");
            int numSources = sc.nextInt();

            System.out.print("Simulation duration (s): ");
            double endTime = sc.nextDouble();

            System.out.print("Pareto alpha ON (e.g., 1.2): ");
            double alphaOn = sc.nextDouble();
            System.out.print("Pareto xm ON (e.g., 1.0): ");
            double xmOn = sc.nextDouble();

            System.out.print("Pareto alpha OFF (e.g., 1.2): ");
            double alphaOff = sc.nextDouble();
            System.out.print("Pareto xm OFF (e.g., 1.0): ");
            double xmOff = sc.nextDouble();

            System.out.print("Sampling interval (s, <=0 to disable): ");
            double sampleInterval = sc.nextDouble();

            long baseSeed = 42L; // could also prompt if you want varying runs

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
}
