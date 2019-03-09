import java.awt.*;
import java.util.List;
import java.util.*;

public class Chromosome {
    private int[] cromosome;
    private Pixel[][] imageMat;
    private ImageMat img;
    private int numberOfSegments;

    private List<List<Integer>> segments;
    private int[] segementDivision;

    private double deviation;
    private double connectivity;


    private Chromosome(ImageMat img, int numberOfSegments) {
        cromosome = new int[img.getHeight() * img.getWidth()];
        this.img = img;
        this.imageMat = img.getPixels();
        this.numberOfSegments = numberOfSegments;
        this.segments = new ArrayList<>();
        this.segementDivision = new int[img.getHeight() * img.getWidth()];
        initPrimMST(img);
        findSegments();
        this.deviation = overallDeviation(this.segments);
        this.connectivity = overallConnectivity();
    }

    private void initPrimMST(ImageMat img) {
        for (int i = 0; i < cromosome.length; i++) cromosome[i] = i;
        HashSet<Integer> visited = new HashSet<>(img.getWidth() * img.getWidth());
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();

        List<Edge> worstEdges = new ArrayList<>();

        int current = new SplittableRandom().nextInt(0, cromosome.length - 1);
        while (visited.size() < cromosome.length) {
            if (!visited.contains(current)) {
                visited.add(current);
                addEdges(priorityQueue, getPixelonIndex(current), img.getPixels());
            }
            Edge edge = priorityQueue.poll();
            if (!visited.contains(edge.getTo())) {
                cromosome[edge.getTo()] = edge.getFrom();
                if (worstEdges.size() == 0 || edge.getDistance() > worstEdges.get(0).getDistance()) {
                    this.addToWorst(edge, worstEdges);
                }
            }
            current = edge.getTo();
        }

        for (Edge e : worstEdges) {
            this.cromosome[e.getFrom()] = e.getFrom();
        }
    }

    private void findSegments() {
        ArrayList<Integer> roots = new ArrayList<>();
        for (int i = 0; i < this.cromosome.length; i++) {
            if (this.cromosome[i] == i) {
                roots.add(i);
                this.segments.add(new ArrayList<>(Collections.singletonList(i)));
                segementDivision[i] = segments.size()-1;
            }
        }
        //adding every pixel to one sement
        for (int i = 0; i < this.cromosome.length; i++) {
            //if already added as root, skip
            if (this.cromosome[i] == i) {
                continue;
            }
            int current = i;
            //search for root by backtracking
            while (this.cromosome[current] != current) {
                current = this.cromosome[current];
            }
            int segmentIdx = roots.indexOf(current);
            this.segments.get(segmentIdx).add(i);
            segementDivision[i] = segmentIdx;
        }
    }

    private void addToWorst(Edge e, List<Edge> worstEdges) {
        //replace the best edge in worstedges if needed
        worstEdges.add(e);
        worstEdges.sort(Comparator.comparingDouble(Edge::getDistance));
        if (worstEdges.size() > this.numberOfSegments - 1) {
            worstEdges.remove(0);
        }
    }

