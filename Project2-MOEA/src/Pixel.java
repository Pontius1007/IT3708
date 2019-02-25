import java.awt.*;

public class Pixel extends Color {

    int rowIdx;
    int colIdx;


    public Pixel(int rgb, int rowIdx, int colIdx) {
        super(rgb);
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
    }


    public int getRowIdx() {
        return rowIdx;
    }

    public int getColIdx() {
        return colIdx;
    }
}
