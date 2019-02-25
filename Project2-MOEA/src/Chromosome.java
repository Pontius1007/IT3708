import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chromosome {
    int[] cromosome;
    Pixel[][] imageMat;
    ImageMat img;


    public Chromosome(ImageMat img) {
        cromosome = new int[img.getHeight()*img.getWidth()];
        this.img = img;
        //TODO 2D list
        this.imageMat = img.getPixels();
        initPrimMST(img);
    }

    public int[] getCromosome() {
        return cromosome;
    }

    private void initPrimMST(ImageMat img){
        Pixel[][] mat = img.getPixels();
        Pixel currentPixel = mat[0][0];
        List<Color> visitedPixels = new ArrayList<Color>();
        List<Edge> candidateEdges = new ArrayList<Edge>();

        visitedPixels.add(currentPixel);

        while(visitedPixels.size() < img.getHeight()*img.getWidth()){
            addEdges(candidateEdges, currentPixel, mat);
        }
    }

    private void addEdges(List<Edge> candidateEdges, Pixel currentPixel, Pixel[][] mat){
        if(currentPixel.getColIdx() > 0){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()]));
            if(currentPixel.getRowIdx() > 0){
                Edge insertEdge = new Edge();
            }
        }
    }

    private void insertByDist(List<Edge> candidates, Edge newEdge){
        for(int i = 0; i < candidates.size(); i++){
            if(newEdge.getDistance() < candidates.get(i).getDistance()){
                candidates.add(i, newEdge);
                return;
            }
        }
        candidates.add(newEdge);
    }

    // measure of the ‘similarity’ (homogeneity) of pixels in the same segment
    // Assumes a 2D list in the form of [[1,52,23]] where the numbers are pixelnumbers
    private double overallDeviation(Chromosome segments) {
        double deviation = 0;
        //Change when we have 2d list
        for (Integer segment:segments.getCromosome()) {
            //Find segment center
            ArrayList<Integer> centerPos = getSegmentCenter(segment);
            Pixel centerPixel = imageMat[centerPos.get(0)][centerPos.get(1)];
            for (int pixel = 0; pixel < segment.length; pixel++ ) {
                Pixel toPixel = getPixelonIndex(segment.get(pixel));
                deviation += Edge.dist(centerPixel, toPixel);
            }
        }
        return deviation;
    }

    private List<Integer> getSegmentCenter(Integer[] segment) {
        List<Integer> segmentCenter = new ArrayList<>();
        int segmentWidth = 0;
        int segmentHeight = 0;
        for (int pixel = 0; pixel < segment.length; pixel++ ) {
            Pixel temp = getPixelonIndex(segment[pixel]);
            if(temp.getRowIdx() > segmentWidth) {
                segmentWidth = temp.getRowIdx();
            }
            if(temp.getColIdx() > segmentHeight) {
                segmentHeight = temp.getColIdx();
            }
        }
        segmentCenter.add(segmentWidth/2);
        segmentCenter.add(segmentHeight/2);
        return segmentCenter;
    }

    private Pixel getPixelonIndex(int pixelNumber) {
        //TODO: Check for bugs
        int rowIndex = pixelNumber / this.img.getWidth();
        int colIndex = pixelNumber % this.img.getWidth();
        return imageMat[rowIndex][colIndex];
    }

    public static void main(String[] args) {
        List<Integer> test = new ArrayList<>();

        test.add(9);
        test.add(5);
        test.add(3);
        test.add(0);
        test.add(5);

        Collections.sort(test);

        for(int i = 0; i < test.size(); i++){
            if(7 < test.get(i)){
                test.add(i, 7);
                break;
            }
        }

        System.out.println(test);

    }

}
