package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import model.Process;
import model.GanttData;
import scheduler.*;
import metrics.MetricsCalculator;

public class MainGUI extends JFrame {

    // Colors
    private static final Color COLOR_BG        = new Color(236, 240, 245);
    private static final Color COLOR_LEFT_BG   = new Color(225, 232, 242);
    private static final Color COLOR_HEADER    = new Color(50, 80, 140);
    private static final Color COLOR_BTN_ADD   = new Color(80, 160, 100);
    private static final Color COLOR_BTN_RUN   = new Color(50, 100, 200);
    private static final Color COLOR_BTN_CLEAR = new Color(180, 60, 60);
    private static final Color COLOR_BTN_RESET = new Color(180, 60, 60);
    private static final Color COLOR_SCENARIO  = new Color(100, 130, 180);
    private static final Color COLOR_TABLE_HDR = new Color(210, 220, 235);
    private static final Color COLOR_AVG_ROW   = new Color(230, 240, 255);

    // ── Input fields ──────────────────────────────────────────────
    private JTextField fPID, fAT, fBT, fPRI;

    // ── Process input table ───────────────────────────────────────
    private DefaultTableModel inputModel;
    private JTable inputTable;

    // ── 4 Gantt charts ────────────────────────────────────────────
    private GanttChartPanel ganttSJFNon, ganttSRTF, ganttPrioPre, ganttPrioNon;

    // ── 4 Result tables ───────────────────────────────────────────
    private DefaultTableModel modelSJFNon, modelSRTF, modelPrioPre, modelPrioNon;
    private JTable tableSJFNon, tableSRTF, tablePrioPre, tablePrioNon;

    // ── Ready Queue Log ───────────────────────────────────────────
    private JTextArea readyQueueLog;

    // ── Conclusion ────────────────────────────────────────────────
    private JTextArea conclusionArea;

    // ── Status bar ────────────────────────────────────────────────
    private JLabel statusLabel;

    public MainGUI() {
        setTitle("CPU Scheduling: SJF & Priority — Non-Preemptive vs Preemptive");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_HEADER);
        titlePanel.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel titleLabel = new JLabel("CPU Scheduling Simulator — SJF & Priority (Non-Preemptive vs Preemptive)",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        
        JPanel leftPanel = buildLeftPanel();
        leftPanel.setPreferredSize(new Dimension(270, 0));
        add(leftPanel, BorderLayout.WEST);

        
        JPanel centerPanel = buildCenterPanel();
        JScrollPane centerScroll = new JScrollPane(centerPanel);
        centerScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerScroll.setBorder(null);
        add(centerScroll, BorderLayout.CENTER);

        
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        statusBar.setBackground(new Color(50, 60, 80));
        statusBar.setBorder(new EmptyBorder(2, 8, 2, 8));
        statusLabel = new JLabel("Ready. Add processes and click Run Simulation.");
        statusLabel.setForeground(new Color(200, 215, 240));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }


    private JPanel buildLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(COLOR_LEFT_BG);
        left.setBorder(new EmptyBorder(10, 10, 10, 10));

        
        JTextArea ruleNote = new JTextArea(
                "Priority Rule: Lower number = Higher priority\n(1 = most urgent)\nSJF Preemptive = SRTF (Shortest Remaining Time First)");
        ruleNote.setEditable(false);
        ruleNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ruleNote.setBackground(COLOR_LEFT_BG);
        ruleNote.setForeground(new Color(50, 60, 80));
        ruleNote.setBorder(new EmptyBorder(2, 2, 8, 2));
        left.add(ruleNote);

        addSeparator(left);

        
        JLabel procLabel = new JLabel("Processes:");
        procLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        procLabel.setForeground(new Color(40, 55, 90));
        procLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(procLabel);
        left.add(Box.createVerticalStrut(4));

        
        String[] inputCols = {"PID", "Arrival", "Burst", "Priority"};
        inputModel = new DefaultTableModel(inputCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        inputTable = new JTable(inputModel);
        inputTable.setRowHeight(22);
        inputTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        inputTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        inputTable.getTableHeader().setBackground(COLOR_TABLE_HDR);
        inputTable.setSelectionBackground(new Color(190, 210, 240));
        styleTable(inputTable, true);

        // Add delete-row button via right-click / delete key
        inputTable.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int row = inputTable.getSelectedRow();
                    if (row >= 0) inputModel.removeRow(row);
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputTable);
        inputScroll.setPreferredSize(new Dimension(250, 180));
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(inputScroll);
        left.add(Box.createVerticalStrut(8));

