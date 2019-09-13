package rescue.base.locator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import static rescue.base.locator.Algorithm.graphicalAnalysis;
import static rescue.base.locator.RescueBaseLocator.locations;

/**
 *
 * @author Daniel Allen
 */
public class GUI extends JFrame {

    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();

    public ImagePanel imgP = new ImagePanel();

    public GUI() {
        this.setLayout(gbl);
        gbl.columnWeights = new double[]{0.622, 0.288};
        gbl.rowWeights = new double[]{1};
        gbl.columnWidths = new int[]{622, 288};

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(imgP, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        ButtonPanel buttons = new ButtonPanel();
        this.add(buttons, gbc);
        this.setSize(900, 658);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

class ImagePanel extends JPanel implements MouseListener {

    private BufferedImage doubleBuffer = new BufferedImage(622, 622, BufferedImage.TYPE_INT_ARGB);
    Image toDraw = null;

    public ImagePanel() {

        this.setSize(622, 622);
        addMouseListener(this);
        new File("src\\provided\\").mkdirs();
        File f = new File("src\\provided\\ontarioMap.jpg");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Image gotImage = ImageIO.read(f);
            if (gotImage != null) {
                toDraw = gotImage.getScaledInstance(622, 622, Image.SCALE_SMOOTH);
            }
        } catch (IOException ex) {
            Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public DoublePoint bestLocation;
    public DoublePoint currentPointCalculation;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) doubleBuffer.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, doubleBuffer.getWidth(), doubleBuffer.getHeight());
        if (toDraw != null) {
            g2d.drawImage(toDraw, 0, 0, null);
        }
        if (clickPoint != null) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(clickPoint.x - 3, clickPoint.y - 3, 6, 6);
        }

        g2d.setColor(Color.BLACK);
        if (RescueBaseLocator.locations != null) {
            for (int i = 0; i < RescueBaseLocator.locations.length; i++) {
                g2d.fillOval((int) RescueBaseLocator.locations[i][0] - 3, (int) RescueBaseLocator.locations[i][1] - 3, 6, 6);
            }
        }
        if (doubleBuffer != null) {
            if (RescueBaseLocator.locations != null) {
                BufferedImage overlay = graphicalAnalysis(new BufferedImage(doubleBuffer.getWidth(), doubleBuffer.getHeight(), BufferedImage.TYPE_INT_ARGB));
                g2d.drawImage(overlay, 0, 0, null);
            }
        }
        if (currentPointCalculation != null) {
            g2d.setColor(Color.RED);
            g2d.fillOval(currentPointCalculation.toPoint().x - 6, currentPointCalculation.toPoint().y - 6, 12, 12);
        }
        if (bestLocation != null) {
            g2d.setColor(Color.RED);
            g2d.fillOval(bestLocation.toPoint().x - 6, bestLocation.toPoint().y - 6, 12, 12);
        }
        g.drawImage(doubleBuffer, 0, 0, null);
    }

    Point clickPoint = null;

