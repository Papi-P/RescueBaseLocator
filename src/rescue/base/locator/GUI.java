package rescue.base.locator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import static rescue.base.locator.Algorithm.graphicalAnalysis;

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
                }
            } catch (Exception e) {
                System.exit(1);
            }

        }
    };
    private ActionButton optimalFinderButton = new ActionButton("Find optimal location") {
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

            RescueBaseLocator.gui.imgP.bestLocation = Algorithm.getOptimalPosition(RescueBaseLocator.locations);
            RescueBaseLocator.gui.imgP.repaint();

        }
    };

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(51, 51, 51));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    public ButtonPanel() {
        this.add(loadResourceButton);
        this.add(optimalFinderButton);
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