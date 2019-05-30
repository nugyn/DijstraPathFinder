package pathFinder;
import java.util.*;
import map.Coordinate;

public class WayPoint {
    /*
        Contains the WayPoint paths from a source and its coordinate
     */
    public Coordinate source;
    public List<Coordinate> paths;
    public Coordinate self;
    public int weight;

    public WayPoint(Coordinate source, List<Coordinate> paths, Coordinate self, int weight) {
        this.source = source;
        this.paths = paths;
        this.self = self;
    }

    public WayPoint() {};
}