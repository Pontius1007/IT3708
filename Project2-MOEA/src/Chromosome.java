import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Chromosome {

    int[][] chrom;

    public Chromosome(Image img){
        chrom = new int[img.getHeight()][img.getWidth()];
        initializeMst(img);
    }

    private void initializeMst(Image img){
        Color[][] pixels = img.getPixelArray();
        List<Edge> possibleEdges = new ArrayList<>();
        int pixelcount = img.getHeight()*img.getWidth();
        Color[] visitedPixels = new Color[pixelcount];
        Color currentPixel = pixels[0][0];
        int count = 0;
        visitedPixels[count] = currentPixel;
        while(count < pixelcount-1){

        }
    }

}
