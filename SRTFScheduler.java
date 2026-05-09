package scheduler;

import model.Process;
import model.GanttData;
import java.util.*;

public class SRTFScheduler {
    // Preemptive SJF = SRTF (Shortest Remaining Time First)
    public static List<GanttData> calculate(List<Process> processes) {
        List<GanttData> gantt = new ArrayList<>();
        int n = processes.size();
        int completed = 0;
        int currentTime = 0;
        int totalBurst = processes.stream().mapToInt(p -> p.bt).sum();
        int maxTime = totalBurst + processes.stream().mapToInt(p -> p.at).max().orElse(0) + 1;

        // Reset remaining burst
        for (Process p : processes) {
            p.remainingBt = p.bt;
            p.rt = -1;
        }

        String lastId = null;
        int segStart = 0;

        for (currentTime = 0; currentTime <= maxTime && completed < n; currentTime++) {
            // Find available process with shortest remaining time
            Process selected = null;
            for (Process p : processes) {
                if (p.at <= currentTime && p.remainingBt > 0) {
                    if (selected == null
                            || p.remainingBt < selected.remainingBt
                            || (p.remainingBt == selected.remainingBt && p.at < selected.at)) {
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

            // First response time
            if (selected.rt == -1) {
                selected.rt = currentTime - selected.at;
            }

            // Track Gantt segments
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