        // ── Input fields ──────────────────────────────────────────
        left.add(buildInputRow("PID", fPID = new JTextField()));
        left.add(Box.createVerticalStrut(3));
        left.add(buildInputRow("Arrival", fAT = new JTextField()));
        left.add(Box.createVerticalStrut(3));
        left.add(buildInputRow("Burst", fBT = new JTextField()));
        left.add(Box.createVerticalStrut(3));
        left.add(buildInputRow("Priority", fPRI = new JTextField()));
        left.add(Box.createVerticalStrut(8));

        // X button for each row (remove selected)
        JButton btnRemove = makeButton("✕ Remove Selected", new Color(160, 60, 60));
        btnRemove.addActionListener(e -> {
            int row = inputTable.getSelectedRow();
            if (row >= 0) inputModel.removeRow(row);
        });
        left.add(btnRemove);
        left.add(Box.createVerticalStrut(4));

        // Add Process button
        JButton btnAdd = makeButton("+ Add Process", COLOR_BTN_ADD);
        btnAdd.addActionListener(e -> addProcess());
        left.add(btnAdd);
        left.add(Box.createVerticalStrut(8));

        addSeparator(left);

        // Run Simulation
        JButton btnRun = makeButton("▶  Run Simulation", COLOR_BTN_RUN);
        btnRun.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRun.setPreferredSize(new Dimension(240, 34));
        btnRun.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnRun.addActionListener(e -> runSimulation());
        left.add(btnRun);
        left.add(Box.createVerticalStrut(4));

        // Clear
        JButton btnClear = makeButton("Clear", new Color(120, 130, 150));
        btnClear.addActionListener(e -> clearAll());
        left.add(btnClear);
        left.add(Box.createVerticalStrut(4));

        // Reset Input
        JButton btnReset = makeButton("Reset Input", COLOR_BTN_RESET);
        btnReset.addActionListener(e -> resetInput());
        left.add(btnReset);

        left.add(Box.createVerticalStrut(12));
        addSeparator(left);

        // Load Scenarios
        JLabel scenLabel = new JLabel("Load Scenario:");
        scenLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        scenLabel.setForeground(new Color(40, 55, 90));
        scenLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(scenLabel);
        left.add(Box.createVerticalStrut(6));

        String[][] scenarios = {
            {"Scenario A - Basic Mixed",       "A"},
            {"Scenario B - BT vs Priority Conflict", "B"},
            {"Scenario C - Starvation Case",   "C"},
            {"Scenario D - Validation Demo",   "D"},
        };
        for (String[] s : scenarios) {
            JButton btn = makeButton(s[0], COLOR_SCENARIO);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            final String code = s[1];
            btn.addActionListener(e -> loadScenario(code));
            left.add(btn);
            left.add(Box.createVerticalStrut(4));
        }

        left.add(Box.createVerticalGlue());

