package rescue.guiComponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JComponent;

/**
 *
 * @author Daniel Allen
 */
public class InputCheckbox extends JComponent {

    boolean onTop() {
        return true;
    }

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
        this.setOpaque(false);
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

    public InputCheckbox setKnobColors(Color active, Color inactive) {
        this.ACTIVE_KNOB = inactive;
        this.INACTIVE_KNOB = inactive;
        return this;
    }

    public InputCheckbox setSlideColors(Color active, Color inactive) {
        this.ACTIVE_SLIDE = inactive;
        this.INACTIVE_SLIDE = inactive;
        return this;
    }
    private boolean doubleBuffered = true;

    public InputCheckbox setBuffered(boolean buf) {
        this.doubleBuffered = buf;
        return this;
    }

    public boolean isBuffered() {
        return this.doubleBuffered;
    }
    private Color INACTIVE_KNOB = Color.decode("#FFFFFF");
    private Color ACTIVE_KNOB = Color.decode("#FFFFFF");
    private Color INACTIVE_SLIDE = Color.decode("#333333");
    private Color ACTIVE_SLIDE = Color.decode("#38A1F3");
    private Color BORDER_COLOR = Color.decode("#DDDDDD");

    Graphics2D g2d = (Graphics2D) buffer.getGraphics();

    /**
     * Paints the component. This should not be called manually, and should
     * instead use <code>repaint()</code>
     *
     * @param g
     * @see repaint()
     */
    @Override
    public void paintComponent(Graphics g) {
        //make sure the double buffer is the same size as this component
        if (buffer.getWidth() != this.getWidth() || buffer.getHeight() != this.getHeight()) {
            //set the buffer to the correct size
            buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
            //get the double buffer's graphics
            g2d = (Graphics2D) buffer.getGraphics();
            //clear the background of the component
            g2d.setBackground(new Color(0, 0, 0, 0));
        }

        g2d.clearRect(0, 0, getWidth(), getHeight());

        //check if antialiasing is on
        if (antialias) {
            //set the renderingHints
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        //check if the component is in its switch format
        if (switchStyle) {
            //paint the background of the component
            g2d.setColor((this.selected ? ACTIVE_SLIDE : INACTIVE_SLIDE));
            g2d.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 6, curve, curve);

            //paint the border of the component
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(borderWeight));
            g2d.drawRoundRect(1, 3, getWidth() - 2, getHeight() - 6, curve, curve);

            //paint the knob of the component
            g2d.setColor((this.selected ? ACTIVE_KNOB : INACTIVE_KNOB));
            g2d.fillRoundRect(curXPosOfSwitch + 1, 1, getWidth() / 2 - 1, getHeight() - 2, curve, curve);

            //paint the knob's border
            g2d.setColor(BORDER_COLOR);
            g2d.drawRoundRect(curXPosOfSwitch + 1, 1, getWidth() / 2 - 1, getHeight() - 2, curve, curve);

        } else {
            //paint the checkbox-style component
            g2d.setColor((this.selected ? ACTIVE_KNOB : INACTIVE_KNOB));
            g2d.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 2, curve, curve);
        }
        //paint the doubleBuffer to the graphics
        g.drawImage(buffer, 0, 0, null);

        //call JComponent's paintComponent method
        super.paintComponent(g);
    }
    int currentPlaceholderPhaseShift = 0;

    public InputCheckbox setBorderColor(Color bordCol) {
        this.BORDER_COLOR = bordCol;
        return this;
    }

    public InputCheckbox setBorderWeight(float weight) {
        this.borderWeight = weight;
        return this;
    }
    private float borderWeight = 1;
    private int knownCurve = curve;

    private boolean selected = false;

    public void toggleSelected() {
        if (isSelected()) {
            setSelectedSmooth(false);
        } else {
            setSelectedSmooth(true);
        }
    }

    public InputCheckbox setSelectedSmooth(boolean sel) {
        boolean pre = this.selected;
        this.selected = sel;
        if (pre != sel) {
            repaint();
        }
        return this;
    }
    
    public InputCheckbox setSelected(boolean sel){
        this.selected = sel;
        this.positionToSlideTo = (isSelected() ? this.getWidth() / 2 - 1 : 0);
        this.curXPosOfSwitch = (int)positionToSlideTo;
        paintImmediately(0, 0, this.getWidth(), this.getHeight());
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

    public void transitionPosition(long time, double step, double acceleration) {
        positionToSlideTo = 0;
        accel = 0;
        if (isSelected()) {
            positionToSlideTo = this.getWidth() / 2 - 1;
        }
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Math.abs(curXPosOfSwitch - positionToSlideTo) < step) {
                    if (curXPosOfSwitch != positionToSlideTo) {
                        curXPosOfSwitch = (int) positionToSlideTo;
                        paintImmediately(0, 0, getWidth(), getHeight());
                    }
                    this.cancel();
                } else {
                    if (accel < step) {
                        accel += acceleration;
                    }
                    if (curXPosOfSwitch > positionToSlideTo) {
                        curXPosOfSwitch -= (int) ((double) step * (double) accel);
                        paintImmediately(0, 0, getWidth(), getHeight());
                    } else if (curXPosOfSwitch < positionToSlideTo) {
                        curXPosOfSwitch += (int) ((double) step * (double) accel);
                        paintImmediately(0, 0, getWidth(), getHeight());
                    } else {
                        if (curXPosOfSwitch != positionToSlideTo) {
                            curXPosOfSwitch = (int) positionToSlideTo;
                            paintImmediately(0, 0, getWidth(), getHeight());
                        }
                        this.cancel();
                    }
                }
            }
        }, 0, time);
    }

    private boolean allowRapid = false;
    public InputCheckbox setAllowRapidUse(boolean rapid){
        this.allowRapid = rapid;
        return this;
    }
    public boolean allowsRapid(){
        return this.allowRapid;
    }
    
    public void clickEvent() {
        if (curXPosOfSwitch == positionToSlideTo  || allowRapid) {
            toggleSelected();
            if (switchStyle) {
                transitionPosition(10, 5, 0.05);
            }
        }
    }

    public void exitEvent() {

    }
}
