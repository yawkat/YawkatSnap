package at.yawk.snap;

import java.awt.image.BufferedImage;
import java.util.Properties;

public interface SaveTarget {
    public void setProperties(Properties properties);
    
    /**
     * Save an image to this target (e.g. FTP)
     * 
     * @param image
     *            The image to be uploaded
     * @return The decided image ID
     * @throws Exception 
     */
    public String saveTo(BufferedImage image, IdGenerator idGenerator, UpdateMonitor monitor) throws Exception;
}
