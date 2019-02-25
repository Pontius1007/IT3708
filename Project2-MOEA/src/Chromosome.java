import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chromosome {
    int[] cromosome;

    public Chromosome(ImageMat img) {
        cromosome = new int[img.getHeight()*img.getWidth()];
        initPrimMST(img);
    }

    private void initPrimMST(ImageMat img){
        Pixel[][] mat = img.getPixels();
        Pixel currentPixel = mat[0][0];
        List<Color> visitedPixels = new ArrayList<>();
        List<Edge> candidateEdges = new ArrayList<>();

        visitedPixels.add(currentPixel);
        while(visitedPixels.size() < img.getHeight()*img.getWidth()){
            addEdges(candidateEdges, currentPixel, mat);

            Edge bestEdge = candidateEdges.get(0);
            candidateEdges.remove(0);
            // Check if the edge "to pixel" is already connected in the MST
            while(visitedPixels.contains(bestEdge.getTo())){
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

    public static void main(String[] args) {
        ImageMat loadImg = new ImageMat("0");
        Chromosome test = new Chromosome(loadImg);
    }
}
