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
    private Map<Coordinate, Integer> totalWeight = new HashMap<Coordinate, Integer>();
    private Map<Coordinate, List<Coordinate>> shortestPaths = new HashMap<Coordinate, List<Coordinate>>();
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
        ArrayList<Coordinate> currentPath = new ArrayList<Coordinate>();
        try {
            int weight = (int) totalWeight.get(currNode.coord); // store coordinate instead of currentNode.
            if(currWeight < weight) {
                totalWeight.put(currNode.coord, currWeight);
                /*  update in the map list */
                while(currNode.parent != null) {
                    currentPath.add(currNode.coord);
                    currNode = currNode.parent;
                }
                shortestPaths.put(currNode.coord, currentPath);
                return true;
            } else  {
                return false;
            }
        } catch(NullPointerException e) {
            /* Happens when dictionary doesn't contain the Node, so the first item in the dictionary. */

            totalWeight.put(currNode.coord, currWeight);
            currentPath.add(currNode.coord);
            shortestPaths.put(currNode.coord, currentPath);
            return true;
        }
    }

    @Override
    public List<Coordinate> findPath() {
        List<Coordinate> path = new ArrayList<Coordinate>();
        Node currNode = sourceNode;
        while(!currNode.visited && currNode != null) {
            System.out.println("Current node:" + currNode.coord);
            ArrayList<Node> currNeighBours = currNode.notVisited();
            //When we reach our goal... It . (The nodes we do not vist)
            boolean notDestination = isNotDestination(currNode.coord);
            boolean existNotVisitedChild = (!currNeighBours.isEmpty() && currNeighBours != null);
            if(((explorable(currNode)  && isNotDestination(currNode.coord)) || (!currNeighBours.isEmpty() && currNeighBours != null))) {
                /* (if it's explorable or it has no children left to vist) and not the goal */
                ArrayList<Node> unvisited = ScanAround(currNode); // this is where we add neighbours
                try {
                    currNode = unvisited.get(0); // problem
                } catch (NullPointerException e) {
                    /* If the unvisited list is empty */
                                    System.out.println(e);
                    currNode = currNode.parent;
                }
            } else {
                                    System.out.println("check6");
                if(currNode.parent != null) {
                    currNode.parent.removeNeighbour(currNode);
                }
                currNode.visited = true;
                currNode = currNode.parent;
            }
            System.out.println("---end of loop---");
        }
        path = shortestPaths.get(destinationCoord);
        return path;
    } // end of findPath()

    @Override
    public int coordinatesExplored() {
        return WE_DONT_NEED_THIS;
    }
    
    public ArrayList<Node> ScanAround(Node parentNode) {

        /* UP: [r + 1][c] 
        RIGHT: [r][ c + 1] 
        DOWN: [r - 1][c] 
        LEFT: [r][c - 1] 
        
        ISSUE: Base Condition for when r or c are 0 (Creates Index out of Bounds)*/

        int c = parentNode.coord.getColumn();
        int r = parentNode.coord.getRow();
        int i = 0;
        //Build neighbours

        if(parentNode.scanned == false) {

            if(isPassible(r+1, c) && (parentNode.parent == null || (parentNode.parent.coord.getColumn() != c || parentNode.parent.coord.getRow() != r+1)) ) {
                /* UP */
                Node neighbour = new Node(map.cells[r + 1][c], parentNode);
                parentNode.addNeighbour(neighbour);
                System.out.println(parentNode.neighbours.get(i).coord);
                i++;
            }

            if(isPassible(r, c+1) && (parentNode.parent == null || (parentNode.parent.coord.getColumn() != c+1 || parentNode.parent.coord.getRow() != r))) {
                /* RIGHT */
                Node neighbour = new Node(map.cells[r][c + 1], parentNode);
                parentNode.addNeighbour(neighbour);
                System.out.println(parentNode.neighbours.get(i).coord);
                i++;
            }
            boolean isInCheck = map.isIn(r-1,c);
            boolean headerCheck = parentNode.parent == null;
            boolean notParentCheck =parentNode.parent == null || (parentNode.parent.coord.getColumn() != c || parentNode.parent.coord.getRow() != r-1);

            if(isPassible(r-1, c) && (parentNode.parent == null || (parentNode.parent.coord.getColumn() != c || parentNode.parent.coord.getRow() != r-1))) {
                /* DOWN */
                Node neighbour = new Node(map.cells[r - 1][c], parentNode);
                parentNode.addNeighbour(neighbour);
                System.out.println(parentNode.neighbours.get(i).coord);
                i++;
            }

            if(isPassible(r,c-1) && (parentNode.parent == null || (parentNode.parent.coord.getColumn() != c-1 || parentNode.parent.coord.getRow() != r))) {
                /* LEFT */
                Node neighbour = new Node(map.cells[r][c - 1], parentNode);
                parentNode.addNeighbour(neighbour);
                System.out.println(parentNode.neighbours.get(i).coord);
            }
        }
        return parentNode.notVisited();

    } // end of ScanAround()
    public boolean isNotDestination(Coordinate coord) {
        if(coord.getRow() != destinationCoord.getRow() || coord.getColumn() != destinationCoord.getColumn()) {
            return true;
        }
        return false;
    }
    public boolean isPassible(int row, int column) {
        if(map.isIn(row, column)){
            return !map.cells[row][column].getImpassable();
        }
        return false;
    }


} // end of class DijsktraPathFinder
