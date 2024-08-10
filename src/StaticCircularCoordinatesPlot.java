package src;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.List;
import java.util.Map;

public class StaticCircularCoordinatesPlot extends JFrame {
    private List<List<Double>> data;
    private List<String> attributeNames;
    private Map<String, Color> classColors;
    private Map<String, Shape> classShapes;
    private List<String> classLabels;
    private List<Integer> selectedRows;
    private int numAttributes;

    public StaticCircularCoordinatesPlot(List<List<Double>> data, List<String> attributeNames, Map<String, Color> classColors, Map<String, Shape> classShapes, List<String> classLabels, List<Integer> selectedRows) {
        this.data = data;
        this.attributeNames = attributeNames;
        this.classColors = classColors;
        this.classShapes = classShapes;
        this.classLabels = classLabels;
        this.selectedRows = selectedRows;
        this.numAttributes = attributeNames.size();
        setTitle("Static Circular Coordinates");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        int radius = 300;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double angleStep = 2 * Math.PI / numAttributes;

        Point2D.Double[] attributePositions = new Point2D.Double[numAttributes];

        // Draw the circular axis
        g2.setColor(Color.BLACK);
        g2.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius));

        // Calculate positions on the circumference for each attribute
        for (int i = 0; i < numAttributes; i++) {
            double angle = i * angleStep - Math.PI / 2;  // Start at the top (12 o'clock)
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            attributePositions[i] = new Point2D.Double(x, y);

            // Draw attribute labels
            g2.drawString(attributeNames.get(i), (int) x, (int) y);
        }

        // Draw non-selected rows first
        for (int row = 0; row < data.get(0).size(); row++) {
            if (!selectedRows.contains(row)) {
                drawRow(g2, row, attributePositions, centerX, centerY, radius, angleStep, false);
            }
        }

        // Draw selected rows last (highlighted in yellow)
        for (int row : selectedRows) {
            drawRow(g2, row, attributePositions, centerX, centerY, radius, angleStep, true);
        }
    }

    private void drawRow(Graphics2D g2, int row, Point2D.Double[] attributePositions, int centerX, int centerY, int radius, double angleStep, boolean isSelected) {
        Point2D.Double[] points = new Point2D.Double[numAttributes];
        String classLabel = classLabels.get(row);
        Color color = isSelected ? Color.YELLOW : classColors.getOrDefault(classLabel, Color.BLACK);
        Shape shape = classShapes.getOrDefault(classLabel, new Ellipse2D.Double(-3, -3, 6, 6));

        // Calculate points
        for (int i = 0; i < numAttributes; i++) {
            double value = data.get(i).get(row);
            double normValue = value;  // Data is already normalized to [0, 1]

            // Interpolate between the start and end points of each segment on the circumference
            double x = centerX + radius * Math.cos(i * angleStep + normValue * angleStep - Math.PI / 2);
            double y = centerY + radius * Math.sin(i * angleStep + normValue * angleStep - Math.PI / 2);
            points[i] = new Point2D.Double(x, y);
        }

        // Connect points sequentially with class color or yellow if selected
        g2.setColor(color);
        for (int i = 0; i < numAttributes - 1; i++) {
            g2.draw(new Line2D.Double(points[i], points[i + 1]));
        }
        g2.draw(new Line2D.Double(points[numAttributes - 1], points[0]));

        // Draw the shapes at the points
        for (int i = 0; i < numAttributes; i++) {
            g2.translate(points[i].x, points[i].y);  // Move the origin to the point location
            g2.fill(shape);       // Draw the shape at the translated origin
            g2.translate(-points[i].x, -points[i].y); // Move back the origin
        }
    }
}
