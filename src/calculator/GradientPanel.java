package calculator;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GradientPanel extends JPanel {

    private static final Color TOP_COLOR = new Color(210, 76, 75);
    private static final Color BOTTOM_COLOR = new Color(126, 29, 61);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, TOP_COLOR, 0, getHeight(), BOTTOM_COLOR));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
