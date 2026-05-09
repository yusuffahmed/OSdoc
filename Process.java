package model;

public class Process {
    public String id;
    public int at, bt, pri;
    public int ct, tat, wt, rt;
    public int remainingBt; // for preemptive

    public Process(String id, int at, int bt, int pri) {
        this.id = id;
        this.at = at;
        this.bt = bt;
        this.pri = pri;
        this.remainingBt = bt;
    }
}