        return left;
    }

    private JPanel buildInputRow(String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(COLOR_LEFT_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel lbl = new JLabel(label + ":");
        lbl.setPreferredSize(new Dimension(58, 22));
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        field.setFont(new Font("SansSerif", Font.PLAIN, 11));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  CENTER PANEL
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(COLOR_BG);
        center.setBorder(new EmptyBorder(8, 8, 8, 8));

        // ── Gantt Charts section ──────────────────────────────────
        JLabel ganttTitle = sectionTitle("Gantt Charts");
        ganttTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(ganttTitle);
        center.add(Box.createVerticalStrut(6));

        // Top row: SJF Non-Pre | SRTF (Pre)
        JPanel ganttTopRow = new JPanel(new GridLayout(1, 2, 8, 0));
        ganttTopRow.setBackground(COLOR_BG);
        ganttTopRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        ganttSJFNon = new GanttChartPanel();
        ganttSRTF   = new GanttChartPanel();
        ganttTopRow.add(wrapGantt("SJF — Non-Preemptive", ganttSJFNon));
        ganttTopRow.add(wrapGantt("SJF — Preemptive (SRTF)", ganttSRTF));
        center.add(ganttTopRow);
        center.add(Box.createVerticalStrut(6));

        // Bottom row: Priority Pre | Priority Non-Pre
        JPanel ganttBotRow = new JPanel(new GridLayout(1, 2, 8, 0));
        ganttBotRow.setBackground(COLOR_BG);
        ganttBotRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        ganttPrioPre = new GanttChartPanel();
        ganttPrioNon = new GanttChartPanel();
        ganttBotRow.add(wrapGantt("Priority — Preemptive", ganttPrioPre));
        ganttBotRow.add(wrapGantt("Priority — Non-Preemptive", ganttPrioNon));
        center.add(ganttBotRow);
        center.add(Box.createVerticalStrut(10));

        // ── Ready Queue Log ───────────────────────────────────────
        JLabel rqTitle = sectionTitle("Ready Queue Log (all 4 algorithms)");
        rqTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(rqTitle);
        center.add(Box.createVerticalStrut(1));

        readyQueueLog = new JTextArea(7, 80);
        readyQueueLog.setEditable(false);
        readyQueueLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        readyQueueLog.setBackground(new Color(250, 252, 255));
        readyQueueLog.setBorder(new EmptyBorder(6, 8, 6, 8));
        readyQueueLog.setText("— SJF Non-Preemptive —\n(Run simulation to see ready queue log)");

        JScrollPane rqScroll = new JScrollPane(readyQueueLog);
        rqScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        rqScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(rqScroll);
        center.add(Box.createVerticalStrut(10));

        
        JLabel resTitle = sectionTitle("Results Tables");
        resTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(resTitle);
        center.add(Box.createVerticalStrut(4));

        String[] resCols = {"PID", "AT", "BT", "PR", "CT", "TAT", "WT", "RT"};

        
        modelSJFNon = new DefaultTableModel(resCols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tableSJFNon = makeResultTable(modelSJFNon);
        modelSRTF   = new DefaultTableModel(resCols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tableSRTF   = makeResultTable(modelSRTF);

        JPanel resTopRow = new JPanel(new GridLayout(1, 2, 8, 0));
        resTopRow.setBackground(COLOR_BG);
        resTopRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        resTopRow.add(wrapTable("SJF Non-Preemptive Results", tableSJFNon));
        resTopRow.add(wrapTable("SJF Preemptive (SRTF) Results", tableSRTF));
        center.add(resTopRow);
        center.add(Box.createVerticalStrut(6));

        
        modelPrioPre = new DefaultTableModel(resCols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tablePrioPre = makeResultTable(modelPrioPre);
        modelPrioNon = new DefaultTableModel(resCols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tablePrioNon = makeResultTable(modelPrioNon);

        JPanel resBotRow = new JPanel(new GridLayout(1, 2, 8, 0));
        resBotRow.setBackground(COLOR_BG);
        resBotRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        resBotRow.add(wrapTable("Priority Preemptive Results", tablePrioPre));
        resBotRow.add(wrapTable("Priority Non-Preemptive Results", tablePrioNon));
        center.add(resBotRow);
        center.add(Box.createVerticalStrut(12));

        // ── Conclusion ────────────────────────────────────────────
        JLabel concTitle = sectionTitle("Conclusion & Analysis Report");
        concTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(concTitle);
        center.add(Box.createVerticalStrut(4));

        conclusionArea = new JTextArea(14, 80);
        conclusionArea.setEditable(false);
        conclusionArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        conclusionArea.setBackground(new Color(250, 252, 255));
        conclusionArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        conclusionArea.setLineWrap(true);
        conclusionArea.setWrapStyleWord(true);
        conclusionArea.setText(getDefaultConclusion());

        JScrollPane concScroll = new JScrollPane(conclusionArea);
        concScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        concScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(concScroll);
        center.add(Box.createVerticalStrut(12));

        return center;
    }



    private void addProcess() {
        String pidTxt = fPID.getText().trim();
        if (pidTxt.isEmpty()) { showError("PID cannot be empty!"); return; }

        String formattedId = pidTxt.matches("\\d+") ? "P" + pidTxt : pidTxt;

        // Duplicate check
        for (int i = 0; i < inputModel.getRowCount(); i++) {
            if (inputModel.getValueAt(i, 0).toString().equalsIgnoreCase(formattedId)) {
                showError("Duplicate ID '" + formattedId + "'! Each process must have a unique ID.");
                return;
            }
        }

        int at, bt, pri;
        try {
            at  = Integer.parseInt(fAT.getText().trim());
            bt  = Integer.parseInt(fBT.getText().trim());
            pri = Integer.parseInt(fPRI.getText().trim());
        } catch (NumberFormatException e) {
            showError("AT, BT, and Priority must be numeric values!");
            return;
        }

        if (at < 0)  { showError("Arrival Time must be >= 0!"); return; }
        if (bt <= 0) { showError("Burst Time must be > 0!"); return; }
        if (pri < 0) { showError("Priority must be >= 0!"); return; }

        inputModel.addRow(new Object[]{formattedId, at, bt, pri});
        fPID.setText(""); fAT.setText(""); fBT.setText(""); fPRI.setText("");
        fPID.requestFocus();
        updateStatus("Process " + formattedId + " added. Total: " + inputModel.getRowCount());
    }

    private void runSimulation() {
        if (inputModel.getRowCount() == 0) {
            showError("Please add at least one process first.");
            return;
        }

        List<Process> base = collectProcesses();

        // Run all 4 algorithms on deep copies
        List<Process> listSJFNon = deepCopy(base);
        List<Process> listSRTF   = deepCopy(base);
        List<Process> listPriPre = deepCopy(base);
        List<Process> listPriNon = deepCopy(base);

        // Reset rt flags
        for (Process p : listSJFNon) p.rt = -1;
        for (Process p : listSRTF)   p.rt = -1;
        for (Process p : listPriPre) p.rt = -1;
        for (Process p : listPriNon) p.rt = -1;

        List<GanttData> gSJFNon = SJFScheduler.calculate(listSJFNon);
        List<GanttData> gSRTF   = SRTFScheduler.calculate(listSRTF);
        List<GanttData> gPriPre = PreemptivePriorityScheduler.calculate(listPriPre);
        List<GanttData> gPriNon = PriorityScheduler.calculate(listPriNon);

        // Update Gantt charts
        ganttSJFNon.setData(gSJFNon);
        ganttSRTF.setData(gSRTF);
        ganttPrioPre.setData(gPriPre);
        ganttPrioNon.setData(gPriNon);

        // Update result tables
        fillTable(modelSJFNon, listSJFNon);
        fillTable(modelSRTF,   listSRTF);
        fillTable(modelPrioPre, listPriPre);
        fillTable(modelPrioNon, listPriNon);

        // Build ready queue log
        readyQueueLog.setText(buildReadyQueueLog(base));

        
        conclusionArea.setText(buildConclusion(listSJFNon, listSRTF, listPriPre, listPriNon));
        conclusionArea.setCaretPosition(0);

        updateStatus("Done. Processes = " + base.size() + " | 4 algorithms ran successfully.");
    }

    private void clearAll() {
        inputModel.setRowCount(0);
        modelSJFNon.setRowCount(0); modelSRTF.setRowCount(0);
        modelPrioPre.setRowCount(0); modelPrioNon.setRowCount(0);
        ganttSJFNon.setData(null); ganttSRTF.setData(null);
        ganttPrioPre.setData(null); ganttPrioNon.setData(null);
        readyQueueLog.setText("— SJF Non-Preemptive —\n(Run simulation to see ready queue log)");
        conclusionArea.setText(getDefaultConclusion());
        resetInput();
        updateStatus("Cleared.");
    }

    private void resetInput() {
        fPID.setText(""); fAT.setText(""); fBT.setText(""); fPRI.setText("");
    }

    private void loadScenario(String code) {
        inputModel.setRowCount(0);
        switch (code) {
            case "A":
                // Scenario A: Basic mixed workload — different ATs, BTs, priorities
                inputModel.addRow(new Object[]{"P1", 0, 6, 3});
                inputModel.addRow(new Object[]{"P2", 1, 4, 1});
                inputModel.addRow(new Object[]{"P3", 2, 8, 2});
                inputModel.addRow(new Object[]{"P4", 3, 3, 4});
                inputModel.addRow(new Object[]{"P5", 4, 5, 2});
                updateStatus("Scenario A loaded — Basic Mixed Workload (5 processes).");
                break;
            case "B":
                // Scenario B: Short BT + low priority vs Long BT + high priority
                inputModel.addRow(new Object[]{"P1", 0, 10, 1});  
                inputModel.addRow(new Object[]{"P2", 1, 1,  3});  
                inputModel.addRow(new Object[]{"P3", 2, 2,  2}); 
                inputModel.addRow(new Object[]{"P4", 3, 4,  4});
                inputModel.addRow(new Object[]{"P5", 4, 3,  2});
                updateStatus("Scenario B loaded — BT vs Priority Conflict.");
                break;
            case "C":
                
                inputModel.addRow(new Object[]{"P1", 0, 2, 1});
                inputModel.addRow(new Object[]{"P2", 0, 2, 1});
                inputModel.addRow(new Object[]{"P3", 0, 2, 1});
                inputModel.addRow(new Object[]{"P4", 0, 15, 5}); 
                inputModel.addRow(new Object[]{"P5", 3, 2, 1});
                updateStatus("Scenario C loaded — Starvation-Sensitive Case.");
                break;
            case "D":
                
                JOptionPane.showMessageDialog(this,
                    "Scenario D — Validation Demo\n\n" +
                    "The following invalid inputs will be demonstrated:\n" +
                    "  1. Negative AT → Rejected\n" +
                    "  2. BT = 0 → Rejected\n" +
                    "  3. Non-numeric input → Rejected\n" +
                    "  4. Duplicate ID → Rejected\n\n" +
                    "Try adding: PID=P1, AT=-1, BT=5, Pri=1 → AT rejected\n" +
                    "Then:       PID=P1, AT=0, BT=0, Pri=1  → BT rejected\n" +
                    "Then:       PID=P1, AT=abc, BT=5, Pri=1 → non-numeric rejected\n" +
                    "Add P1 once, then add P1 again → duplicate rejected",
                    "Scenario D — Validation Demo",
                    JOptionPane.INFORMATION_MESSAGE);
                // Pre-load valid process so user can test duplicate
                inputModel.addRow(new Object[]{"P1", 0, 5, 2});
                fPID.setText("P1"); fAT.setText("0"); fBT.setText("3"); fPRI.setText("1");
                updateStatus("Scenario D — try clicking Add Process now (duplicate will be rejected).");
                break;
        }
    }

    
    private String buildReadyQueueLog(List<Process> base) {
        StringBuilder sb = new StringBuilder();

        // SJF Non-Preemptive log
        sb.append("— SJF Non-Preemptive ——\n");
        sb.append(buildSJFNonLog(deepCopy(base)));
        sb.append("\n");

        // SJF Preemptive (SRTF) log
        sb.append("— SJF Preemptive (SRTF) ——\n");
        sb.append(buildSRTFLog(deepCopy(base)));
        sb.append("\n");

        // Priority Non-Preemptive log
        sb.append("— Priority Non-Preemptive ——\n");
        sb.append(buildPrioNonLog(deepCopy(base)));
        sb.append("\n");

        // Priority Preemptive log
        sb.append("— Priority Preemptive ——\n");
        sb.append(buildPrioPreLog(deepCopy(base)));

        return sb.toString();
    }

    private String buildSJFNonLog(List<Process> processes) {
        StringBuilder sb = new StringBuilder();
        List<Process> pool = new ArrayList<>(processes);
        List<Process> readyQueue = new ArrayList<>();
        pool.sort(Comparator.comparingInt(p -> p.at));
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Set<Integer> logged = new HashSet<>();

        while (completed < n) {
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
            readyQueue.sort(Comparator.comparingInt((Process p) -> p.bt).thenComparingInt(p -> p.at));
            if (!logged.contains(currentTime)) {
                sb.append(String.format("t=%-3d | Ready: %s%n", currentTime, formatQueue(readyQueue)));
                logged.add(currentTime);
            }
            Process p = readyQueue.remove(0);
            p.rt = currentTime - p.at;
            currentTime += p.bt;
            p.ct = currentTime; p.tat = p.ct - p.at; p.wt = p.tat - p.bt;
            completed++;
        }
        return sb.toString();
    }

    private String buildSRTFLog(List<Process> processes) {
        StringBuilder sb = new StringBuilder();
        int n = processes.size();
        for (Process p : processes) { p.remainingBt = p.bt; p.rt = -1; }
        int completed = 0;
        int totalBurst = processes.stream().mapToInt(p -> p.bt).sum();
        int maxTime = totalBurst + processes.stream().mapToInt(p -> p.at).max().orElse(0) + 1;
        String lastId = null;
        int lastLogTime = -1;

        for (int t = 0; t < maxTime && completed < n; t++) {
            List<Process> ready = new ArrayList<>();
            for (Process p : processes)
                if (p.at <= t && p.remainingBt > 0) ready.add(p);

            if (ready.isEmpty()) continue;
            ready.sort(Comparator.comparingInt((Process p) -> p.remainingBt).thenComparingInt(p -> p.at));
            Process sel = ready.get(0);

            if (!sel.id.equals(lastId) && t != lastLogTime) {
                sb.append(String.format("t=%-3d | Ready: %s%n", t, formatQueueRem(ready)));
                lastLogTime = t;
            }
            lastId = sel.id;
            if (sel.rt == -1) sel.rt = t - sel.at;
            sel.remainingBt--;
            if (sel.remainingBt == 0) {
                sel.ct = t + 1; sel.tat = sel.ct - sel.at; sel.wt = sel.tat - sel.bt;
                completed++;
                lastId = null;
            }
        }
        return sb.toString();
    }

    private String buildPrioNonLog(List<Process> processes) {
        StringBuilder sb = new StringBuilder();
        List<Process> pool = new ArrayList<>(processes);
        List<Process> readyQueue = new ArrayList<>();
        pool.sort(Comparator.comparingInt(p -> p.at));
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();

        while (completed < n) {
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
            readyQueue.sort(Comparator.comparingInt((Process p) -> p.pri)
                .thenComparingInt(p -> p.at).thenComparingInt(p -> p.bt));
            sb.append(String.format("t=%-3d | Ready: %s%n", currentTime, formatQueuePri(readyQueue)));
            Process p = readyQueue.remove(0);
            p.rt = currentTime - p.at;
            currentTime += p.bt;
            p.ct = currentTime; p.tat = p.ct - p.at; p.wt = p.tat - p.bt;
            completed++;
        }
        return sb.toString();
    }

    private String buildPrioPreLog(List<Process> processes) {
        StringBuilder sb = new StringBuilder();
        int n = processes.size();
        for (Process p : processes) { p.remainingBt = p.bt; p.rt = -1; }
        int completed = 0;
        int totalBurst = processes.stream().mapToInt(p -> p.bt).sum();
        int maxTime = totalBurst + processes.stream().mapToInt(p -> p.at).max().orElse(0) + 1;
        String lastId = null;

        for (int t = 0; t < maxTime && completed < n; t++) {
            List<Process> ready = new ArrayList<>();
            for (Process p : processes)
                if (p.at <= t && p.remainingBt > 0) ready.add(p);

            if (ready.isEmpty()) continue;
            ready.sort(Comparator.comparingInt((Process p) -> p.pri)
                .thenComparingInt(p -> p.at).thenComparingInt(p -> p.bt));
            Process sel = ready.get(0);

            if (!sel.id.equals(lastId)) {
                sb.append(String.format("t=%-3d | Ready: %s%n", t, formatQueuePri(ready)));
            }
            lastId = sel.id;
            if (sel.rt == -1) sel.rt = t - sel.at;
            sel.remainingBt--;
            if (sel.remainingBt == 0) {
                sel.ct = t + 1; sel.tat = sel.ct - sel.at; sel.wt = sel.tat - sel.bt;
                completed++;
                lastId = null;
            }
        }
        return sb.toString();
    }

    private String formatQueue(List<Process> q) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(q.get(i).id).append("(BT=").append(q.get(i).bt).append(")");
        }
        return sb.toString();
    }

    private String formatQueueRem(List<Process> q) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(q.get(i).id).append("(rem=").append(q.get(i).remainingBt).append(")");
        }
        return sb.toString();
    }

    private String formatQueuePri(List<Process> q) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(q.get(i).id).append("(Pri=").append(q.get(i).pri).append(")");
        }
        return sb.toString();
    }


    private String buildConclusion(List<Process> sjfNon, List<Process> srtf,
                                   List<Process> priPre, List<Process> priNon) {
        double wtSJFNon = MetricsCalculator.getAvgWT(sjfNon);
        double tatSJFNon= MetricsCalculator.getAvgTAT(sjfNon);
        double rtSJFNon = MetricsCalculator.getAvgRT(sjfNon);

        double wtSRTF  = MetricsCalculator.getAvgWT(srtf);
        double tatSRTF = MetricsCalculator.getAvgTAT(srtf);
        double rtSRTF  = MetricsCalculator.getAvgRT(srtf);

        double wtPriPre = MetricsCalculator.getAvgWT(priPre);
        double tatPriPre= MetricsCalculator.getAvgTAT(priPre);
        double rtPriPre = MetricsCalculator.getAvgRT(priPre);

        double wtPriNon = MetricsCalculator.getAvgWT(priNon);
        double tatPriNon= MetricsCalculator.getAvgTAT(priNon);
        double rtPriNon = MetricsCalculator.getAvgRT(priNon);

        StringBuilder sb = new StringBuilder();

        sb.append("══════════════════════════════════════════════════════════════════════\n");
        sb.append("  1. ALGORITHM DESCRIPTIONS\n");
        sb.append("══════════════════════════════════════════════════════════════════════\n\n");
        sb.append("► SJF Non-Preemptive: Picks process with shortest BT from ready queue.\n");
        sb.append("  Once running, process cannot be interrupted until it completes.\n");
        sb.append("  Tie: equal BT → lower AT wins.\n\n");
        sb.append("► SJF Preemptive (SRTF): At every time unit, picks shortest REMAINING burst.\n");
        sb.append("  If a new process arrives with shorter remaining time, current is preempted.\n\n");
        sb.append("► Priority Non-Preemptive: Picks highest-priority process (lowest number).\n");
        sb.append("  Running process is never interrupted. Tie: lower AT wins.\n\n");
        sb.append("► Priority Preemptive: At every time unit, the highest-priority ready\n");
        sb.append("  process runs. A higher-priority arrival immediately preempts current.\n\n");

        sb.append("══════════════════════════════════════════════════════════════════════\n");
        sb.append("  2. METRICS SUMMARY\n");
        sb.append("══════════════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("  %-32s  AvgWT   AvgTAT  AvgRT%n", "Algorithm"));
        sb.append("  ────────────────────────────────────────────────────\n");
        sb.append(String.format("  %-32s  %-7.2f %-7.2f %.2f%n", "SJF Non-Preemptive", wtSJFNon, tatSJFNon, rtSJFNon));
        sb.append(String.format("  %-32s  %-7.2f %-7.2f %.2f%n", "SJF Preemptive (SRTF)",  wtSRTF,   tatSRTF,  rtSRTF));
        sb.append(String.format("  %-32s  %-7.2f %-7.2f %.2f%n", "Priority Preemptive",    wtPriPre, tatPriPre,rtPriPre));
        sb.append(String.format("  %-32s  %-7.2f %-7.2f %.2f%n", "Priority Non-Preemptive",wtPriNon, tatPriNon,rtPriNon));
        sb.append("\n");

        // Winner for WT
        double[] wts  = {wtSJFNon, wtSRTF, wtPriPre, wtPriNon};
        String[] names= {"SJF Non-Pre", "SRTF", "Priority Pre", "Priority Non-Pre"};
        int bestWT = 0;
        for (int i=1;i<4;i++) if(wts[i]<wts[bestWT]) bestWT=i;
        sb.append("  Best Avg Waiting Time  → ").append(names[bestWT])
          .append(String.format(" (%.2f)%n", wts[bestWT]));

        double[] rts = {rtSJFNon, rtSRTF, rtPriPre, rtPriNon};
        int bestRT = 0;
        for (int i=1;i<4;i++) if(rts[i]<rts[bestRT]) bestRT=i;
        sb.append("  Best Avg Response Time → ").append(names[bestRT])
          .append(String.format(" (%.2f)%n%n", rts[bestRT]));

        sb.append("══════════════════════════════════════════════════════════════════════\n");
        sb.append("  3. STARVATION ANALYSIS\n");
        sb.append("══════════════════════════════════════════════════════════════════════\n\n");
        appendStarvation(sb, "SJF Non-Pre", sjfNon);
        appendStarvation(sb, "SRTF",        srtf);
        appendStarvation(sb, "Priority Pre",priPre);
        appendStarvation(sb, "Priority Non",priNon);
        sb.append("\n");

        sb.append("══════════════════════════════════════════════════════════════════════\n");
        sb.append("  4. FORMULAS\n");
        sb.append("══════════════════════════════════════════════════════════════════════\n\n");
        sb.append("  CT  = Completion Time\n");
        sb.append("  TAT = CT - AT\n");
        sb.append("  WT  = TAT - BT\n");
        sb.append("  RT  = Time first started - AT\n\n");

        sb.append("══════════════════════════════════════════════════════════════════════\n");
        sb.append("  5. TRADE-OFF ANALYSIS\n");
        sb.append("══════════════════════════════════════════════════════════════════════\n\n");
        sb.append("  • SJF (both variants) minimizes average WT when all processes equal importance.\n");
        sb.append("  • SRTF is more efficient than SJF Non-Pre but causes more context switches.\n");
        sb.append("  • Priority scheduling ensures urgent/critical processes run first.\n");
        sb.append("  • Preemptive Priority is best for real-time systems needing fast urgent response.\n");
        sb.append("  • Neither algorithm implements AGING — low-priority / long processes may starve.\n\n");
        sb.append("  Recommendation:\n");
        sb.append("  • Batch systems (equal importance)  → SJF Non-Preemptive\n");
        sb.append("  • Interactive / real-time systems   → SRTF or Preemptive Priority\n");
        sb.append("  • Mixed urgency workloads           → Priority (Non-Pre or Pre)\n");

        return sb.toString();
    }

    private void appendStarvation(StringBuilder sb, String name, List<Process> list) {
        List<String> starved = new ArrayList<>();
        for (Process p : list)
            if (p.wt > 3 * p.bt) starved.add(p.id + "(WT=" + p.wt + ")");
        if (starved.isEmpty())
            sb.append("  ").append(name).append(": No starvation detected.\n");
        else
            sb.append("  ").append(name).append(": ⚠ Possible starvation → ")
              .append(String.join(", ", starved)).append("\n");
    }

    private String getDefaultConclusion() {
        return
            "══════════════════════════════════════════════════════════════════════\n" +
            "  CONCLUSION & ANALYSIS REPORT\n" +
            "══════════════════════════════════════════════════════════════════════\n\n" +
            "  Add processes or load a scenario and click Run Simulation\n" +
            "  to generate the full analysis report including:\n\n" +
            "    1. Algorithm descriptions (SJF Non-Pre, SRTF, Priority Pre, Priority Non-Pre)\n" +
            "    2. Metrics summary table (AvgWT, AvgTAT, AvgRT for all 4)\n" +
            "    3. Starvation analysis per algorithm\n" +
            "    4. Formulas (CT, TAT, WT, RT)\n" +
            "    5. Trade-off analysis & recommendation\n\n" +
            "  Use the Scenario buttons on the left to load preset test cases.\n";
    }


    private List<Process> collectProcesses() {
        List<Process> list = new ArrayList<>();
        for (int i = 0; i < inputModel.getRowCount(); i++) {
            String id = inputModel.getValueAt(i, 0).toString();
            int at  = Integer.parseInt(inputModel.getValueAt(i, 1).toString());
            int bt  = Integer.parseInt(inputModel.getValueAt(i, 2).toString());
            int pri = Integer.parseInt(inputModel.getValueAt(i, 3).toString());
            list.add(new Process(id, at, bt, pri));
        }
        return list;
    }

    private List<Process> deepCopy(List<Process> src) {
        List<Process> copy = new ArrayList<>();
        for (Process p : src) copy.add(new Process(p.id, p.at, p.bt, p.pri));
        return copy;
    }

    private void fillTable(DefaultTableModel model, List<Process> list) {
        model.setRowCount(0);
        double sumCT=0, sumTAT=0, sumWT=0, sumRT=0;
        for (Process p : list) {
            model.addRow(new Object[]{p.id, p.at, p.bt, p.pri, p.ct, p.tat, p.wt, p.rt});
            sumCT+=p.ct; sumTAT+=p.tat; sumWT+=p.wt; sumRT+=p.rt;
        }
        int n = list.size();
        if (n > 0) {
            model.addRow(new Object[]{"Avg", "-", "-", "-",
                String.format("%.2f", sumCT/n),
                String.format("%.2f", sumTAT/n),
                String.format("%.2f", sumWT/n),
                String.format("%.2f", sumRT/n)});
        }
    }

    private JTable makeResultTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(COLOR_TABLE_HDR);
        styleTable(table, false);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (row == t.getRowCount() - 1) {
                    c.setBackground(COLOR_AVG_ROW);
                    ((JLabel)c).setFont(((JLabel)c).getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
                }
                return c;
            }
        });
        return table;
    }

    private void styleTable(JTable table, boolean alternating) {
        table.setGridColor(new Color(210, 215, 225));
        table.setSelectionBackground(new Color(190, 210, 240));
        table.setIntercellSpacing(new Dimension(4, 2));
        table.setShowGrid(true);
    }

    private JPanel wrapGantt(String title, GanttChartPanel panel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(170, 185, 210)),
                title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(50, 80, 140)),
            new EmptyBorder(2, 2, 2, 2)));
        panel.setBackground(Color.WHITE);
        p.add(panel, BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapTable(String title, JTable table) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(170, 185, 210)),
            title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 11), new Color(50, 80, 140)));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(40, 60, 110));
        return l;
    }

    private JButton makeButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            new EmptyBorder(4, 10, 4, 10)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        b.setOpaque(true);
        // hover effect
        Color hoverBg = bg.brighter();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hoverBg); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private void addSeparator(JPanel panel) {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setForeground(new Color(180, 195, 215));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(8));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new MainGUI().setVisible(true);
        });
    }
}
