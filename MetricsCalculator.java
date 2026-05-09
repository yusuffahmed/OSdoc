package metrics;

import model.Process;
import java.util.List;

public class MetricsCalculator {
    public static double getAvgWT(List<Process> list) {
        return list.stream().mapToInt(p -> p.wt).average().orElse(0);
    }
    public static double getAvgTAT(List<Process> list) {
        return list.stream().mapToInt(p -> p.tat).average().orElse(0);
    }
    public static double getAvgRT(List<Process> list) {
        return list.stream().mapToInt(p -> p.rt).average().orElse(0);
    }
}
