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
                Edge insertEdge = new Edge()
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
