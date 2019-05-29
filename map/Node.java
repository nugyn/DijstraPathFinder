package map;
import java.util.*;
import map.Coordinate;

public class Node {
    public Coordinate coord;
    public ArrayList<Node> neighbours = new ArrayList<Node>();
    public int accumlativeCost = 0; 
    public boolean visited = false;
    public Node parent;
    public boolean scanned = false;
    /* 
     * accumlativeCost =  cost of itself + parent cost;
     */
    public Node(Coordinate node, Node parent) { 
        this.parent = parent;
        this.coord = node;
        if(parent!=null) {
            this.accumlativeCost += this.coord.terrainCost + parent.accumlativeCost;
        }
    }

    public void addNeighbour(Node neighbour) {
        neighbours.add(neighbour);
        scanned = true;
    }

    public int[] getPosition() {
        return coord.getPosition();
    }

    public boolean removeNeighbour(Node neighbour) {
        this.neighbours.remove(neighbour);
        return true;
    }
    public ArrayList<Node> notVisited() {
        /* 
           Return the neighbours of nodes that has not visited.
           If empty, it self checked itself. 
        */
        ArrayList<Node> result = new ArrayList<Node>();


        if(neighbours != null) {
            for(Node node: neighbours) {
                if(node.visited != true) {
                    result.add(node);
                }
            }
            if(result.isEmpty() && parent != null) {
                parent.removeNeighbour(this);
                // this.visited = true;
            }
            return result;
        } else {
            return null;
        }
    }
}