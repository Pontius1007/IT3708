import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageMat {
    int width;
    int height;
    Color[][] pixels;

    public ImageMat(String imageFile) {
        try {
            File input = new File("Test_Images/"+imageFile+"/Test image.jpg");
            BufferedImage image = ImageIO.read(input);
            this.width = image.getWidth();
            this.height = image.getHeight();

            this.pixels = new Color[height][width];


            for(int i=0; i<this.height; i++) {
                for(int j=0; j<this.width; j++) {
                    Color c = new Color(image.getRGB(i, j));
                    pixels[i][j] = c;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color[][] getPixels() {
        return pixels;
    }

    public static void main(String[] args) {
        ImageMat test = new ImageMat("86016");
        System.out.println(test.getHeight());
        System.out.println(test.getWidth());
    }
}