    @Override
    public void mousePressed(MouseEvent e) {
        Point clickedPoint = new Point(e.getPoint().x, e.getPoint().y) {
            //override the toString method for a more readable point in debugging
            @Override
            public String toString() {
                return "(" + this.x + ", " + this.y + ")";
            }
        };
        if (new Rectangle(this.getSize()).contains(clickedPoint)) {
            clickPoint = clickedPoint;
            repaint();
            System.out.println(clickedPoint);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}

class ButtonPanel extends JPanel {

    private ActionButton loadResourceButton = new ActionButton("Load and Display Rescues") {
        @Override
        void onClick() {
            JFileChooser jfc = new JFileChooser();
            jfc.setToolTipText("Select rescue locations from file");
            FileNameExtensionFilter fnef = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            jfc.setFileFilter(fnef);
            jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            try {
                if (jfc.showOpenDialog(jfc) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    String filePath = selectedFile.getParentFile().getAbsolutePath() + "\\";
                    String fileName = selectedFile.getName();
                    RescueBaseLocator.locations = RescueBaseLocator.readLocations(filePath, fileName);
                    RescueBaseLocator.gui.imgP.repaint();
                    double distanceFromPayne = Algorithm.totalDistance(new double[]{331, 352}, locations);
                    double distanceFromMe = Algorithm.totalDistance(new double[]{362, 388}, locations);
                    System.out.println("Distance from Mr. Payne's point: " + distanceFromPayne + "\nDistance from My Point: " + distanceFromMe);

                }
            } catch (Exception e) {
                System.exit(1);
            }

        }
    };
    ExecutorService finderExcutor = Executors.newCachedThreadPool();
    private ActionButton optimalFinderButton = new ActionButton("Find optimal location") {
        Future<DoublePoint> f;

        @Override
        void onClick() {
            if (RescueBaseLocator.locations == null) {
                new InformationWindow("Error!", "Load locations first!", JOptionPane.ERROR_MESSAGE).show();
                return;
            }

            if (RescueBaseLocator.locations.length == 0) {
                new InformationWindow("Error!", "Load locations first!", JOptionPane.ERROR_MESSAGE).show();
                return;
            }
            FindOptimalLocationRunnable finderCallable = new FindOptimalLocationRunnable(RescueBaseLocator.locations);
            if (f == null) {
                f = finderExcutor.submit(finderCallable);
            }
            if (f.isDone()) {
                f = finderExcutor.submit(finderCallable);
            }
            try {
                RescueBaseLocator.gui.imgP.bestLocation = f.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(ButtonPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(ButtonPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            RescueBaseLocator.gui.imgP.repaint();

        }
    };
    private InputField startingXField = new InputField(150, 25, "Starting X coordinate");
    private InputField startingYField = new InputField(150, 25, "Starting Y coordinate");

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(51, 51, 51));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    public ButtonPanel() {
        this.setLayout(gbl);
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(loadResourceButton, gbc);


        startingXField.setNumbersOnly(true);
        startingXField.setAllowDecimals(true);
        startingXField.setPlaceholder("Starting X Coordinate");
        startingXField.setPadding(0, 5, 0, 0);
        startingXField.setCurve(15);

        startingYField.setNumbersOnly(true);
        startingYField.setAllowDecimals(true);
        startingYField.setPlaceholder("Starting Y Coordinate");
        startingYField.setPadding(0, 5, 0, 0);
        startingXField.setCurve(15);


        gbc.gridy++;
        this.add(startingXField, gbc);
        gbc.gridy++;
        this.add(startingYField, gbc);
        gbc.gridy++;
        this.add(optimalFinderButton, gbc);
    }
}

abstract class ActionButton extends JButton {

    public ActionButton(String text) {
        super(text);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClick();
            }
        });
    }

    abstract void onClick();
}

class InputField extends JTextField {

    private boolean numOnly = false;
    private boolean allowDecimals = true;

    public InputField(int width, int height, String tooltip) {
        this.setPreferredSize(new Dimension(width, height));
        this.setToolTipText(tooltip);
        init();
    }

    public InputField(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        init();
    }

    public InputField(String tooltip) {
        this.setToolTipText(tooltip);
        init();
    }

    public InputField() {
        init();
    }

    public void setPadding(int top, int left, int bottom, int right) {
        padding = BorderFactory.createEmptyBorder(top, left, bottom, right);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
    }

    public Border getPadding() {
        return padding;
    }
    private Border padding = BorderFactory.createEmptyBorder();

    private void init() {
        setOpaque(false);
        ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter());
    }

    public void setNumbersOnly(boolean numOnly) {
        this.numOnly = numOnly;
        ((PlainDocument) this.getDocument()).setDocumentFilter(new CustomDocFilter("[0-9]+$"));
    }

    public boolean isNumbersOnly() {
        return this.numOnly;
    }

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

    public boolean allowsDecimals() {
        return this.allowDecimals;
    }

    public void setCurve(int curve) {
        this.curve = curve;
        repaint();
    }
    private int curve = 0;

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
    private String placeholder = "";

    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    @Override
    protected void paintComponent(Graphics g) {
        if(buffer.getWidth() != this.getWidth() || buffer.getHeight() != this.getHeight()){
            buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            g2d.setColor(getDisabledTextColor());
            g2d.drawString(placeholder, 5, g.getFontMetrics().getHeight());
        }
        g.drawImage(buffer, 0, 0, null);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, curve, curve);
        super.paintComponent(g);
    }

    private Shape fieldShape;
    private int knownCurve = curve;

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
