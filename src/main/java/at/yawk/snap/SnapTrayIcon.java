package at.yawk.snap;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class SnapTrayIcon implements Runnable, UpdateMonitor {
    private final YawkatSnap snapper;
    
    private float value = 1F;
    private boolean downloading = false;
    
    private TrayIcon trayIcon;
    
    public SnapTrayIcon(final YawkatSnap snapper) {
        this.snapper = snapper;
    }
    
    @Override
    public void run() {
        try {
            this.trayIcon = new TrayIcon(new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR));
            this.trayIcon.setToolTip("YawkatSnap");
            final PopupMenu popup = new PopupMenu();
            final MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    }).start();
                }
            });
            final MenuItem snap = new MenuItem("Snap");
            snap.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SnapTrayIcon.this.snapper.doSnap();
                        }
                    }).start();
                }
            });
            final MenuItem config = new MenuItem("Config");
            config.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent arg0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final JFrame jframe = new JFrame("Config");
                            jframe.setLayout(new BorderLayout());
                            jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            jframe.setResizable(false);
                            final Runnable callback = new Runnable() {
                                @Override
                                public void run() {
                                    jframe.setVisible(false);
                                    jframe.dispose();
                                }
                            };
                            jframe.add(new SettingsPanel(callback, callback, SnapTrayIcon.this.snapper.config));
                            jframe.validate();
                            jframe.pack();
                            jframe.setLocationRelativeTo(null);
                            jframe.setVisible(true);
                        }
                    }).start();
                }
            });
            popup.add(config);
            popup.add(snap);
            popup.add(exit);
            this.trayIcon.setPopupMenu(popup);
            SystemTray.getSystemTray().add(this.trayIcon);
            this.trayIcon.setImage(this.generateLogo());
        } catch (final HeadlessException e) {
            e.printStackTrace();
        } catch (final AWTException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void setValue(final float value) {
        final boolean newDownloading = value >= 0;
        if (newDownloading != this.downloading || value != this.value) {
            this.downloading = newDownloading;
            this.value = value;
            this.trayIcon.setImage(this.generateLogo());
        }
    }
    
    private BufferedImage generateLogo() {
        final Dimension traySize = this.trayIcon.getSize();
        final BufferedImage image = new BufferedImage(traySize.width, traySize.height, this.downloading ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
        if (this.downloading) {
            final Graphics2D gfx = image.createGraphics();
            gfx.setColor(Color.BLUE);
            final int h = (int) (traySize.height * (1 - this.value));
            gfx.fillRect(0, h, traySize.width, traySize.height - h);
            gfx.dispose();
        }
        return image;
    }
    
    public void displayMessage(final String caption, final String text, final MessageType type) {
        this.trayIcon.displayMessage(caption, text, type);
    }
}
