/*
 * © 2019 Daniel Allen
 */
package rescue.guiComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * JTextField with convenient customization options.<br><br>
 *
 * @author Daniel Allen
 */
public abstract class InputButton extends JButton {

    //store the input restriction booleans.
    private boolean numOnly = false;
    private boolean allowDecimals = true;

    //<editor-fold defaultstate="collapsed" desc="Enums">
    enum Alignments {

        HORIZONTAL_LEFT(-3),
        HORIZONTAL_MIDDLE(-2),
        HORIZONTAL_RIGHT(-1),
        VERTICAL_BOTTOM(1),
        VERTICAL_MIDDLE(2),
        VERTICAL_TOP(3);

        final int align;

        Alignments(int align) {
            this.align = align;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new InputField with the specified width, height, and
     * tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText
     */
    public InputButton(int width, int height, String text) {
        this.setPreferredSize(new Dimension(width, height));
        this.setText(text);
        init();
    }

    /**
     * Creates a new InputField with the specified width and height.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     *
     */
    public InputButton(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    /**
     * Creates a new InputField with the specified tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code>setToolTipText()</code> method.
     *
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText
     */
    public InputButton(String text) {
        this.setText(text);
        init();
    }

    /**
     * Creates a new InputField with no predetermined values.
     *
     */
    public InputButton() {
        init();
    }

    //initialize the component. This doesn't need to be called manually, or ever after the field is initialized.
    private void init() {
        setOpaque(false);
        this.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                onClick();
            }
        });
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Padding">
    //padding methods and storage
    private Border padding = BorderFactory.createEmptyBorder();

    /**
     * Set the padding of the insides of the InputField.<br>
     * This changes the position of the text inside the field.
     *
     * @param top The padding at the top of the field.
     * @param left The padding at the left of the field.
     * @param bottom The padding at the bottom of the field.
     * @param right The padding at the right of the field.
     */
    public InputButton setPadding(int top, int left, int bottom, int right) {
        padding = BorderFactory.createEmptyBorder(top, left, bottom, right);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
        return this;
    }

    /**
     * Get the border representing this field's padding.
     *
     * @return An Empty Border filled with the current padding.
     */
    public Border getPadding() {
        return padding;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Rounded Corners">
    private int curve = 0;

    /**
     * Setter method to set the curve of the corners of the field.
     *
     * @param curve The radius of the curve
     */
    public InputButton setCurve(int curve) {
        this.curve = curve;
        repaint();
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Placeholder Text">
    private String placeholder = "";

    /**
     * Setter method to set the placeholder text of the field. This only
     * displays if the field has no input, and the placeholder is not empty.
     *
     * @param placeholder The text to display.
     * @see getPlaceholder
     */
    public InputButton setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Getter method to get the placeholder text of this field.
     *
     * @return The placeholder text.
     * @see setPlaceholder
     */
    public String getPlaceholder() {
        return this.placeholder;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Painting">
    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     * Paints the component. This should not be called manually, and should
     * instead use <code>repaint()</code>
     *
     * @see repaint()
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (buffer.getWidth() != this.getWidth() || buffer.getHeight() != this.getHeight()) {
            buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);

        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    /**
     * This should not be called manually. Use <code>repaint()</code> instead.
     *
     * @see repaint()
     */
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        super.paintComponent(g);
    }

    private Shape fieldShape;
    private int knownCurve = curve;
//</editor-fold>

    /**
     * Detect if this field contains a point in it. This is adapted to account
     * for rounded corners.
     *
     * @param x The X-coordinate of the point
     * @param y The Y-coordinate of the point
     * @return
     */
    @Override
    public boolean contains(int x, int y) {
        if (fieldShape == null || !fieldShape.getBounds().equals(getBounds()) || knownCurve != curve) {
            knownCurve = curve;
            fieldShape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        }
        return fieldShape.contains(x, y);
    }

    public abstract void onClick();
}
