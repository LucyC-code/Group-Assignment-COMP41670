
import java.util.Random;

public class TrafficSource {
    private int id;
    private boolean isOn;
    private double nextEventTime;
    private double alpha;          // Pareto parameter
    private double xm;             // Minimum value for Pareto
    private Random random;

    // Constructor
    public TrafficSource(int id, boolean startOn, double alpha, double xm) {
        this.id = id;
        this.isOn = startOn;
        this.alpha = alpha;
        this.xm = xm;
        this.random = new Random();
        this.nextEventTime = 0.0;
    }

    // Pareto sample generator (inverse CDF)
    private double paretoSample() {
        double u = 1 - random.nextDouble();
        return xm / Math.pow(u, 1.0 / alpha);
    }

    public double getNextOnDuration() {
        return paretoSample();
    }

    public double getNextOffDuration() {
        return paretoSample();
    }

    public void switchState() {
        isOn = !isOn;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getId() {
        return id;
    }
}
