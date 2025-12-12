package com.telecom.sim;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            int numSources;
            while (true) {
                numSources = (int) readNumber(sc, "Number of sources (or q to quit): ");
                if (numSources <= 0) {
                    System.out.println("Number of sources must be greater than 0.");
                } else {
                    break;
                }
            }
            double endTime;
            while (true) {
                endTime = readNumber(sc, "Simulation duration (s): ");
                if (endTime <= 0) {
                    System.out.println("Simulation duration must be greater than 0.");
                } else {
                    break;
                }
            }


            double alphaOn = readNumber(sc, "Pareto alpha ON: ");
            if (alphaOn <= 1.0) {
                System.out.println("alphaOn must be > 1 for a finite mean. Please enter a new value.");
                alphaOn = readNumber(sc, "Pareto alpha ON: ");
            }

            double xmOn;
            while (true) {
                xmOn = readNumber(sc, "Pareto xm ON: ");
                if (xmOn <= 0) {
                    System.out.println("Pareto xm ON must be greater than 0.");
                } else {
                    break;
                }
            }


            double alphaOff;
            while (true) {
                alphaOff = readNumber(sc, "Pareto alpha OFF: ");
                if (alphaOff <= 1.0) {
                    System.out.println("alphaOff must be > 1 for a finite mean. Please enter a new value.");
                } else {
                    break;
                }
            }

            double xmOff;
            while (true) {
                xmOff = readNumber(sc, "Pareto xm OFF: ");
                if (xmOff <= 0) {
                    System.out.println("Pareto xm OFF must be greater than 0.");
                } else {
                    break;
                }
            }


            double sampleInterval = readNumber(sc, "Sampling interval (<=0 to disable): ");

            int modelChoice;
            while (true) {
                modelChoice = (int) readNumber(sc, "Traffic model (1 = Pareto, 2 = FGN-like): ");
                if (modelChoice != 1 && modelChoice != 2) {
                    System.out.println("Traffic model must be 1 (Pareto) or 2 (FGN-like).");
                } else {
                    break;
                }
            }


            long baseSeed = 42L;

            Simulator sim = new Simulator(
                    endTime, numSources,
                    alphaOn, xmOn, alphaOff, xmOff,
                    sampleInterval, baseSeed,
                    modelChoice
            );
            sim.initialise();
            sim.run();

            // Export simulation data to CSV
                        writeCSV(
                                "simulation_output.csv",
                                sim.getSampleTimes(),
                                sim.getActiveCounts()
                        );

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
    private static void writeCSV(String fileName, List<Double> times, List<Integer> counts) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Time,ActiveSources\n");

            for (int i = 0; i < times.size(); i++) {
                writer.write(times.get(i) + "," + counts.get(i) + "\n");
            }

            System.out.println("CSV file created: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
