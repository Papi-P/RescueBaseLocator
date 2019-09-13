/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescue.base.locator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * JTextField with convenient customization options.<br><br>
 *
 * @author Daniel Allen
 */
public class InputField extends JTextField {

    //<editor-fold defaultstate="collapsed" 
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

    //store the input restriction booleans.
    private boolean numOnly = false;
    private boolean allowDecimals = true;

    
    /**
     * Creates a new InputField with the specified width, height, and
     * tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code><i>setToolTipText(String text)</i></code> method.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText(String text)
     */
    public InputField(int width, int height, String tooltip) {
        this.setPreferredSize(new Dimension(width, height));
        this.setToolTipText(tooltip);
        init();
    }

    /**
     * Creates a new InputField with the specified width and height.
     *
     * @param width the width of the input field
     * @param height the height of the input field
     *
     */
    public InputField(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    /**
     * Creates a new InputField with the specified tooltip.<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Note: the tooltip will likely be switched for
     * placeholder text, and the tooltip must be set using the
     * <code><i>setToolTipText(String text)</i></code> method.
     *
     * @param tooltip the tooltip that will display when the mouse hovers above
     * the field.
     *
     * @see setToolTipText(String text)
     */
    public InputField(String tooltip) {
        this.setToolTipText(tooltip);
        init();
    }

    /**
     * Creates a new InputField with no pre-determined values.
     *
     */
    public InputField() {
        init();
    }
    
    //initialize the component. This doesn't need to be called manually, or ever after the field is initialized.
    private void init() {
        setOpaque(false);
        ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter());
    }
    
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
    public void setPadding(int top, int left, int bottom, int right) {
        padding = BorderFactory.createEmptyBorder(top, left, bottom, right);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
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

    /**
     * Setter method to set whether the user can type only numbers or not.
     * @param numOnly true to only allow numbers | false to allow anything.
     */
    public void setNumbersOnly(boolean numOnly) {
        this.numOnly = numOnly;
        ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter("[0-9]+$"));
    }

    /**
     * Getter method to get whether the field can receive text input or numbers only.
     * @return whether this field can only use numbers.
     */
    public boolean isNumbersOnly() {
        return this.numOnly;
    }

    /**
     *
     * @param dec True to allow decimals | False to 
     * @throws IllegalStateException if this attempts to set allowDecimals to false before numbersOnly is true.
     */
    public void setAllowDecimals(boolean dec) throws IllegalStateException {
        if (!isNumbersOnly() && !dec) {
            throw new IllegalStateException("Must set to numbers only before denying decimals!");
        } else if (dec == true) {
            ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter("[0-9.]+$"));
        } else {
            ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter("[0-9]+$"));
        }
        this.allowDecimals = dec;
    }

    /**
     * Getter method to get whether this field will accept decimals.
     * @return
     */
    public boolean getAllowsDecimals() {
        return this.allowDecimals;
    }

    /**
     * Setter method to set the curve radius of the corners of the field.
     * @param curve
     */
    public void setCurve(int curve) {
        this.curve = curve;
        repaint();
    }
    private int curve = 0;

    /**
     *
     * @param placeholder
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     *
     * @return
     */
    public String getPlaceholder() {
        return this.placeholder;
    }
    private String placeholder = "";

    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    /**
     *
     * @param g
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
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            g2d.setColor(getDisabledTextColor());
            g2d.drawString(placeholder, 5, g.getFontMetrics().getHeight());
        }
        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        super.paintComponent(g);
    }

    private Shape fieldShape;
    private int knownCurve = curve;

    /**
     *
     * @param x
     * @param y
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

    class CustomDocFilter extends DocumentFilter {

        private String regex = "";

        public CustomDocFilter() {
            super();
        }

        public CustomDocFilter(String reg) {
            super();
            this.regex = reg;
        }

        public CustomDocFilter setRegex(String reg) {
            this.regex = reg;
            return this;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet as) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offs, str);
            if (hasText(sb.toString())) {
                super.insertString(fb, offs, str, as);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet as) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offs, str);
            if (hasText(sb.toString())) {
                super.replace(fb, offs, length, str, as);
            }
        }

        private boolean hasText(String input) {
            if (!input.matches(regex) && !regex.isEmpty()) {
                return false;
            }
            return true;
        }
    }
}
