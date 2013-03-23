package at.yawk.snap;

import java.awt.image.BufferedImage;

public interface ImageCropDisplay {
    public void setCallback(Runnable callback);
    
    public void display();
    
    public void setImage(BufferedImage image);
    
    public BufferedImage getCroppedImage();
}
