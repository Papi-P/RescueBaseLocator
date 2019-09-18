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
import javax.swing.JComponent;

/**
 *
 * @author Daniel Allen
 */
public class InputCheckbox extends JComponent {

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

    private boolean switchStyle = true;

    public InputCheckbox setSwitch(boolean s) {
        this.switchStyle = s;
        return this;
    }

    public boolean isSwitch() {
        return this.switchStyle;
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

    public void setKnobColors(Color active, Color inactive) {
        this.ACTIVE_KNOB = inactive;
        this.INACTIVE_KNOB = inactive;
    }

    public void setSlideColors(Color active, Color inactive) {
        this.ACTIVE_SLIDE = inactive;
        this.INACTIVE_SLIDE = inactive;
    }

    private Color INACTIVE_KNOB = Color.WHITE;
    private Color ACTIVE_KNOB = Color.decode("#FFFFFF");
    private Color INACTIVE_SLIDE = Color.decode("#333333");
    private Color ACTIVE_SLIDE = Color.decode("#38A1F3");
    private Color BORDER_COLOR = Color.decode("#DDDDDD");

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
            g2d.setColor((this.selected ? ACTIVE_SLIDE : INACTIVE_SLIDE));
            g2d.fillRoundRect(1, 6, getWidth() - 1, getHeight() - 6, curve, curve);
            g2d.setColor(currentKnobColor);
            g2d.fillRoundRect(curXPosOfSwitch + 1, 1, getWidth() / 2 - 1, getHeight() - 1, curve, curve);
        } else {
            g2d.setColor((this.selected ? ACTIVE_KNOB : INACTIVE_KNOB));
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
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        }

        if (switchStyle) {
            g2d.setColor(BORDER_COLOR);
            g2d.drawRoundRect(1, 3, getWidth() - 1, getHeight() - 6, curve, curve);
            g2d.drawRoundRect(curXPosOfSwitch + 1, 1, getWidth() / 2 - 1, getHeight() - 1, curve, curve);
        } else {
            g2d.setColor(BORDER_COLOR);
            g2d.drawRoundRect(1, 1, getWidth() - 1, getHeight() - 1, curve, curve);
        }
        g.drawImage(buffer, 0, 0, null);
        super.paintBorder(g);
    }

    public InputCheckbox setBorderColor(Color bordCol) {
        this.BORDER_COLOR = bordCol;
        return this;
    }

    public InputCheckbox setBorderWeight(int weight) {
        this.borderWeight = weight;
        return this;
    }
    private int borderWeight = 1;
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
    private double accel = 0;

    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    public void transitionPosition(long time, double step) {
        positionToSlideTo = 0;
        accel = 0;
        if (isSelected()) {
            positionToSlideTo = this.getWidth() / 2;
        }
        if (ses == null || ses.isShutdown()) {
            ses = Executors.newSingleThreadScheduledExecutor();
        }
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (Math.abs(curXPosOfSwitch - positionToSlideTo) < step) {
                    if (curXPosOfSwitch != positionToSlideTo) {
                        curXPosOfSwitch = (int) positionToSlideTo;
                        paintImmediately(0, 0, getWidth(), getHeight());
                    }
                    ses.shutdown();
                } else {
                    if (accel < step) {
                        accel += 0.1;
                    }
                    if (curXPosOfSwitch > positionToSlideTo) {
                        curXPosOfSwitch -= (int) ((double) step * (double) accel);
                        paintImmediately(0, 0, getWidth(), getHeight());
                    } else if (curXPosOfSwitch < positionToSlideTo) {
                        curXPosOfSwitch += (int) ((double) step * (double) accel);
                        paintImmediately(0, 0, getWidth(), getHeight());
                    } else {
                        ses.shutdown();
                        if (curXPosOfSwitch != positionToSlideTo) {
                            curXPosOfSwitch = (int) positionToSlideTo;
                            paintImmediately(0, 0, getWidth(), getHeight());
                        }
                    }
                }
            }
        }, 0, time, TimeUnit.MILLISECONDS);
    }
    private volatile Color currentKnobColor = INACTIVE_KNOB;

    public void clickEvent() {
        toggleSelected();
        if (switchStyle) {
            transitionPosition(10, 5);
        }
        currentKnobColor = (isSelected() ? this.ACTIVE_KNOB : this.INACTIVE_KNOB);
    }

    public void exitEvent() {

    }
}
