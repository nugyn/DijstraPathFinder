package pathFinder;
import java.util.*;
import map.Coordinate;


public class GoalPath{
    public List<Coordinate> paths = new ArrayList<Coordinate>();
    public int weight;

    public GoalPath(List<Coordinate> paths, int weight) {
        this.paths = paths;
        this.weight = weight;
    }
}