    private void addEdges(PriorityQueue<Edge> candidateEdges, Pixel currentPixel, Pixel[][] mat) {
        //add left neighbours
        if (currentPixel.getColIdx() > 0) {
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx() - 1]));
            if (currentPixel.getRowIdx() > 0) {
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() - 1]));
            }
            if (currentPixel.getRowIdx() + 1 < mat.length) {
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() - 1]));
            }
        }
        //add right neighbours
        if (currentPixel.getColIdx() + 1 < mat[0].length) {
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx()][currentPixel.getColIdx() + 1]));
            if (currentPixel.getRowIdx() > 0) {
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() + 1]));
            }
            if (currentPixel.getRowIdx() + 1 < mat.length) {
                candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() + 1]));
            }
        }
        //add up and down
        if (currentPixel.getRowIdx() > 0) {
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx()]));
        }
        if (currentPixel.getRowIdx() + 1 < mat.length) {
            candidateEdges.add(new Edge(currentPixel, mat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx()]));
        }
    }


    public int[] getCromosome() {
        return cromosome;
    }

    //Evaluates the degree to which neighbouring pixels have been placed in the same segment
    private double overallConnectivity() {
        double connectiviy = 0;
        for (List<Integer> segment : this.segments) {
            //Find segment center
            for(int pixel: segment){
                Pixel currentPixel = getPixelonIndex(pixel);
                if (currentPixel.getColIdx() > 0) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() - 1].pixelIdx);
                    if (currentPixel.getRowIdx() > 0) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()-1][currentPixel.getColIdx() - 1].pixelIdx);
                    }
                    if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()+1][currentPixel.getColIdx() - 1].pixelIdx);
                    }
                }
                //add right neighbours
                if (currentPixel.getColIdx() + 1 < imageMat[0].length) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() + 1].pixelIdx);
                    if (currentPixel.getRowIdx() > 0) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()-1][currentPixel.getColIdx() + 1].pixelIdx);
                    }
                    if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()+1][currentPixel.getColIdx() + 1].pixelIdx);
                    }
                }
                //add up and down
                if (currentPixel.getRowIdx() > 0) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()-1][currentPixel.getColIdx()].pixelIdx);
                }
                if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()+1][currentPixel.getColIdx()].pixelIdx);
                }

            }
        }
        return connectiviy;
    }

    private double checkNeighbour(int current, int target){
        if(segementDivision[current] == segementDivision[target]){
            return 0;
        }
        else{
            return 0.125;
        }
    }


    // measure of the ‘similarity’ (homogeneity) of pixels in the same segment
    // Assumes a 2D list in the form of [[1,52,23]] where the numbers are pixelnumbers
    private double overallDeviation(List<List<Integer>> segments) {
        double deviation = 0;
        //Change when we have 2d list
        for (List<Integer> segment : this.segments) {
            //Find segment center
            Color centroidColor = getSegmentCentroid(segment);
            //List<Integer> centerPos = getSegmentCenter(segment);
            //Pixel centerPixel = imageMat[centerPos.get(0)][centerPos.get(1)];
            for (Integer integer : segment) {
                Pixel toPixel = getPixelonIndex(integer);
                deviation += Edge.distColor(toPixel, centroidColor);
            }
        }
        return deviation;
    }
    //TODO - have we missed something here? Centroid is the average color of all the pixels in the segment


    private List<List<Integer>> getSegments() {
        return segments;
    }

    //The centroid is the average color of all the pixels in one segment.
    private Color getSegmentCentroid(List<Integer> segment) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int numberOfPixelsInSegment = segment.size();
        for (Integer integer : segment) {
            Pixel temp = getPixelonIndex(integer);
            red += temp.getRed();
            green += temp.getGreen();
            blue += temp.getBlue();
        }
        red = red / numberOfPixelsInSegment;
        green = green / numberOfPixelsInSegment;
        blue = blue / numberOfPixelsInSegment;

        Color centroid = new Color(red, green, blue);
        return centroid;
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
        segmentCenter.add((minSegmentWidth + (segmentWidth - minSegmentWidth)) / 2);
        segmentCenter.add((minSegmentHeight + (segmentHeight - minSegmentHeight)) / 2);
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

        ImageMat loadImg = new ImageMat("86016");
        Chromosome test = new Chromosome(loadImg, 3);


        for(List<Integer> l : test.segments){
            System.out.println(l);
        }


        List<List<Integer>> testSeg = test.getSegments();
        for (int index : testSeg.get(0)) {
            test.getPixelonIndex(index).color = Color.green;
        }
        System.out.println(test.getDeviation());
        System.out.println(test.connectivity);
        test.img.saveAs("test.jpg");
    }
}
