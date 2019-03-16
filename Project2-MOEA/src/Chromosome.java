import java.awt.*;
import java.util.List;
import java.util.*;

public class Chromosome {
    public int[] chromosome;
    public static ImageMat img;
    private int numberOfSegments;

    public int[] segementDivision;

    private double deviation;
    private double connectivity;
    private double crowding_distance;
    private boolean useDeviation = true; //0
    private boolean useConnectivity = true; //1
    private int rank;

    //Used for normal GA
    private double weightedSum = Integer.MAX_VALUE;

    private int lastMergeSize = -1;

    public Chromosome(int numberOfSegments) {
        chromosome = new int[img.getHeight() * img.getWidth()];
        this.numberOfSegments = numberOfSegments;
        initPrimMST(img);
        findSegments();
        this.deviation = overallDeviation();
        this.connectivity = overallConnectivity();
    }


    public Chromosome(Chromosome c2, double mutationRate) {
        chromosome = new int[img.getHeight() * img.getWidth()];
        for (int x = 0; x < chromosome.length; x++) {
            chromosome[x] = c2.chromosome[x];
        }
        for (int i = 0; i < chromosome.length; i++) {
            if (new SplittableRandom().nextInt(0, 100) < mutationRate * 100) {
                mutateRandomEdge(i);
            }
        }
        findSegments();
        this.deviation = overallDeviation();
        this.connectivity = overallConnectivity();
    }


    // crossover constructor
    public Chromosome(Chromosome father, Chromosome mother, double mutationRate) {
        chromosome = new int[img.getHeight() * img.getWidth()];
        this.segementDivision = new int[img.getHeight() * img.getWidth()];
        //integer for index to take genes from mother instead of father.
        for (int i = 0; i < chromosome.length; i++) {
            if (new SplittableRandom().nextInt(0, 2) == 0) {
                chromosome[i] = father.chromosome[i];
            } else {
                chromosome[i] = mother.chromosome[i];
            }
        }
        for (int i = 0; i < chromosome.length; i++) {
            if (new SplittableRandom().nextInt(0, 100) < mutationRate * 100) {
                mutateRandomEdge(i);
            }
        }
        findSegments();
        this.deviation = overallDeviation();
        this.connectivity = overallConnectivity();
    }

    public void mutateRandomEdge(int pixelIndex) {
        List<Integer> neigbours = getNeighbours(pixelIndex);
        //change edge to a random possible edge for the pixel
        chromosome[pixelIndex] = neigbours.get(new SplittableRandom().nextInt(0, neigbours.size()));
    }

    public void mutateBestEdge(int )

    public void mutateMergeTwoRandomSegments(){
        List<Edge> connectingEdges = new ArrayList<>();
        for(int pixel = 0; pixel < chromosome.length; pixe)
    }

    private void initPrimMST(ImageMat img) {
        for (int i = 0; i < chromosome.length; i++) chromosome[i] = i;
        HashSet<Integer> visited = new HashSet<>(img.getWidth() * img.getWidth());
        // Edges sorted after color distance in priorityQueue
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();
        PriorityQueue<Edge> edgesQueue = new PriorityQueue<>(Collections.reverseOrder());
        List<Edge> worstEdges = new ArrayList<>();
        // Random starting point for the prims algorithm
        int current = new SplittableRandom().nextInt(0, chromosome.length - 1);
        while (visited.size() < chromosome.length) {
            if (!visited.contains(current)) {
                visited.add(current);
                // add all possible edges from current pixel to all neighbours.
                addEdges(priorityQueue, getPixelonIndex(current), img.getPixels());
            }
            Edge edge = priorityQueue.poll();
            // add the best scoring edge to the MST if the "to node" is not visited.

            if (!visited.contains(edge.getTo())) {
                chromosome[edge.getTo()] = edge.getFrom();
                worstEdges.add(edge);
                // adds the n worst edges, to remove them and make segments.
            }
            current = edge.getTo();
        }

        Collections.sort(worstEdges);
        Collections.reverse(worstEdges);

        for (int i = 0; i <numberOfSegments-1; i++) {
            Edge removeEdge = worstEdges.get(i);
            this.chromosome[removeEdge.getFrom()] = removeEdge.getFrom();
        }
    }

    private List<List<Integer>> getSegmentMatrix() {
        List<List<Integer>> segmentMat = new ArrayList<>();
        for (int i = 0; i < numberOfSegments; i++) {
            segmentMat.add(new ArrayList<>());
        }
        for (int i = 0; i < segementDivision.length; i++) {
            segmentMat.get(segementDivision[i]).add(i);
        }
        return segmentMat;
    }

