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
    private Map<Coordinate, Integer> totalWeight;
    private Map<Coordinate, List<Coordinate>> shortestPaths;
    private Map<Coordinate, List<Coordinate>> sourceCoordPaths = new HashMap<Coordinate, List<Coordinate>>();
    private Coordinate sourceCoord;
    private Coordinate destinationCoord;
    private Coordinate wayPointSourceCoord;
    private Coordinate wayPointDestinationCoord;
    private Node sourceNode;
    private int count = 0;

    public DijkstraPathFinder(PathMap map) {
        this.map = map;
        this.sourceCoord = map.originCells.get(0);
        this.destinationCoord = map.destCells.get(0);
        sourceNode = new Node(sourceCoord, null);

    } // end of DijkstraPathFinder()

    public boolean explorable(Node node) {
        /* Compare if the weight of the currNode is smaller than it in the dictionary.
           If it's smaller, we update the ArrayList<Node> for it. */
        Node currNode = node;
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
                currentPath.add(currNode.coord);
                shortestPaths.put(node.coord, currentPath);
                return true;
            } else {
                return false;
            }
        } catch(NullPointerException e) {
            /* Happens when dictionary doesn't contain the Node, so the first item in the dictionary. */
            while(currNode.parent != null) {
                currentPath.add(currNode.coord);
                currNode = currNode.parent;
            }
            currentPath.add(currNode.coord);
            totalWeight.put(node.coord, currWeight);
            shortestPaths.put(node.coord, currentPath);
            return true;
        }
    }

    @Override
    public List<Coordinate> findPath() {

         /* To build paths for way points

            1) Build a path from source to the first waypoint
                1a) The path appened to a path Arraylist
            2) Then new source becomes the waypoint from previous step.
            3) Repeat.
        
        */ 
        /*
        To build waypoints for multiple destination, and sources
            1) Choose 1 SourceCoord, build the paths to every DestCoord
                1a) Store these two paths into a map?
            2) Repeat until all Sources have been done
            3) For each SourceCoord, choose the smallest path. 
        */
        List<Coordinate> path = new ArrayList<Coordinate>();
        boolean begginning = true;
        /* When the there no waypoints*/
        if(map.waypointCells.size() == 0){
            path = shortestPathFrom(sourceCoord, destinationCoord);
            
        } else if(map.waypointCells.size() > 0) { /* When there waypoints*/
            int wayPointsHit = 0;
            int j = 0; // to store the index of the end to append
            int count = 0;
            ArrayList<Coordinate> wayList = new ArrayList<Coordinate>(map.waypointCells);
            Coordinate currentCord = wayList.remove(0);
            path = shortestPathFrom(sourceCoord, currentCord);
            while(wayList.size() > 0) {
                j = path.size();
                Coordinate nextCoordinate = wayList.remove(0);
                path.addAll(j, shortestPathFrom(currentCord, nextCoordinate));
                currentCord = nextCoordinate;
            }
            path.addAll(j, shortestPathFrom(currentCord, destinationCoord));
            return path;
            /* Everytime, we reach a wayPoint, increment, continue to perform until we reach the last waypoint*/
            // while(wayPointsHit <= map.waypointCells.size()){ 
            //     /* Build a path to the first waypoint */
            //     if(begginning == true){
            //         wayPointSourceCoord = map.originCells.get(0); 
            //         wayPointDestinationCoord = map.waypointCells.get(0);
            //         path.addAll(j, shortestPathFrom(wayPointSourceCoord, wayPointDestinationCoord));
            //         begginning = false;
            //         j = path.size();
            //     } /* New source node is the previous waypoint, build a path, then REPEAT */
            //     else if(begginning == false){
            //         System.out.println("Beginning is false");
            //         for(int i = 0; i < map.waypointCells.size() - 1; i++){
            //             wayPointSourceCoord = map.waypointCells.get(i);
            //             wayPointDestinationCoord = map.waypointCells.get(i + 1); // 
            //             path.addAll(j, shortestPathFrom(wayPointSourceCoord, wayPointDestinationCoord));
            //             j = path.size();
            //             count++;                
            //             wayPointsHit++;
            //         }
            //     }
            //     /* Once all waypoints have has paths build, the last path is the last waypoint to the destination */
            //     if(wayPointsHit == map.waypointCells.size() - 1){
            //         System.out.println("++++++++++++++++>" + j + ":" + wayPointsHit + ":" + map.waypointCells.size() + ":" + count);
            //         wayPointSourceCoord = map.waypointCells.get(map.waypointCells.size() - 1);
            //         path.addAll(j, shortestPathFrom(wayPointSourceCoord, destinationCoord));
            //     }
            //     // wayPointsHit++;
            // }
           
        }
        // else if(map.destCells.size() > 1 || map.originCells.size() > 1){
        //   //  int j = 0;
        //     /* Build a path from each source, to every destination coordinate, then store each one in a list for comparison */
        //     while(j < map.originCells.size()){ 
        //         for(int i = 0; i < map.originCells.size(); i++){
        //             sourceCoord = map.originCells.get(j);
        //             destinationCoord = map.destCells.get(i);
        //             path = shortestPathFrom(sourceCoord, destinationCoord);
        //             // sourceCoordPaths.put(sourceCoord, shortestPathFrom(sourceCoord, destinationCoord));
        //         }
        //         j++;
        //     }
            
        // }        

        return path;
    } // end of findPath()

    public List<Coordinate> shortestPathFrom(Coordinate source, Coordinate goal){
        totalWeight = new HashMap<Coordinate, Integer>();
        shortestPaths = new HashMap<Coordinate, List<Coordinate>>();
        Node currNode = new Node(source, null);
        while(currNode != null && !currNode.visited) {
            ArrayList<Node> currNeighBours = currNode.notVisited();
            //When we reach our goal... It . (The nodes we do not vist)
            boolean notDestination = isNotDestination(currNode.coord, goal);
            boolean existNotVisitedChild = (!currNeighBours.isEmpty() && currNeighBours != null);
            if(((explorable(currNode)  && isNotDestination(currNode.coord, goal)) || (!currNeighBours.isEmpty() && currNeighBours != null))) {
                /* (if it's explorable or it has no children left to vist) and not the goal */
                ArrayList<Node> unvisited = ScanAround(currNode); // this is where we add neighbours
                try {
                    currNode = unvisited.get(0); // problem
                } catch (NullPointerException e) {
                    /* If the unvisited list is empty */
                    currNode = currNode.parent;
                }
            } else {
                if(currNode.parent != null) {
                    currNode.parent.removeNeighbour(currNode);
                }
                currNode.visited = true;
                count += 1;
                currNode = currNode.parent;
            }
        }
        return shortestPaths.get(goal);
    }
    @Override
    public int coordinatesExplored() {
        return count;
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
    public boolean isNotDestination(Coordinate coord, Coordinate goal) {
        if(coord.getRow() != goal.getRow() || coord.getColumn() != goal.getColumn()) {
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

    public boolean isEqual(Coordinate a, Coordinate b) {
        return (a.getColumn() == b.getColumn()) && (a.getRow() == b.getRow());
    }


} // end of class DijsktraPathFinder
