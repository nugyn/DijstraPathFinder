package pathFinder;

import map.Coordinate;
import map.PathMap;
import map.Node;
import java.util.*;

public class DijkstraPathFinder implements PathFinder
{
    /* PSEUDO:
     *1. Make a dictionary to store the visted nodes shortest path total weight
     *2. Make a ArrList- Linklist to store the shortest path

     *3. Go to the node, generate type Node based on Coordinate. Fill in the neighbours
     *4. Add the weight node to the dictionary, the node to the array with head = The source
     *5. if Node == goal. or Node.weight >= dict[Node], STOP!.
     *6. If visited every node's neighbour, check the parent visited.
     *
     * Weight get by direction coordinate.cost.
     */

    private PathMap map;
    private Map<Node, Integer> totalWeight = new HashMap<Node, Integer>();
    private Map<Node, ArrayList<Node>> shortestPaths = new HashMap<Node, ArrayList<Node>>();
    private Coordinate sourceCoord;
    private Coordinate destinationCoord;
    private Node sourceNode;
    private static final int WE_DONT_NEED_THIS = -1;

    public DijkstraPathFinder(PathMap map) {
        this.map = map;
        this.sourceCoord = map.originCells.get(0);
        this.destinationCoord = map.destCells.get(0);
        sourceNode = new Node(sourceCoord, null);

    } // end of DijkstraPathFinder()

    public boolean explorable(Node currNode) {
        /* Compare if the weight of the currNode is smaller than it in the dictionary.
           If it's smaller, we update the ArrayList<Node> for it. */

        int currWeight = currNode.accumlativeCost;
        ArrayList<Node> currentPath = new ArrayList<Node>();
        try {
            int weight = (int) totalWeight.get(currNode);
            if(currWeight < weight) {
                totalWeight.put(currNode, currWeight);
                /*  update in the map list */
                while(currNode.parent != null) {
                    currentPath.add(currNode);
                    currNode = currNode.parent;
                }
                shortestPaths.put(currNode, currentPath);
                return true;
            }
        } catch(NullPointerException e) {
            /* Happens when dictionary doesn't contain the Node, so the first item in the dictionary. */

            totalWeight.put(currNode, currWeight);
            currentPath.add(currNode);
            shortestPaths.put(currNode, currentPath);
            return true;
        }
        return false;
    }

    @Override
    public List<Coordinate> findPath() {
        List<Coordinate> path = new ArrayList<Coordinate>();
        Node currNode = sourceNode;
        while(!currNode.visited && currNode != null) {
            System.out.println(currNode.coord);

            ArrayList<Node> currNeighBours = currNode.notVisited();
            if((explorable(currNode) || (!currNeighBours.isEmpty() && currNeighBours != null)) &&
                currNode.getPosition() != destinationCoord.getPosition()) {
                /* (if it's explorable or it has no children left to vist) and not the goal */
                ArrayList<Node> unvisited = ScanAround(currNode);
                try {
                    currNode = unvisited.get(0);
                } catch (NullPointerException e) {
                    /* If the unvisited list is empty */
                    currNode = currNode.parent;
                }
            } else {
                if(currNode.parent != null) {
                    currNode.parent.removeNeighbour(currNode);
                }
                currNode.visited = true;
                currNode = currNode.parent;
            }
        }
        System.out.println(currNode);
        return path;
    } // end of findPath()

    @Override
    public int coordinatesExplored() {
        return WE_DONT_NEED_THIS;
    }
    
    public ArrayList<Node> ScanAround(Node parentNode) {
        //Look for from x,y: (X, Y + 1), (X + 1, Y), (X, Y - 1), (X - 1, Y), create node and then add it to its neighBour.
        int c ,r;
        c = parentNode.coord.getColumn();
        r = parentNode.coord.getRow();    

        //Build neighbours
        if(parentNode.scanned == false) {
            if(map.isIn(map.cells[r][c + 1])){
                Node neighbour = new Node(map.cells[r][c + 1], parentNode);
                parentNode.addNeighbour(neighbour);
            }
            if(map.isIn(map.cells[r + 1][c])){
                Node neighbour = new Node(map.cells[r + 1][c], parentNode);
                parentNode.addNeighbour(neighbour);
            }
            if(map.isIn(map.cells[r][c - 1])){
                Node neighbour = new Node(map.cells[r][c - 1], parentNode);
                parentNode.addNeighbour(neighbour);
            }
            if(map.isIn(map.cells[r - 1][c])){
                Node neighbour = new Node(map.cells[r - 1][c], parentNode);
                parentNode.addNeighbour(neighbour);
            }
        }             
        return parentNode.notVisited();

    } // end of ScanAround()



} // end of class DijsktraPathFinder