    private void findSegments() {
        //roots is all pixels representing one segment. (pointing to itself)
        segementDivision = new int[chromosome.length];
        Arrays.fill(segementDivision, -1);
        int currentSegmentID = 0;
        List<Integer> currentSegment;
        for (int i = 0; i < chromosome.length; i++) {

            if (segementDivision[i] != -1) continue;
            currentSegment = new ArrayList<>();
            currentSegment.add(i);
            segementDivision[i] = currentSegmentID;
            //Sets next pixel to pointer in chromosome. See chromosome representation. Will be one of the neighbours
            int nextPixel = chromosome[i];
            //As long as the neighbour does not belong to a segment
            while (segementDivision[nextPixel] == -1) {
                //Loops and adds pixel to segment. Updates segmentDivision-list.
                currentSegment.add(nextPixel);
                segementDivision[nextPixel] = currentSegmentID;
                nextPixel = chromosome[nextPixel];
            }
            //If connected to another segment "merges" them together
            if (segementDivision[i] != segementDivision[nextPixel]) {
                //Sets segment to the parent segment
                int setSegment = segementDivision[nextPixel];
                for (int pixelidx : currentSegment) {
                    segementDivision[pixelidx] = setSegment;
                }
            } else {
                currentSegmentID++;
            }

        }
        numberOfSegments = currentSegmentID;
        /*for (int segid : segementDivision) {
            if (segid > numberOfSegments) {
                numberOfSegments = segid;
            }
        }*/
    }

    public void mergeAllSmallerThanN(int n, int counter){
        int[] segmentcount = new int[numberOfSegments];
        for(int segId: segementDivision){
            segmentcount[segId]++;
        }

        List<Integer> toMerge = new ArrayList<>();
        for(int i = 0; i < segmentcount.length; i++){
            if(segmentcount[i] < n){
                toMerge.add(i);
            }
        }
        System.out.println(toMerge.size());
        if(lastMergeSize == toMerge.size()) counter++;
        if(toMerge.size() == 0 || counter > 20) return;
        for(int segId: toMerge){
            Edge bestEdge = findBestEdgeFromSegment(segId);
            chromosome[bestEdge.getFrom()] = bestEdge.getTo();
        }
        lastMergeSize = toMerge.size();
        findSegments();
        mergeAllSmallerThanN(n, counter);
    }

    public Edge findBestEdgeFromSegment(int segIdx){
        List<Integer> seg = getSegmentMatrix().get(segIdx);
        double bestDist = Double.MAX_VALUE;
        Edge bestEdge = new Edge(getPixelonIndex(0), getPixelonIndex(0));
        for(int pixelIdx: seg){
            for(int neighbourIdx: getNeighbours(pixelIdx)){
                if(segementDivision[pixelIdx] != segementDivision[neighbourIdx]){
                    Edge currentEdge = new Edge(getPixelonIndex(pixelIdx), getPixelonIndex(neighbourIdx));
                    if(currentEdge.getDistance() < bestDist){
                        bestDist = currentEdge.getDistance();
                        bestEdge = currentEdge;
                    }
                }
            }
        }
        return bestEdge;
    }

    public void mergeNsmallestSegments(int n){
        int[] segmentcount = new int[numberOfSegments];
        for(int segId: segementDivision){
            segmentcount[segId]++;
        }
    }

