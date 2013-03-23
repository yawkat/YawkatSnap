package at.yawk.snap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.JDialog;

public class FullscreenFrameCropDisplay implements ImageCropDisplay {
    private Runnable callback;
    private BufferedImage originalImage;
    private BufferedImage croppedImage;
    
    @Override
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
    
    @Override
    public void display() {
        final JDialog cutFrame = new JDialog();
        cutFrame.setResizable(false);
        cutFrame.setLayout(new BorderLayout());
        final AtomicReference<Rectangle> selectedPart = new AtomicReference<Rectangle>(null);
        cutFrame.add(new JComponent() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics gfx) {
                gfx.drawImage(originalImage, 0, 0, null);
                final Rectangle selected = selectedPart.get();
                gfx.setColor(new Color(0, 0, 0, 140));
                if (selected != null) {
                    final Rectangle positiveSelected = getAbsolute(selected);
                    gfx.fillRect(0, 0, getWidth(), positiveSelected.y);
                    gfx.fillRect(0, positiveSelected.y + positiveSelected.height, getWidth(), getHeight() - positiveSelected.y - positiveSelected.height);
                    gfx.fillRect(0, positiveSelected.y, positiveSelected.x, positiveSelected.height);
                    gfx.fillRect(positiveSelected.x + positiveSelected.width, positiveSelected.y, getWidth() - positiveSelected.x - positiveSelected.width, positiveSelected.height);
                    gfx.setColor(Color.BLACK);
                    gfx.drawRect(positiveSelected.x - 1, positiveSelected.y - 1, positiveSelected.width + 1, positiveSelected.height + 1);
                } else {
                    gfx.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        });
        cutFrame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        cutFrame.setUndecorated(true);
        cutFrame.pack();
        cutFrame.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
                cutFrame.setVisible(false);
                cutFrame.dispose();
                final Rectangle absRect = getAbsolute(selectedPart.get());
                croppedImage = new BufferedImage(absRect.width, absRect.height, BufferedImage.TYPE_3BYTE_BGR);
                final Graphics2D gfx = croppedImage.createGraphics();
                gfx.drawImage(originalImage, 0, 0, absRect.width, absRect.height, absRect.x, absRect.y, absRect.x + absRect.width, absRect.y + absRect.height, null);
                gfx.dispose();
                if (callback != null) {
                    callback.run();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                selectedPart.set(new Rectangle(e.getPoint()));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        cutFrame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                final Rectangle rect = selectedPart.get();
                if (rect != null) {
                    rect.setSize(e.getX() - rect.x, e.getY() - rect.y);
                    cutFrame.repaint();
                }
            }
        });
        ScreenUtil.getGraphicsDevice().setFullScreenWindow(cutFrame);
        cutFrame.setModal(true);
        cutFrame.setVisible(true);
    }
    
    @Override
    public void setImage(BufferedImage image) {
        originalImage = image;
    }
    
    @Override
    public BufferedImage getCroppedImage() {
        return croppedImage;
    }
    
    private static Rectangle getAbsolute(Rectangle original) {
        final Rectangle abs = new Rectangle();
        abs.x = original.width < 0 ? original.x + original.width : original.x;
        abs.y = original.height < 0 ? original.y + original.height : original.y;
        abs.width = Math.abs(original.width);
        abs.height = Math.abs(original.height);
        return abs;
    }
}
