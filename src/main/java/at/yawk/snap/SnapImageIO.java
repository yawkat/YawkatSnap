package at.yawk.snap;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

public class SnapImageIO {
    private SnapImageIO() {
    }
    
    public static void write(RenderedImage im, String formatName, OutputStream output) throws IOException {
        /* TODO properly implement image compression
        if (formatName.equalsIgnoreCase("png")) {
            try {
                Class.forName("com.googlecode.pngtastic.core.PngImage");
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(im, formatName, stream);
                final PngImage image = new PngImage(new ByteArrayInputStream(stream.toByteArray()));
                image.setInterlace((short) 5);
                output.write(new PngOptimizer().optimize(image).getImageData());
            } catch (ClassNotFoundException e) {
            }
        }
        */
        ImageIO.write(im, formatName, output);
    }
}
