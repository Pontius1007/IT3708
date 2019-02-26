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
  
  
    public Chromosome(ImageMat img, int numberOfSegments) {
        cromosome = new int[img.getHeight()*img.getWidth()];
        this.img = img;
        this.imageMat = img.getPixels();
        this.numberOfSegments = numberOfSegments;
        this.segments = new ArrayList<>();
        initPrimMST(img, numberOfSegments);
        this.deviation = overallDeviation(this.segments);
    }

    private void initPrimMST(ImageMat img, int numberOfSegments){
        for (int i = 0; i < cromosome.length; i++) cromosome[i] = i;
        HashSet<Integer> visited = new HashSet<>(img.getWidth()*img.getWidth());
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();

        List<Edge> worstEdges = new ArrayList<Edge>();
        double bestWorstEdge = 0;

        int current = 0; // Starts at the last pixel
        while (visited.size() < cromosome.length){
            if (!visited.contains(current)){
                visited.add(current);
                addEdges(priorityQueue, getPixelonIndex(current), img.getPixels());
            }
            Edge edge = priorityQueue.poll();
            if (!visited.contains(edge.getTo())){
                cromosome[edge.getTo()] = edge.getFrom();
                if(worstEdges.size() == 0 || edge.getDistance() > worstEdges.get(0).getDistance()){
                    this.addToWorst(edge, worstEdges);
                }
            }
            current = edge.getTo();
        }

        for(Edge e: worstEdges){
            this.cromosome[e.getFrom()] = e.getFrom();
        }

        findSegments();
    }

    private void findSegments() {
        ArrayList<Integer> roots = new ArrayList<>();
        for (int i = 0; i < this.cromosome.length; i++) {
            if (this.cromosome[i] == i) {
                roots.add(i);
                this.segments.add(new ArrayList<>(Arrays.asList(i)));
            }
        }
        //adding every pixel to one sement
        for(int i = 0; i < this.cromosome.length; i++){
            //if already added as root, skip
            if (this.cromosome[i] == i) { continue; }
            int current = i;
            //search for root by backtracking
            while(this.cromosome[current] != current){
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

    private void addEdges(PriorityQueue<Edge> candidateEdges, Pixel currentPixel, Pixel[][] mat){
        //add left neighbours
        if(currentPixel.getColIdx() > 0){
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()-1]));
            if(currentPixel.getRowIdx() > 0){
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()-1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()-1]));
            }
        }
        //add right neighbours
        if(currentPixel.getColIdx()+1 < mat[0].length){
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx()+1]));
            if(currentPixel.getRowIdx() > 0){
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()+1]));
            }
            if(currentPixel.getRowIdx()+1 < mat.length){
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()+1]));
            }
        }
        //add up and down
        if(currentPixel.getRowIdx() > 0){
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()]));
        }
        if(currentPixel.getRowIdx()+1 < mat.length){
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()]));
        }
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
            for (int pixel = 0; pixel < segment.size(); pixel++ ) {
                Pixel toPixel = getPixelonIndex(segment.get(pixel));
                deviation += Edge.dist(centerPixel, toPixel);
            }
        }
        return deviation;
    }

    public List<List<Integer>> getSegments() {
        return segments;
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

    public Pixel getPixelonIndex(int pixelNumber) {
        //TODO: Check for bugs
        int rowIndex = pixelNumber / this.img.getWidth();
        int colIndex = pixelNumber % this.img.getWidth();
        return imageMat[rowIndex][colIndex];
    }

    public double getDeviation() {
        return deviation;
    }

    public static void main(String[] args) {
        ImageMat loadImg = new ImageMat("1");
        Chromosome test = new Chromosome(loadImg, 100);
        List<List<Integer>> testSeg = test.getSegments();
        for(List<Integer> l: testSeg){
            System.out.println(l);
        }
        for(int index: testSeg.get(0)){
            test.getPixelonIndex(index).color = Color.green;
        }

        System.out.println(test.getDeviation());
        test.img.saveAs("test.jpg");
    }
}
