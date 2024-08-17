package src.plots;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Map;

public class StarCoordinatesPlot extends JFrame {

    private List<List<Double>> data;
    private List<String> attributeNames;
    private Map<String, Color> classColors;
    private Map<String, Shape> classShapes;
    private List<String> classLabels;
    private List<Integer> selectedRows;

    // Font settings
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font AXIS_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 16);

    public StarCoordinatesPlot(List<List<Double>> data, List<String> attributeNames, Map<String, Color> classColors, Map<String, Shape> classShapes, List<String> classLabels, List<Integer> selectedRows) {
        this.data = data;
        this.attributeNames = attributeNames;
        this.classColors = classColors;
        this.classShapes = classShapes;
        this.classLabels = classLabels;
        this.selectedRows = selectedRows;

        setTitle("Star Coordinates Plot");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the layout and background color of the main content pane
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Add the plot panel at the center
        add(new StarCoordinatesPanel(), BorderLayout.CENTER);

        // Add a legend panel at the bottom (horizontal)
        add(createLegendPanel(), BorderLayout.SOUTH);
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        legendPanel.setBackground(Color.WHITE);
    
        for (Map.Entry<String, Color> entry : classColors.entrySet()) {
            String className = entry.getKey();
            Color color = entry.getValue();
            Shape shape = classShapes.get(className);
    
            JPanel colorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            colorLabelPanel.setBackground(Color.WHITE);
    
            JLabel shapeLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(color);
                    g2.translate(20, 20);
                    g2.scale(3, 3);
                    g2.fill(shape);
                }
            };
            shapeLabel.setPreferredSize(new Dimension(50, 50));
    
            JLabel label = new JLabel(className);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
    
            colorLabelPanel.add(shapeLabel);
            colorLabelPanel.add(label);
    
            legendPanel.add(colorLabelPanel);
        }
    
        return legendPanel;
    }    

    private class StarCoordinatesPanel extends JPanel {
        private static final int TITLE_PADDING = 20;

        public StarCoordinatesPanel() {
            setBackground(new Color(0xC0C0C0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set the background color for the entire panel to white
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw the title above the grey background
            String title = "Star Coordinates Plot";
            g2.setFont(TITLE_FONT);
            FontMetrics fm = g2.getFontMetrics(TITLE_FONT);
            int titleWidth = fm.stringWidth(title);
            int titleHeight = fm.getHeight();
            g2.setColor(Color.BLACK);
            g2.drawString(title, (getWidth() - titleWidth) / 2, titleHeight);

            // Calculate plot area dimensions
            int plotAreaY = titleHeight + TITLE_PADDING;
            int plotAreaHeight = getHeight() - plotAreaY;

            // Set the background color for the plot area
            g2.setColor(new Color(0xC0C0C0));
            g2.fillRect(0, plotAreaY, getWidth(), plotAreaHeight);

            int plotSize = Math.min(getWidth(), plotAreaHeight) - 2 * 50;
            int centerX = getWidth() / 2;
            int centerY = plotAreaY + plotAreaHeight / 2;

            // Number of attributes excluding the class column
            int numAttributes = attributeNames.size();
            double angleIncrement = 2 * Math.PI / numAttributes;

            // Draw the star coordinates for each data point
            for (int row = 0; row < data.get(0).size(); row++) {
                if (!selectedRows.contains(row)) {
                    drawStar(g2, row, centerX, centerY, plotSize / 2, angleIncrement, false);
                }
            }

            // Highlight selected rows
            for (int row = 0; row < data.get(0).size(); row++) {
                if (selectedRows.contains(row)) {
                    drawStar(g2, row, centerX, centerY, plotSize / 2, angleIncrement, true);
                }
            }
        }

        private void drawStar(Graphics2D g2, int row, int centerX, int centerY, double radius, double angleIncrement, boolean highlight) {
            Path2D starPath = new Path2D.Double();
            for (int i = 0; i < attributeNames.size(); i++) {
                double value = data.get(i).get(row);
                double normValue = (value - getMin(data.get(i))) / (getMax(data.get(i)) - getMin(data.get(i)));
                double angle = i * angleIncrement;

                double x = centerX + radius * normValue * Math.cos(angle);
                double y = centerY - radius * normValue * Math.sin(angle);

                if (i == 0) {
                    starPath.moveTo(x, y);
                } else {
                    starPath.lineTo(x, y);
                }
            }

            starPath.closePath();

            if (highlight) {
                g2.setPaint(Color.YELLOW);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setPaint(classColors.get(classLabels.get(row)));
                g2.setStroke(new BasicStroke(1));
            }

            g2.draw(starPath);
        }

        private double getMin(List<Double> data) {
            return data.stream().min(Double::compare).orElse(Double.NaN);
        }

        private double getMax(List<Double> data) {
            return data.stream().max(Double::compare).orElse(Double.NaN);
        }
    }
}