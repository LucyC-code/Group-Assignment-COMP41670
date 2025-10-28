import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of sources: ");
        int numSources = sc.nextInt();

        System.out.print("Enter simulation duration (seconds): ");
        double endTime = sc.nextDouble();

        System.out.print("Enter Pareto alpha (1–2 typical): ");
        double alpha = sc.nextDouble();

        System.out.print("Enter Pareto xm (minimum value): ");
        double xm = sc.nextDouble();

        Simulator sim = new Simulator(endTime, numSources, alpha, xm);
        sim.run();
    }
}
