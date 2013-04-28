package at.yawk.snap;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Graphics2D;
import java.awt.TrayIcon.MessageType;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class YawkatSnap implements Runnable {
    protected SnapConfig config;
    protected SnapTrayIcon trayIcon;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new YawkatSnap().run();
    }
    
    @Override
    public void run() {
        try {
            config = new SnapConfig(new File("config.properties"));
        } catch (Exception e1) {
            displayThrowableMessage(e1);
            System.exit(-1);
        }
        trayIcon = new SnapTrayIcon(this);
        trayIcon.run();
        KeyboardHandler.registerHotkey(KeyEvent.VK_1, KeyboardHandler.MASK_CTRL, new Runnable() {
            @Override
            public void run() {
                doSnap();
            }
        });
        KeyboardHandler.registerHotkey(KeyEvent.VK_2, KeyboardHandler.MASK_CTRL, new Runnable() {
            @Override
            public void run() {
                snapFromClipboard();
            }
        });
    }
    
    private void displayThrowableMessage(Throwable throwable) {
        throwable.printStackTrace();
        final JDialog frame = new JDialog();
        frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(Box.createRigidArea(new Dimension(0, 5)));
        frame.add(new JLabel(throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage()));
        frame.add(Box.createRigidArea(new Dimension(0, 5)));
        final JTextArea errorArea = new JTextArea();
        final StringWriter error = new StringWriter();
        throwable.printStackTrace(new PrintWriter(error));
        errorArea.setText(error.toString());
        errorArea.setEditable(false);
        frame.add(new JScrollPane(errorArea));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setModal(true);
        frame.setVisible(true);
    }
    
    protected void doSnap() {
        try {
            final GraphicsDevice device = ScreenUtil.getGraphicsDevice();
            final Robot robo = new Robot();
            final BufferedImage originalImage = robo.createScreenCapture(new Rectangle(0, 0, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight()));
            config.getCropDisplay().setImage(originalImage);
            config.getCropDisplay().setCallback(new Runnable() {
                @Override
                public void run() {
                    handleSnap(config.getCropDisplay().getCroppedImage());
                }
            });
            config.getCropDisplay().display();
        } catch (Exception e) {
            displayThrowableMessage(e);
            System.exit(0);
        }
    }
    
    protected void snapFromClipboard() {
        final Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                handleSnap(toRendered((Image) t.getTransferData(DataFlavor.imageFlavor)));
            } else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                @SuppressWarnings("unchecked")
                final List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : l) {
                    try {
                        handleSnap(ImageIO.read(file));
                        break;
                    } catch (IOException e) {
                    }
                }
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                final String[] as = ((String) t.getTransferData(DataFlavor.stringFlavor)).split("\n");
                if (as.length == 0 || (as.length == 1 && as[0].isEmpty())) {
                    return;
                }
                boolean monospaced = false;
                int spaces = Integer.MAX_VALUE;
                for (int i = 0; i < as.length; i++) {
                    as[i] = as[i].replace("\t", "    ");
                    // monospace if any kind of indentation is detected
                    if (!monospaced && as[i].contains("  ")) {
                        monospaced = true;
                    }
                    for (int j = 0; j < as[i].length() && j < spaces; j++) {
                        if (as[i].charAt(j) != ' ') {
                            spaces = j;
                            break;
                        }
                    }
                }
                if (spaces != Integer.MAX_VALUE && spaces != 0) {
                    for (int i = 0; i < as.length; i++) {
                        as[i] = as[i].length() > spaces ? as[i].substring(spaces) : "";
                    }
                }
                final FontRenderContext renderContext = new FontRenderContext(new AffineTransform(), true, false);
                final Font font = new Font(monospaced ? "Courir Sans New" : "Tahoma", Font.PLAIN, monospaced ? 10 : 16);
                final Rectangle r = new Rectangle();
                r.height = as.length * (font.getSize() + 1);
                for (String s : as) {
                    final Rectangle2D bounds = font.getStringBounds(s, renderContext);
                    r.width = Math.max((int) bounds.getWidth(), r.width);
                }
                final BufferedImage bi = new BufferedImage((int) r.getWidth() + 2, (int) r.getHeight() + 2, BufferedImage.TYPE_4BYTE_ABGR);
                final Graphics2D g = bi.createGraphics();
                g.setFont(font);
                g.setColor(Color.BLACK);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, renderContext.getAntiAliasingHint());
                int a = (int) g.getFontMetrics().getLineMetrics(as[0], g).getAscent() + 1;
                for (int i = 0; i < as.length; i++) {
                    g.drawString(as[i], 1, a + i * (font.getSize() + 1));
                }
                g.dispose();
                handleSnap(bi);
            }
        } catch (UnsupportedFlavorException e) {
            // flavors are checked, this should not happen.
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    
    private static RenderedImage toRendered(Image image) {
        if (image instanceof RenderedImage) {
            return (RenderedImage) image;
        }
        final BufferedImage copy = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g = copy.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }
    
    protected void handleSnap(RenderedImage image) {
        try {
            final String id = config.getSaveTarget().saveTo(image, config.getIdGenerator(), trayIcon);
            final String clip = config.getTargetUrl().replace("%id", id);
            ClipboardSaver.saveToClipboard(clip);
            trayIcon.displayMessage("YawkatSnap", "Uploaded " + clip, MessageType.INFO);
            trayIcon.setValue(-1);
        } catch (Exception e) {
            displayThrowableMessage(e);
        }
    }
}
