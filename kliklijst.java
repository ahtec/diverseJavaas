
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.plaf.ColorUIResource;

public class kliklijst extends JFrame {

    private static String[] extensies;
    private Image image;
//    private JList keuzelijst;
//    private DefaultListModel lijst;
//    private fullscreenFrame fulscreenframe;
    private int frameHoogte;
    private int frameBreedte;
    JFrame diaFrame;
//            =    new fullscreenFrame(keuzeFile);
    public kliklijst() {
        setSize(500, 300);
        setTitle("dia's");
//        hoeveel monitoren hebbenwe en welke is voor de beamer
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
//        int ll = gs.length;
        if (gs.length > 1) {
            //dus er is nog een scherm en die gaan we gebruikne voor de diaas

            GraphicsDevice gd = gs[1];
            gd.setFullScreenWindow(null);
            diaFrame = new JFrame(gd.getDefaultConfiguration());
            frameHoogte = gd.getDisplayMode().getHeight();
            frameBreedte = gd.getDisplayMode().getWidth();
            diaFrame.setSize(frameBreedte, frameHoogte);

        } else {
            //er is mar 1 scherm
            GraphicsDevice gd = gs[0];
            diaFrame = new JFrame();
            frameHoogte = gd.getDisplayMode().getHeight();
            frameBreedte = gd.getDisplayMode().getWidth();
        }

        diaFrame.setSize(frameBreedte, frameHoogte);
        diaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                diaFrame.setBackground(Color.BLACK);
        diaFrame.getContentPane().setBackground(Color.BLACK);
        diaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        diaFrame.addMouseListener(new clickListener());

        System.out.println(frameBreedte);
        System.out.println(frameHoogte);
//        UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
//        UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.black));


//
//    }



//        fulscreenframe = new fullscreenFrame();
    }

    public static void main(String[] args) {

        if ((args == null) || (args.length == 0)) {
            extensies = new String[]{"jpg"};
        } else {
            extensies = args;
        }


        kliklijst frame = new kliklijst();
        try {
            frame.createGUI();
        } catch (IOException ex) {
            Logger.getLogger(kliklijst.class.getName()).log(Level.SEVERE, null, ex);
        }
        frame.setVisible(true);
    }

    public void createGUI() throws IOException {
        final JFileChooser fc = new JFileChooser();
        File f = null;

        File filedefault = readVorigeFileKeuze();

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setSelectedFile(filedefault);

        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
            schrijfHuidigeFileKeuze(f);


            setDefaultCloseOperation(EXIT_ON_CLOSE);
            Container window = this.getContentPane();
//            lijst = new DefaultListModel();
            JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(0, 8));
//            String ex = "jpg";
            String prefix = "0000";
            File[] files = f.listFiles();
            Arrays.sort(files);
            ImageIcon ia;
            myButton jb = null;
            for (int k = 0; k < files.length; k++) {
                File file = files[k];
                if (file.isFile()) {
                    Boolean extensieOK = Boolean.FALSE;
                    for (String s : extensies) {
                        if (file.getName().toLowerCase().endsWith(s)) {
                            extensieOK = Boolean.TRUE;
                            break;
                        }
                    }

                    if (extensieOK == Boolean.TRUE) {
                        System.out.println(file.getAbsoluteFile());
                        if (file.getName().startsWith(prefix)) {
                        } else {
                            JLabel label = new JLabel(file.getName().substring(0, 3));
                            jp.add(label);
                            prefix = file.getName().substring(0, 3);
                        }
                        try {
                            image = ImageIO.read(file);
                            ia = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                            jb = new myButton();
                            jb.setIcon(ia);
                            jb.setLeftFile(file);
                            jb.addMouseListener(new MuisVoeler());
                            jb.setPreferredSize(new Dimension(ia.getIconWidth(), ia.getIconHeight()));
                            jp.add(jb);
                        } catch (javax.imageio.IIOException iio) {
                            System.out.println("Dit is geen geldig image." + file.getName());
                        }
                    }
                }
                JScrollPane metSchuif = new JScrollPane(jp);
                metSchuif.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                metSchuif.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                window.add(metSchuif);
            }
        }
    }

    private File readVorigeFileKeuze() {
        File fileOut = null;
        try {
            FileInputStream fileIn = new FileInputStream("vorigeKeuze");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            fileOut = (File) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            fileOut = new File("");
        } catch (ClassNotFoundException c) {
            fileOut = new File("");
        }

        return fileOut;
    }

    private void schrijfHuidigeFileKeuze(File f) {


        try {
            FileOutputStream fileOut =
                    new FileOutputStream("vorigeKeuze");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(f);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    class MuisVoeler extends MouseAdapter {

        private double widthImageToBeDisplayed;
        private double heightImageToBeDisplayed;
        private Image imageVooricon;

        @Override
        public void mouseClicked(MouseEvent event) {

            myButton jButtonPressed = (myButton) event.getSource();
            File keuzeFile = jButtonPressed.getLeftFile();
            try {
                Image imageToBeDisplayed = ImageIO.read(keuzeFile);
                widthImageToBeDisplayed = imageToBeDisplayed.getWidth(null);
                heightImageToBeDisplayed = imageToBeDisplayed.getHeight(null);
                System.out.print(widthImageToBeDisplayed + " ");
                System.out.println(heightImageToBeDisplayed);


                // portrait
                if (heightImageToBeDisplayed > widthImageToBeDisplayed) {

                    imageVooricon = imageToBeDisplayed.getScaledInstance(-1, frameHoogte, Image.SCALE_DEFAULT);
                    System.out.println(" geschaald op de vaste hoogte");

                } else {
                    double factorPlaatje = widthImageToBeDisplayed / heightImageToBeDisplayed;
                    double factorScherm = frameBreedte * 1.0 / frameHoogte * 1.0;
                    // landscape
                    if (factorPlaatje > factorScherm) {
                        // schaal op breddte
                        imageVooricon = imageToBeDisplayed.getScaledInstance(frameBreedte, -1, Image.SCALE_DEFAULT);
                        System.out.println(" geschaald op de vaste breedte " + factorPlaatje + " " + factorScherm);
                    } else {
                        imageVooricon = imageToBeDisplayed.getScaledInstance(-1, frameHoogte, Image.SCALE_DEFAULT);
                        System.out.println(" geschaald op de vaste hoogte " + factorPlaatje + " " + factorScherm);
                    }


                }
                diaFrame.getContentPane().removeAll();
//                diaFrame.pack();
//                        removeAll();
                BufferedImage bi = new BufferedImage(frameBreedte, frameHoogte, BufferedImage.TYPE_3BYTE_BGR);
                diaFrame.getContentPane().add(new JLabel(new ImageIcon(bi)));
                diaFrame.getContentPane().add(new JLabel(new ImageIcon(imageVooricon)), BorderLayout.PAGE_END);
//                diaFrame.pack();
                diaFrame.setVisible(true);
                diaFrame.setFocusable(Boolean.FALSE);
//            diaFrame.setfullscreenFrame(keuzeFile);
//            Object[] keuze = keuzelijst.getSelectedValues();
//            for (int i = 0; i < keuze.length; i++) {
//            }
//            lijst.addElement("water");
            } catch (IOException ex) {
                System.out.println(" IOExeption op lezen file  " + keuzeFile);


            }
//            lijst.addElement("water");
        }
    }

    public class clickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
//            myButton jButtonPressed = (myButton) event.getSource();

//        setVisible(false); //you can't see me!
//        skipnumaardez = Boolean.FALSE;
            System.out.println(" Disposing Diaframe");

            diaFrame.dispose(); //Destroy the JFrame object
        }
    }
}