package org.jboss.seam.examples.booking.util;

import org.apache.commons.io.FileUtils;
import org.jboss.solder.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: J
 * Date: 29.07.2009
 * Time: 19:31:33
 */
@ApplicationScoped
@Named
public class ImageConverter {
    @Inject
    protected Logger log;

    private interface Converter{
        BufferedImage prepare(File input) throws Exception;
        BufferedImage prepare(InputStream input) throws Exception;
        void resize(Object prepared, File outFile, ImageFormat format) throws Exception;
        void convert(File from, File to, ImageFormat format) throws Exception;
        void convert(InputStream from, File to, ImageFormat format) throws Exception;
    }

    private abstract class AbstractConverter implements Converter{
        public void convert(File from, File to, ImageFormat format) throws Exception {
            BufferedImage prepared = prepare(from);
            resize(prepared, to, format);
        }
        @Override
        public void convert(InputStream from, File to, ImageFormat format) throws Exception {
            BufferedImage prepared = prepare(from);
            resize(prepared, to, format);
        }
    }

    private class JavaConverter extends AbstractConverter{
        public BufferedImage prepare(File input) throws IOException {
            return ImageIO.read(input);
        }

        @Override
        public BufferedImage prepare(InputStream input) throws Exception {
            return ImageIO.read(input);
        }

        private BufferedImage scale(BufferedImage image, ImageFormat format) {
            TwoTuple<Rectangle, Rectangle> tt = getScaledSize(image.getWidth(), image.getHeight(), format.getSize(), format.getSize2(), format.getResizeType());
            int width = tt.second.width;
            int height = tt.second.height;
            if (tt.first != null){
                Rectangle r = tt.first;
                image = image.getSubimage(r.x, r.y, r.width, r.height);
            }
            int type = image.getType() == 0 ? format.isTransparent() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB : image.getType();
            BufferedImage tmp = new BufferedImage(width, height, type);
            Graphics2D g2 = tmp.createGraphics();
            if (!format.isTransparent()) {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(image, 0, 0, width, height, null);
            g2.dispose();

            return tmp;
        }

        public void resize(Object prepared, File f, ImageFormat format) throws IOException {
            BufferedImage scaled = scale((BufferedImage) prepared, format);
            if (!ImageIO.write(scaled, format.getFormat().getExt(), f)) {
                throw new RuntimeException("can't save image");
            }
        }



    }

    static TwoTuple<Rectangle, Rectangle> getScaledSize(int imageWidth, int imageHeight, int size, int size2, ImageResizeType resizeType) {
        int width = 0;
        int height = 0;
        Rectangle crop = null;

        switch (resizeType) {
            case SQUARE:
                if ((imageWidth > size || imageHeight > size) && (imageHeight != imageWidth)){
                    int x;
                    int y;
                    if (imageWidth > imageHeight){
                        x = (imageWidth-imageHeight)/2;
                        y = 0;
                        //noinspection SuspiciousNameCombination
                        imageWidth = imageHeight;
                    }else{
                        x = 0;
                        y = (imageHeight-imageWidth)/2;
                        //noinspection SuspiciousNameCombination
                        imageHeight = imageWidth;
                    }
                    crop = new Rectangle(x, y, imageWidth, imageHeight);
                }
                //no break - fall through
            case WIDTH:
                if (imageWidth > size) {
                    width = size;
                    height = (int) Math.round((double) imageHeight / imageWidth * width);
                } else {
                    width = imageWidth;
                    height = imageHeight;
                }
                break;
            case RECTANGLE_CENTER:

                width = imageWidth;
                height = imageHeight;

                if ((imageWidth > size || imageHeight > size2)){
                    int x = 0;
                    int y = 0;

                    double ratioWidth = (double)imageWidth/size;
                    double ratioHeight = (double)imageHeight/size2;

                    if (ratioWidth > ratioHeight) {
                        if (ratioHeight > 1) {
                            x = (int)(imageWidth - size * ratioHeight) / 2;
                            imageWidth = imageWidth - 2*x;
                        }

                        y = 0;

                    } else {
                        x = 0;
                        if (ratioWidth > 1) {
                            y = (int)(imageHeight - size2 * ratioWidth)/2;
                            imageHeight = imageHeight - 2*y;
                        }
                    }

                    crop = new Rectangle(x, y, imageWidth, imageHeight);

                }

                if (imageWidth > size) {
                    width = size;
                    if (imageHeight > size2) {
                        height = size2;
                    } else {
                        height = (int) Math.round((double) imageHeight / imageWidth * width);
                    }
                }

                if (imageHeight > size2) {
                    height = size2;
                    width = (int) Math.round((double) imageWidth / imageHeight * height);
                }

                break;
            default:
                if (imageWidth > size || imageHeight > size) {
                    if (imageWidth >= imageHeight) {
                        width = size;
                        height = (int) Math.round((double) imageHeight / imageWidth * size);
                    } else {
                        height = size;
                        width = (int) Math.round((double) imageWidth / imageHeight * size);
                    }
                } else {
                    width = imageWidth;
                    height = imageHeight;
                }
                break;
        }
        //первый - кроп, второй - ресайз
        return new TwoTuple<Rectangle, Rectangle>(crop, new Rectangle(width, height));
    }

    private Converter converter = new JavaConverter();

    public void convert(File from, File to, ImageFormat format){
        try {
            File tmp = new File(to.getParent(), "_tmp_i_"+to.getName());
            if (tmp.exists()){
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
            }
            converter.convert(from, tmp, format);
            FileUtils.moveFile(tmp, to);
        } catch (Exception e) {
            String message = "Image converting failed: ";
            log.error(message, e);
            throw new RuntimeException(message, e);
		}
    }

    public void convert(InputStream from, File to, ImageFormat format){
        try {
            File tmp = new File(to.getParent(), "_tmp_i_"+to.getName());
            if (tmp.exists()){
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
            }
            converter.convert(from, tmp, format);
            if(to.exists()) to.delete();
            FileUtils.moveFile(tmp, to);
        } catch (Exception e) {
            String message = "Image converting failed: ";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

}
