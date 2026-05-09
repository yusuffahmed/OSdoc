package scheduler;

import model.Process;
import model.GanttData;
import java.util.*;

public class PreemptivePriorityScheduler {
    // Preemptive Priority Scheduling
    public static List<GanttData> calculate(List<Process> processes) {
        List<GanttData> gantt = new ArrayList<>();
        int n = processes.size();
        int completed = 0;
        int currentTime = 0;
        int totalBurst = processes.stream().mapToInt(p -> p.bt).sum();
        int maxTime = totalBurst + processes.stream().mapToInt(p -> p.at).max().orElse(0) + 1;

        for (Process p : processes) {
            p.remainingBt = p.bt;
            p.rt = -1;
        }

        String lastId = null;
        int segStart = 0;

        for (currentTime = 0; currentTime <= maxTime && completed < n; currentTime++) {
            Process selected = null;
            for (Process p : processes) {
                if (p.at <= currentTime && p.remainingBt > 0) {
                    if (selected == null
                            || p.pri < selected.pri
                            || (p.pri == selected.pri && p.at < selected.at)
                            || (p.pri == selected.pri && p.at == selected.at && p.bt < selected.bt)) {
                        selected = p;
                    }
                }
            }

            if (selected == null) {
                if (lastId != null) {
                    gantt.add(new GanttData(lastId, segStart, currentTime));
                    lastId = null;
                }
                continue;
            }

            if (selected.rt == -1) {
                selected.rt = currentTime - selected.at;
            }

            if (!selected.id.equals(lastId)) {
                if (lastId != null) {
                    gantt.add(new GanttData(lastId, segStart, currentTime));
                }
                lastId = selected.id;
                segStart = currentTime;
            }

            selected.remainingBt--;

            if (selected.remainingBt == 0) {
                selected.ct = currentTime + 1;
                selected.tat = selected.ct - selected.at;
                selected.wt = selected.tat - selected.bt;
                completed++;
                gantt.add(new GanttData(selected.id, segStart, currentTime + 1));
                lastId = null;
            }
        }

        if (lastId != null) {
            gantt.add(new GanttData(lastId, segStart, currentTime));
        }

        return gantt;
    }
}
