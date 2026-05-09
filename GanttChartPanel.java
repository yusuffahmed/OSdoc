package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.GanttData;

public class GanttChartPanel extends JPanel {
    private List<GanttData> data;
    private Color[] COLORS = {
        new Color(70, 130, 200),   // blue
        new Color(60, 160, 100),   // green
        new Color(200, 100, 60),   // orange red
        new Color(150, 80, 200),   // purple
        new Color(200, 170, 50),   // yellow
        new Color(60, 180, 180),   // teal
        new Color(220, 80, 120),   // red
        new Color(100, 160, 60),   // light green
    };

    public void setData(List<GanttData> data) {
        this.data = data;
        this.revalidate();
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 80);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        
        g2.setColor(new Color(245, 247, 250));
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (data == null || data.isEmpty()) {
            g2.setColor(new Color(150, 160, 175));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.drawString("No data — run simulation first", 20, getHeight() / 2 + 4);
            return;
        }

        int paddingLeft = 8;
        int paddingRight = 8;
        int barY = 8;
        int barH = 36;
        int labelY = barY + barH + 14;

        int totalDuration = data.get(data.size() - 1).endTime;
        if (totalDuration == 0) return;
        double availableWidth = getWidth() - paddingLeft - paddingRight;
        double scale = availableWidth / totalDuration;

        
        java.util.Map<String, Integer> colorMap = new java.util.LinkedHashMap<>();
        int colorIdx = 0;
        for (GanttData d : data) {
            if (!colorMap.containsKey(d.processId)) {
                colorMap.put(d.processId, colorIdx % COLORS.length);
                colorIdx++;
            }
        }

        for (int i = 0; i < data.size(); i++) {
            GanttData d = data.get(i);
            int x = paddingLeft + (int) (d.startTime * scale);
            int w = Math.max(1, (int) ((d.endTime - d.startTime) * scale));

            
            Color baseColor = COLORS[colorMap.get(d.processId)];
            g2.setColor(baseColor);
            g2.fillRect(x, barY, w, barH);

            
            g2.setColor(baseColor.brighter().brighter());
            g2.fillRect(x, barY, w, 4);

            
            g2.setColor(baseColor.darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(x, barY, w, barH);

            
            if (w > 14) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String label = d.processId;
                int textX = x + (w - fm.stringWidth(label)) / 2;
                int textY = barY + barH / 2 + fm.getAscent() / 2 - 1;
                if (textX < x) textX = x + 2;
                g2.drawString(label, textX, textY);
            }

            
            g2.setColor(new Color(80, 90, 110));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.drawString(String.valueOf(d.startTime), x, labelY);
        }

        
        if (!data.isEmpty()) {
            GanttData last = data.get(data.size() - 1);
            int xEnd = paddingLeft + (int) (last.endTime * scale);
            g2.setColor(new Color(80, 90, 110));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(String.valueOf(last.endTime),
                    Math.min(xEnd, getWidth() - fm.stringWidth(String.valueOf(last.endTime)) - 2),
                    labelY);
        }
    }
}
