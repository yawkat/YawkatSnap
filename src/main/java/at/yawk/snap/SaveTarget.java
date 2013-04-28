package at.yawk.snap;

import java.awt.image.RenderedImage;
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
    public String saveTo(RenderedImage image, IdGenerator idGenerator, UpdateMonitor monitor) throws Exception;
}
