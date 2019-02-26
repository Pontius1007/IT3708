import java.awt.*;
import java.util.*;
import java.util.List;

public class Chromosome {
    private int[] cromosome;
    private Pixel[][] imageMat;
    private ImageMat img;
    private int numberOfSegments;

    private List<List<Integer>> segments;

    private double deviation;
  
  
    private Chromosome(ImageMat img, int numberOfSegments) {
        cromosome = new int[img.getHeight()*img.getWidth()];
        this.img = img;
        this.imageMat = img.getPixels();
        this.numberOfSegments = numberOfSegments;
        this.segments = new ArrayList<>();
        initPrimMST(img);
        this.deviation = overallDeviation(this.segments);
    }

    private void initPrimMST(ImageMat img){
        Pixel[][] mat = img.getPixels();
        Pixel currentPixel = mat[0][0];

        List<Pixel> visitedPixels = new ArrayList<>();
        List<Edge> candidateEdges = new ArrayList<>();

        List<Edge> worstEdges = new ArrayList<Edge>();
        int numberOfWorstEdges = 5;
        double bestWorstEdge = 0;

        this.cromosome[0] = -1;
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

            this.cromosome[bestEdge.getTo().getPixelIdx()] = bestEdge.getFrom().getPixelIdx();
            if(worstEdges.size() == 0 || bestEdge.getDistance() > worstEdges.get(0).getDistance()){
                this.addToWorst(bestEdge, worstEdges);
            }
            visitedPixels.add(currentPixel);
            currentPixel = bestEdge.getTo();
        }
        //remove the n worst edges in the mst
        for(Edge e: worstEdges){
            this.cromosome[e.getFrom().getPixelIdx()] = -1;
        }

        findSegments();
    }

    private void findSegments() {
        ArrayList<Integer> roots = new ArrayList<>();
        for (int i = 0; i < this.cromosome.length; i++) {
            if (this.cromosome[i] == -1) {
                roots.add(i);
                this.segments.add(new ArrayList<>(Collections.singletonList(i)));
            }
        }
        //adding every pixel to one sement
        for(int i = 0; i < this.cromosome.length; i++){
            //if already added as root, skip
            if (this.cromosome[i] == -1) { continue; }
            int current = i;
            //search for root by backtracking
            while(this.cromosome[current] != -1){
                current = this.cromosome[current];
            }
            int segmentIdx = roots.indexOf(current);
            this.segments.get(segmentIdx).add(i);
        }
    }

    private void addToWorst(Edge e, List<Edge> worstEdges){
        //replace the best edge in worstedges if needed
        worstEdges.add(e);
        worstEdges.sort(Comparator.comparingDouble(Edge::getDistance));
        if(worstEdges.size() > this.numberOfSegments-1){
            worstEdges.remove(0);
        }
    }

    private void addEdges(List<Edge> candidateEdges, Pixel currentPixel, Pixel[][] mat){
        //add left neighbours
        if(currentPixel.getColIdx() > 0){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()-1]));
            if(currentPixel.getRowIdx() > 0){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()-1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()-1]));
            }
        }
        //add right neighbours
        if(currentPixel.getColIdx()+1 < mat[0].length){
            insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()+1]));
            if(currentPixel.getRowIdx() > 0){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()+1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                insertByDist(candidateEdges, new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()+1]));
            }
        }
        //add up and down
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
    private double overallDeviation(List<List<Integer>> segments) {
        double deviation = 0;
        //Change when we have 2d list
        for (List<Integer> segment: this.segments) {
            //Find segment center
            List<Integer> centerPos = getSegmentCenter(segment);
            Pixel centerPixel = imageMat[centerPos.get(0)][centerPos.get(1)];
            for (Integer integer : segment) {
                Pixel toPixel = getPixelonIndex(integer);
                deviation += Edge.dist(centerPixel, toPixel);
            }
        }
        return deviation;
    }

    private List<List<Integer>> getSegments() {
        return segments;
    }

    private List<Integer> getSegmentCenter(List<Integer> segment) {
        List<Integer> segmentCenter = new ArrayList<>();
        int segmentWidth = 0;
        int segmentHeight = 0;
        int minSegmentWidth = Integer.MAX_VALUE;
        int minSegmentHeight = Integer.MAX_VALUE;
        for (Integer integer : segment) {
            Pixel temp = getPixelonIndex(integer);
            if (temp.getRowIdx() > segmentWidth) {
                segmentWidth = temp.getRowIdx();
            }
            if (temp.getRowIdx() < minSegmentWidth) {
                minSegmentWidth = temp.getRowIdx();
            }
            if (temp.getColIdx() > segmentHeight) {
                segmentHeight = temp.getColIdx();
            }
            if (temp.getColIdx() < minSegmentHeight) {
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

    private double getDeviation() {
        return deviation;
    }

    public static void main(String[] args) {
        ImageMat loadImg = new ImageMat("353013");
        Chromosome test = new Chromosome(loadImg, 2);
        List<List<Integer>> testSeg = test.getSegments();
        for(List<Integer> l: testSeg){
            System.out.println(l);
        }
        for(int index: testSeg.get(1)){
            test.getPixelonIndex(index).color = Color.green;
        }
        System.out.println(test.getDeviation());
        test.img.saveAs("test.jpg");
    }
}
