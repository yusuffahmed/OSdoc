package scheduler;

import model.Process;
import model.GanttData;
import java.util.*;

public class SJFScheduler {
    // Non-Preemptive SJF
    public static List<GanttData> calculate(List<Process> processes) {
        List<GanttData> gantt = new ArrayList<>();
        List<Process> pool = new ArrayList<>(processes);
        List<Process> readyQueue = new ArrayList<>();
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();

        pool.sort(Comparator.comparingInt(p -> p.at));

        while (completed < n) {
            // Add all arrived processes to ready queue
            for (int i = 0; i < pool.size(); i++) {
                if (pool.get(i).at <= currentTime) {
                    readyQueue.add(pool.remove(i));
                    i--;
                }
            }

            if (readyQueue.isEmpty()) {
                if (!pool.isEmpty()) currentTime = pool.get(0).at;
                continue;
            }

            // Sort by BT, then AT as tie-breaker
            readyQueue.sort(Comparator.comparingInt((Process p) -> p.bt).thenComparingInt(p -> p.at));
            Process p = readyQueue.remove(0);

            if (p.rt == -1) p.rt = currentTime - p.at;
            gantt.add(new GanttData(p.id, currentTime, currentTime + p.bt));
            currentTime += p.bt;
            p.ct = currentTime;
            p.tat = p.ct - p.at;
            p.wt = p.tat - p.bt;
            completed++;
        }
        return gantt;
    }
}
