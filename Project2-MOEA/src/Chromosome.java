import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chromosome {
    int[] cromosome;
    Pixel[][] imageMat;
    ImageMat img;
    List<ArrayList<Integer>> segments;
  
  
    public Chromosome(ImageMat img) {
        cromosome = new int[img.getHeight()*img.getWidth()];
        this.img = img;
        this.imageMat = img.getPixels();
        initPrimMST(img);
        this.segments = new ArrayList<>();
    }

    private void initPrimMST(ImageMat img){
        Pixel[][] mat = img.getPixels();
        Pixel currentPixel = mat[0][0];
        List<Pixel> visitedPixels = new ArrayList<>();
        List<Edge> candidateEdges = new ArrayList<>();

        List<Edge> worstEdges = new ArrayList<Edge>();
        int numberOfWorstEdges = 5;

        visitedPixels.add(currentPixel);
        while(visitedPixels.size() < img.getHeight()*img.getWidth()){
            addEdges(candidateEdges, currentPixel, mat);

            Edge bestEdge = candidateEdges.get(0);
            candidateEdges.remove(0);
            // Check if the edge "to pixel" is already connected in the MST
            while(visitedPixels.contains(bestEdge.getTo())) {
                bestEdge = candidateEdges.get(0);
                candidateEdges.remove(0);
            }

            this.cromosome[bestEdge.getFrom().getPixelIdx()] = bestEdge.getTo().getPixelIdx();
            visitedPixels.add(currentPixel);
            currentPixel = bestEdge.getTo();
        }
    }


    private void addEdges(List<Edge> candidateEdges, Pixel currentPixel, Pixel[][] mat){
        if(currentPixel.getColIdx() > 0){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()-1]));
            if(currentPixel.getRowIdx() > 0){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()-1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()-1]));
            }
        }
        if(currentPixel.getColIdx()+1 < mat[0].length){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()+1]));
            if(currentPixel.getRowIdx() > 0){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()+1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()+1]));
            }
        }
        if(currentPixel.getRowIdx() > 0){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()]));
        }
        if(currentPixel.getRowIdx()+1 < mat.length){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()]));
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


    public int[] getCromosome() {
        return cromosome;
    }


    // measure of the ‘similarity’ (homogeneity) of pixels in the same segment
    // Assumes a 2D list in the form of [[1,52,23]] where the numbers are pixelnumbers
    private double overallDeviation(Chromosome segments) {
        double deviation = 0;
        //Change when we have 2d list
        for (List<Integer> segment: this.segments) {
            //Find segment center
            List<Integer> centerPos = getSegmentCenter(segment);
            Pixel centerPixel = imageMat[centerPos.get(0)][centerPos.get(1)];
            for (int pixel = 0; pixel < segment.size(); pixel++ ) {
                Pixel toPixel = getPixelonIndex(segment.get(pixel));
                deviation += Edge.dist(centerPixel, toPixel);
            }
        }
        return deviation;
    }

    private List<Integer> getSegmentCenter(List<Integer> segment) {
        List<Integer> segmentCenter = new ArrayList<>();
        int segmentWidth = 0;
        int segmentHeight = 0;
        int minSegmentWidth = Integer.MAX_VALUE;
        int minSegmentHeight = Integer.MAX_VALUE;
        for (int pixel = 0; pixel < segment.size(); pixel++ ) {
            Pixel temp = getPixelonIndex(segment.get(pixel));
            if(temp.getRowIdx() > segmentWidth) {
                segmentWidth = temp.getRowIdx();
            }
            if(temp.getRowIdx() < minSegmentWidth) {
                minSegmentWidth = temp.getRowIdx();
            }
            if(temp.getColIdx() > segmentHeight) {
                segmentHeight = temp.getColIdx();
            }
            if(temp.getColIdx() < minSegmentHeight) {
                minSegmentHeight = temp.getColIdx();
            }
        }
        segmentCenter.add((minSegmentWidth + (segmentWidth-minSegmentWidth))/2);
        segmentCenter.add((minSegmentHeight + (segmentHeight-minSegmentHeight))/2);
        return segmentCenter;
    }

    private Pixel getPixelonIndex(int pixelNumber) {
        //TODO: Check for bugs
        int rowIndex = pixelNumber / this.img.getWidth();
        int colIndex = pixelNumber % this.img.getWidth();
        return imageMat[rowIndex][colIndex];
    }

    public static void main(String[] args) {
        ImageMat loadImg = new ImageMat("1");
        Chromosome test = new Chromosome(loadImg);
    }
}
