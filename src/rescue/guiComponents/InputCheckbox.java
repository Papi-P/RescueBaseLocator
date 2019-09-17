package rescue.guiComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author Daniel Allen
 */
public class InputCheckbox extends JComponent {

    private boolean switchStyle = true;

    public InputCheckbox(int width, int height, String tooltip) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    public InputCheckbox(int width, int height) {
        init();
        this.setPreferredSize(new Dimension(width, height));
    }

    public InputCheckbox(String tooltip) {
        init();
    }

    public InputCheckbox() {
        init();
    }

    private void init() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clickEvent();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                enterEvent();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitEvent();
            }

        });
    }
    private int curve = 0;

    public InputCheckbox setCurve(int curve) {
        this.curve = curve;
        return this;
    }

    public int getCurve() {
        return this.curve;
    }

    private int curXPosOfSwitch = 0;
    //<editor-fold defaultstate="collapsed" desc="Painting">
    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     * Paints the component. This should not be called manually, and should
     * instead use <code>repaint()</code>
     *
     * @param g
     * @see repaint()
     */
    @Override
    public void paintComponent(Graphics g) {
        if (buffer.getWidth() != this.getWidth() || buffer.getHeight() != this.getHeight()) {
            buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        if (switchStyle) {
            g2d.setColor((this.selected ? Color.decode("#07e607") : Color.GRAY));
            g2d.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 1, curve, curve);
            g2d.setColor((this.selected ? Color.decode("#38A1F3") : Color.LIGHT_GRAY));
            g2d.fillRoundRect(curXPosOfSwitch + 1, 1, getWidth() / 2 - 1, getHeight() - 1, curve, curve);
        } else {
            g2d.setColor((this.selected ? Color.decode("#38A1F3") : Color.WHITE));
            g2d.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 1, curve, curve);
        }

        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }
    int currentPlaceholderPhaseShift = 0;

    /**
     * This should not be called manually. Use <code>repaint()</code> instead.
     *
     * @param g
     * @see repaint()
     */
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        g2d.setColor(borderColor);
        g2d.drawRoundRect(0, 0, getWidth(), getHeight(), curve, curve);
        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    public InputCheckbox setBorderColor(Color bordCol) {
        this.borderColor = bordCol;
        return this;
    }

    public InputCheckbox setBorderWeight(int weight) {
        this.borderWeight = weight;
        return this;
    }
    private int borderWeight = 1;
    private Color borderColor = Color.BLACK;
    private Shape fieldShape;
    private int knownCurve = curve;

    private boolean selected = false;

    public void toggleSelected() {
        if (isSelected()) {
            setSelected(false);
        } else {
            setSelected(true);
        }
    }

    public InputCheckbox setSelected(boolean sel) {
        boolean pre = this.selected;
        this.selected = sel;
        /*if (sel) {
         this.curXPosOfSwitch = this.getWidth() / 2;
         } else {
         this.curXPosOfSwitch = 0;
         }*/
        if (pre != sel) {
            repaint();
        }
        return this;
    }

    public boolean isSelected() {
        return this.selected;
    }

    private boolean antialias = false;

    public InputCheckbox setAntialiased(boolean alias) {
        this.antialias = alias;
        return this;
    }

    public boolean isAntialiased() {
        return this.antialias;
    }

    public void enterEvent() {

    }
    private double positionToSlideTo = 0;

    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    public void transitionPosition(long time) {

        if (isSelected()) {
            positionToSlideTo = this.getWidth() / 2;
        }
        if (ses == null || ses.isShutdown()) {
            ses = Executors.newSingleThreadScheduledExecutor();
        }
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (curXPosOfSwitch > positionToSlideTo && curXPosOfSwitch - positionToSlideTo >= 1) {
                    System.out.println("A");
                    curXPosOfSwitch--;
                    repaint();
                } else if (curXPosOfSwitch <= positionToSlideTo && curXPosOfSwitch - positionToSlideTo <= 1) {
                    System.out.println("B");
                    curXPosOfSwitch++;
                    repaint();
                } else {
                    System.out.println("Done");
                    ses.shutdown();
                }
            }
        }, 0, time, TimeUnit.MILLISECONDS);
    }

    public void clickEvent() {
        toggleSelected();
        transitionPosition(1);
    }

    public void exitEvent() {

    }
}
