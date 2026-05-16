# Test Cases — CPU Scheduling Simulator

All test cases are built into the program and can be loaded via the scenario buttons (A, B, C, D) in the GUI.

---

## Metrics Formulas

| Metric | Formula |
|---|---|
| CT — Completion Time | Time when process finishes execution |
| TAT — Turnaround Time | CT − AT |
| WT — Waiting Time | TAT − BT |
| RT — Response Time | Start Time − AT |

---

## Scenario A — Basic Mixed Workload

**Description:** Basic mixed workload with different Arrival Times, Burst Times, and priorities.

### Input

| PID | AT | BT | Priority |
|---|---|---|---|
| P1 | 0 | 6 | 3 |
| P2 | 1 | 4 | 1 |
| P3 | 2 | 8 | 2 |
| P4 | 3 | 3 | 4 |
| P5 | 4 | 5 | 2 |

### Expected Output

**SJF (Non-Preemptive) — Execution Order: P1 → P4 → P2 → P5 → P3**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 6 | 3 | 6 | 6 | 0 | 0 |
| P2 | 1 | 4 | 1 | 13 | 12 | 8 | 8 |
| P3 | 2 | 8 | 2 | 26 | 24 | 16 | 16 |
| P4 | 3 | 3 | 4 | 9 | 6 | 3 | 3 |
| P5 | 4 | 5 | 2 | 18 | 14 | 9 | 9 |
| **Average** | | | | | **12.4** | **7.2** | **7.2** |

**Priority (Non-Preemptive) — Execution Order: P1 → P2 → P3 → P5 → P4**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 6 | 3 | 6 | 6 | 0 | 0 |
| P2 | 1 | 4 | 1 | 10 | 9 | 5 | 5 |
| P3 | 2 | 8 | 2 | 18 | 16 | 8 | 8 |
| P4 | 3 | 3 | 4 | 26 | 23 | 20 | 20 |
| P5 | 4 | 5 | 2 | 23 | 19 | 14 | 14 |
| **Average** | | | | | **14.6** | **9.4** | **9.4** |

**SRTF (Preemptive SJF) — Execution Order: P1 → P2 → P4 → P1(cont) → P5 → P3**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 6 | 3 | 13 | 13 | 7 | 0 |
| P2 | 1 | 4 | 1 | 5 | 4 | 0 | 0 |
| P3 | 2 | 8 | 2 | 26 | 24 | 16 | 16 |
| P4 | 3 | 3 | 4 | 8 | 5 | 2 | 2 |
| P5 | 4 | 5 | 2 | 18 | 14 | 9 | 9 |
| **Average** | | | | | **12.0** | **6.8** | **5.4** |

**Preemptive Priority — Execution Order: P1 → P2 → P3 → P5 → P1(cont) → P4**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 6 | 3 | 23 | 23 | 17 | 0 |
| P2 | 1 | 4 | 1 | 5 | 4 | 0 | 0 |
| P3 | 2 | 8 | 2 | 13 | 11 | 3 | 3 |
| P4 | 3 | 3 | 4 | 26 | 23 | 20 | 20 |
| P5 | 4 | 5 | 2 | 18 | 14 | 9 | 9 |
| **Average** | | | | | **15.0** | **9.8** | **6.4** |

**Summary — Scenario A**

| Algorithm | Avg WT | Avg TAT | Avg RT |
|---|---|---|---|
| SJF (Non-Preemptive) | 7.2 | 12.4 | 7.2 |
| Priority (Non-Preemptive) | 9.4 | 14.6 | 9.4 |
| SRTF (Preemptive SJF) | **6.8** ✅ | **12.0** ✅ | **5.4** ✅ |
| Preemptive Priority | 9.8 | 15.0 | 6.4 |

> ✅ SRTF achieves the best Avg WT (6.80) and Avg RT (5.40) by preempting P1 when shorter jobs arrive.

---

## Scenario B — BT vs Priority Conflict

**Description:** Demonstrates conflict between choosing shortest burst time vs highest priority. P1 has the longest BT but highest priority; P2 has the shortest BT but low priority.

### Input

| PID | AT | BT | Priority |
|---|---|---|---|
| P1 | 0 | 10 | 1 |
| P2 | 1 | 1 | 3 |
| P3 | 2 | 2 | 2 |
| P4 | 3 | 4 | 4 |
| P5 | 4 | 3 | 2 |

### Expected Output

**SJF (Non-Preemptive) — Execution Order: P1 → P2 → P3 → P5 → P4**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 10 | 1 | 10 | 10 | 0 | 0 |
| P2 | 1 | 1 | 3 | 11 | 10 | 9 | 9 |
| P3 | 2 | 2 | 2 | 13 | 11 | 9 | 9 |
| P4 | 3 | 4 | 4 | 20 | 17 | 13 | 13 |
| P5 | 4 | 3 | 2 | 16 | 12 | 9 | 9 |
| **Average** | | | | | **12.0** | **8.0** | **8.0** |

