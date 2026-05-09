package model;

public class GanttData {
    public String processId;
    public int startTime;
    public int endTime;

    public GanttData(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
