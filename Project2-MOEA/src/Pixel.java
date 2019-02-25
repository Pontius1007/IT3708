import java.awt.*;

public class Pixel extends Color {

    int rowIdx;
    int colIdx;
    int pixelIdx;

    public Pixel(int rgb, int rowIdx, int colIdx, int pixelIdx) {
        super(rgb);
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
        this.pixelIdx = pixelIdx;
    }


    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }

    public int getPixelIdx() { return pixelIdx; }
}
