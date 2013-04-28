package at.yawk.snap;

import java.awt.Dialog.ModalExclusionType;
import java.awt.TrayIcon.MessageType;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

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
                    try {
                        final String id = config.getSaveTarget().saveTo(config.getCropDisplay().getCroppedImage(), config.getIdGenerator(), trayIcon);
                        final String clip = config.getTargetUrl().replace("%id", id);
                        ClipboardSaver.saveToClipboard(clip);
                        trayIcon.displayMessage("YawkatSnap", "Uploaded " + clip, MessageType.INFO);
                        trayIcon.setValue(-1);
                    } catch (Exception e) {
                        displayThrowableMessage(e);
                    }
                }
            });
            config.getCropDisplay().display();
        } catch (Exception e) {
            displayThrowableMessage(e);
            System.exit(0);
        }
    }
}