    private void addToWorst(Edge e, List<Edge> worstEdges) {
        //replace the best edge in worstedges if needed
        worstEdges.add(e);
        worstEdges.sort(Comparator.comparingDouble(Edge::getDistance));
        if (worstEdges.size() > this.numberOfSegments) {
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
        return chromosome;
    }

    public List<Integer> getNeighbours(int pixelIndex) {
        List<Integer> neighbours = new ArrayList<>();
        Pixel currentPixel = getPixelonIndex(pixelIndex);
        // checks if a neighbour is out of bounds of the matrix, and adds to neighbours if not.
        // add left neighbours
        Pixel[][] imageMat = img.getPixels();
        if (currentPixel.getColIdx() > 0) {
            neighbours.add(imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() - 1].getPixelIdx());
            if (currentPixel.getRowIdx() > 0) {
                neighbours.add(imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() - 1].getPixelIdx());
            }
            if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                neighbours.add(imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() - 1].getPixelIdx());
            }
        }
        //add right neighbours
        if (currentPixel.getColIdx() + 1 < imageMat[0].length) {
            neighbours.add(imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() + 1].getPixelIdx());
            if (currentPixel.getRowIdx() > 0) {
                neighbours.add(imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() + 1].getPixelIdx());
            }
            if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                neighbours.add(imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() + 1].getPixelIdx());
            }
        }
        //add up and down
        if (currentPixel.getRowIdx() > 0) {
            neighbours.add(imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx()].getPixelIdx());
        }
        if (currentPixel.getRowIdx() + 1 < imageMat.length) {
            neighbours.add(imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx()].getPixelIdx());
        }
        return neighbours;
    }

    public boolean isEdge(int pixelIndex) {
        for (int neighbourIndex : getNeighbours(pixelIndex)) {
            if (segementDivision[pixelIndex] != segementDivision[neighbourIndex]) {
                return true;
            }
        }
        return false;
    }

    //Evaluates the degree to which neighbouring pixels have been placed in the same segment
    private double overallConnectivity() {
        double connectiviy = 0;
        Pixel[][] imageMat = img.getPixels();
        for (List<Integer> segment : this.getSegmentMatrix()) {
            //Find segment center
            for (int pixel : segment) {
                Pixel currentPixel = getPixelonIndex(pixel);
                if (currentPixel.getColIdx() > 0) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() - 1].pixelIdx);
                    if (currentPixel.getRowIdx() > 0) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() - 1].pixelIdx);
                    }
                    if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() - 1].pixelIdx);
                    }
                }
                //add right neighbours
                if (currentPixel.getColIdx() + 1 < imageMat[0].length) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx()][currentPixel.getColIdx() + 1].pixelIdx);
                    if (currentPixel.getRowIdx() > 0) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx() + 1].pixelIdx);
                    }
                    if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                        connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx() + 1].pixelIdx);
                    }
                }
                //add up and down
                if (currentPixel.getRowIdx() > 0) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() - 1][currentPixel.getColIdx()].pixelIdx);
                }
                if (currentPixel.getRowIdx() + 1 < imageMat.length) {
                    connectiviy += checkNeighbour(currentPixel.pixelIdx, imageMat[currentPixel.getRowIdx() + 1][currentPixel.getColIdx()].pixelIdx);
                }

            }
        }
        return connectiviy;
    }

    private double checkNeighbour(int current, int target) {
        if (segementDivision[current] == segementDivision[target]) {
            return 0;
        } else {
            return 0.125;
        }
    }


    // measure of the ‘similarity’ (homogeneity) of pixels in the same segment
    // Assumes a 2D list in the form of [[1,52,23]] where the numbers are pixelnumbers
    private double overallDeviation() {
        double deviation = 0;
        //Change when we have 2d list
        for (List<Integer> segment : getSegmentMatrix()) {
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

    private Pixel getPixelonIndex(int pixelNumber) {
        int rowIndex = pixelNumber / this.img.getWidth();
        int colIndex = pixelNumber % this.img.getWidth();
        return img.getPixels()[rowIndex][colIndex];
    }

    //TODO: Needs optimalization
    public void setWeightedSum() {
        findSegments();
        this.deviation = overallDeviation();
        this.connectivity = overallConnectivity();
        this.weightedSum = this.connectivity*8 + this.deviation;
    }

    double getDeviation() {
        return deviation;
    }

    double getConnectivity() {
        return connectivity;
    }

    public void setCrowding_distance(double crowding_distance) {
        this.crowding_distance = crowding_distance;
    }

    public double getCrowding_distance() {
        return crowding_distance;
    }

    public boolean isUseDeviation() {
        return useDeviation;
    }

    public boolean isUseConnectivity() {
        return useConnectivity;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getWeightedSum() {
        return weightedSum;
    }

    static Comparator<Chromosome> deviationComparator() {
        return Comparator.comparingDouble(Chromosome::getDeviation);
    }

    static Comparator<Chromosome> weightedSumComparator() {
        return Comparator.comparingDouble(Chromosome::getWeightedSum);
    }

    static Comparator<Chromosome> connectivityComparator() {
        return Comparator.comparingDouble(Chromosome::getConnectivity);
    }

    //Return 1 if object 2 should be before object 1
    static Comparator<Chromosome> nonDominatedCrowdingComparator() {
        return ((o1, o2) -> {
            if (o1.getRank() < o2.getRank()) return -1;
            if (o1.getRank() > o2.getRank()) return 1;
            if (o1.getCrowding_distance() > o2.getCrowding_distance()) return -1;
            if (o1.getCrowding_distance() < o2.getCrowding_distance()) return 1;
            return 0;
        });
    }

    public static void main(String[] args) {
        ImageMat loadImg = new ImageMat("160068");
        Chromosome.img = loadImg;
        Chromosome test = new Chromosome(50000);
        test.mergeAllSmallerThanN(1000, 0);

        for(List<Integer> seg: test.getSegmentMatrix()){
            System.out.println(seg.size());
        }

        Chromosome.img.saveAsGreen("blablalbal", test);
        Chromosome.img.saveAsBlackAndWhite("bnw", test);

    }
}