**Priority (Non-Preemptive) — Execution Order: P1 → P3 → P5 → P2 → P4**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 10 | 1 | 10 | 10 | 0 | 0 |
| P2 | 1 | 1 | 3 | 16 | 15 | 14 | 14 |
| P3 | 2 | 2 | 2 | 12 | 10 | 8 | 8 |
| P4 | 3 | 4 | 4 | 20 | 17 | 13 | 13 |
| P5 | 4 | 3 | 2 | 15 | 11 | 8 | 8 |
| **Average** | | | | | **12.6** | **8.6** | **8.6** |

**SRTF (Preemptive SJF) — Execution Order: P1 → P2 → P3 → P5 → P4 → P1(cont)**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 10 | 1 | 20 | 20 | 10 | 0 |
| P2 | 1 | 1 | 3 | 2 | 1 | 0 | 0 |
| P3 | 2 | 2 | 2 | 4 | 2 | 0 | 0 |
| P4 | 3 | 4 | 4 | 11 | 8 | 4 | 4 |
| P5 | 4 | 3 | 2 | 7 | 3 | 0 | 0 |
| **Average** | | | | | **6.8** | **2.8** | **0.8** |

**Preemptive Priority — Execution Order: P1 → P3 → P5 → P2 → P4 (same as Non-Preemptive)**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 10 | 1 | 10 | 10 | 0 | 0 |
| P2 | 1 | 1 | 3 | 16 | 15 | 14 | 14 |
| P3 | 2 | 2 | 2 | 12 | 10 | 8 | 8 |
| P4 | 3 | 4 | 4 | 20 | 17 | 13 | 13 |
| P5 | 4 | 3 | 2 | 15 | 11 | 8 | 8 |
| **Average** | | | | | **12.6** | **8.6** | **8.6** |

> Note: Since P1 has the highest priority (1), no other process can preempt it — result is identical to non-preemptive Priority.

**Summary — Scenario B**

| Algorithm | Avg WT | Avg TAT | Avg RT |
|---|---|---|---|
| SJF (Non-Preemptive) | 8.0 | 12.0 | 8.0 |
| Priority (Non-Preemptive) | 8.6 | 12.6 | 8.6 |
| SRTF (Preemptive SJF) | **2.8** ✅ | **6.8** ✅ | **0.8** ✅ |
| Preemptive Priority | 8.6 | 12.6 | 8.6 |

> ✅ SRTF dramatically outperforms all others by preempting P1 and letting short jobs run first.

---

## Scenario C — Starvation-Sensitive Case

**Description:** Demonstrates starvation risk. P4 has a long burst time and lowest priority, while all other processes have the highest priority.

### Input

| PID | AT | BT | Priority |
|---|---|---|---|
| P1 | 0 | 2 | 1 |
| P2 | 0 | 2 | 1 |
| P3 | 0 | 2 | 1 |
| P4 | 0 | 15 | 5 |
| P5 | 3 | 2 | 1 |

### Expected Output

All four algorithms produce identical results in this scenario:

**Execution Order (all algorithms): P1 → P2 → P3 → P5 → P4**

| Process | AT | BT | Priority | CT | TAT | WT | RT |
|---|---|---|---|---|---|---|---|
| P1 | 0 | 2 | 1 | 2 | 2 | 0 | 0 |
| P2 | 0 | 2 | 1 | 4 | 4 | 2 | 2 |
| P3 | 0 | 2 | 1 | 6 | 6 | 4 | 4 |
| P4 | 0 | 15 | 5 | 23 | 23 | 8 | 8 |
| P5 | 3 | 2 | 1 | 8 | 5 | 3 | 3 |
| **Average** | | | | | **8.0** | **3.4** | **3.4** |

**Summary — Scenario C**

| Algorithm | Avg WT | Avg TAT | Avg RT |
|---|---|---|---|
| SJF (Non-Preemptive) | 3.4 | 8.0 | 3.4 |
| Priority (Non-Preemptive) | 3.4 | 8.0 | 3.4 |
| SRTF (Preemptive SJF) | 3.4 | 8.0 | 3.4 |
| Preemptive Priority | 3.4 | 8.0 | 3.4 |

> ⚠️ All algorithms produce identical results because new high-priority arrivals are finite. In a real system with continuous high-priority short arrivals, P4 would **never** get CPU time (starvation). Solution: implement an **aging mechanism**.

---

## Scenario D — Validation & Error Handling

**Description:** Tests input validation. These inputs should all be **rejected** by the program.

| # | Test Input | Expected Result |
|---|---|---|
| 1 | PID=P1, AT=**-1**, BT=5, Pri=1 | ❌ Rejected — AT cannot be negative |
| 2 | PID=P1, AT=0, BT=**0**, Pri=1 | ❌ Rejected — BT must be > 0 |
| 3 | PID=P1, AT=**abc**, BT=5, Pri=1 | ❌ Rejected — AT must be numeric |
| 4 | Add P1 twice | ❌ Rejected — Process IDs must be unique |
