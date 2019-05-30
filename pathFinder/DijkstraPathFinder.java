package pathFinder;

import map.Coordinate;
import map.PathMap;
import map.Node;
import java.util.*;
import pathFinder.GoalPath;
import pathFinder.WayPoint;

public class DijkstraPathFinder implements PathFinder
{
    private PathMap map;
    private Map<Coordinate, Integer> totalWeight;
    private Map<Coordinate, List<Coordinate>> shortestPaths;
    private List<Coordinate> sourceCoord;
    private List<Coordinate> destinationCoord;
    private int count = 0;

    public DijkstraPathFinder(PathMap map) {
        this.map = map;
        this.sourceCoord = map.originCells;
        this.destinationCoord = map.destCells;
    } // end of DijkstraPathFinder()

    public boolean explorable(Node node) {
        /* Compare if the weight of the currNode is smaller than it in the dictionary.
           If it's smaller, we update the ArrayList<Node> for it. */
        Node currNode = node;
        int currWeight = currNode.accumlativeCost;
        ArrayList<Coordinate> currentPath = new ArrayList<Coordinate>();
        try {
            int weight = (int) totalWeight.get(currNode.coord);
            if(currWeight < weight) { // If it is shorter than what we have.
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
            /* Happens when dictionary doesn't contain the Node, 
            so it is the first item in the dictionary. */
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
        List<Coordinate> path = new ArrayList<Coordinate>();
        List<GoalPath> compareGoals = new ArrayList<GoalPath>();
        int compWeight = 0;
        try {
            for(Coordinate source: sourceCoord) {
                List<GoalPath> allThePaths = new ArrayList<GoalPath>();
                int smallestWeight = 0;
                GoalPath shortest = null;
                for(Coordinate goal: destinationCoord) {
                    allThePaths.add(wayPointTraverse(source, goal));
                }
                for(GoalPath aPath : allThePaths) {
                    if(aPath.weight < smallestWeight || smallestWeight == 0) {
                        smallestWeight = aPath.weight;
                        shortest = aPath;
                    }
                }
                /* We got the shortest destination */
                compareGoals.add(shortest);
            }
            for(GoalPath pathToGoal : compareGoals) {
                if(pathToGoal.weight < compWeight || compWeight == 0) {
                    compWeight = pathToGoal.weight;
                    path = pathToGoal.paths;
                }
            }
            System.out.println("path size: " + path.size());
        } catch(NullPointerException e) {
            /* No path found */
        }
        return path;
    } // end of findPath()

    public GoalPath wayPointTraverse(Coordinate source, Coordinate goal) {
        /* PSEUDO:
            To build paths for way points
            1) Build a path from source to the first waypoint
                1a) The path appened to a path Arraylist
            2) Then new source becomes the waypoint from previous step.
            3) Repeat.

            To build waypoints for multiple destination, and sources
            1) Choose 1 SourceCoord, build the paths to every DestCoord
                1a) Store these two paths into a map?
            2) Repeat until all Sources have been done
            3) For each SourceCoord, choose the smallest path. 
        */
        List<Coordinate> paths = new ArrayList<Coordinate>();
        int weight = 0;
        if(map.waypointCells.size() == 0){
            /* When the there no waypoints*/
            GoalPath shortestPath = shortestPathFrom(source, goal);
            paths = shortestPath.paths;
            weight = shortestPath.weight;
        } else if(map.waypointCells.size() > 0) { /* When there waypoints*/
            int index = 0; // to store the index of the end to append

            ArrayList<Coordinate> wayList = new ArrayList<Coordinate>(map.waypointCells);
            WayPoint currentWayPoint = shortestWayPoint(source, wayList);
            try {
                wayList.remove(currentWayPoint.self);

                paths = currentWayPoint.paths;
                weight = currentWayPoint.weight;
    
                while(wayList.size() > 0) {
                    /* Remove the last item of the paths to avoid duplication */
                    paths.remove(paths.size() - 1);
                    index = paths.size();
                    WayPoint nextWayPoint = shortestWayPoint(currentWayPoint.self, wayList);
                    paths.addAll(index, nextWayPoint.paths);
                    weight += nextWayPoint.weight;
                    currentWayPoint = nextWayPoint;
                    wayList.remove(currentWayPoint.self);
                }
                paths.remove(paths.size() - 1);
                index = paths.size();
                GoalPath shortestPath = shortestPathFrom(currentWayPoint.self, goal);
                paths.addAll(index, shortestPath.paths);
                weight += shortestPath.weight;
                return new GoalPath(paths, weight);
            } catch (NullPointerException e) {
                /* There is no path */
                return null;
            }

        }
        return new GoalPath(paths, weight);
    }

    public WayPoint shortestWayPoint(Coordinate source, List<Coordinate> waypoints) {
        /* Return a nearest waypoint from a waypoint list. */
        int smallestWeight = 0;
        WayPoint nearest = new WayPoint();
        
        for(Coordinate point : waypoints) {
            GoalPath tryPath = shortestPathFrom(source, point);
            if(tryPath != null) {
                if(smallestWeight == 0 || tryPath.weight < smallestWeight) {
                    smallestWeight = tryPath.weight;
                    nearest = new WayPoint(source, tryPath.paths, point, tryPath.weight);
                }
            } else {
                return null;
            }
        }

        return nearest;
    }

    public GoalPath shortestPathFrom(Coordinate source, Coordinate goal){
        /* PSEUDO:
            1. Make a dictionary to store the visted nodes shortest path total 
               weight

            2. Make a ArrList- Linklist to store the shortest path

            3. Go to the node, generate type Node based on Coordinate. Fill in 
               the neighbours

            4. if weight of the node < than existed one in the dictionary or 
               it's the first instance of the dictionary,
               add it in the dictionary and update the shortest path to it.

            5. if Node == goal. and Node.weight >= dict[Node] or there is no 
               child to visit, STOP!. Go to 8.

            6. else, we scan around to find to unvisited child, and assign the 
               current node to the first child. repeat from 4

            7. if there is  no children left to visit, we go back to the parent
               to check, repeat from 4.

            8. If the current node has a parent, we remove itself as the 
               neighbours.

            10. Mark the current node as visited and set the current node to be
               the parent. repeat from 4.

            11. we stop if the currentNode is null, when the current node is 
               null, we have the shortestPaths from the dictionary.
        */
        totalWeight = new HashMap<Coordinate, Integer>();
        shortestPaths = new HashMap<Coordinate, List<Coordinate>>();
        Node currNode = new Node(source, null);
        count  = 0;
        while(currNode != null && !currNode.visited) {
            ArrayList<Node> currNeighBours = currNode.notVisited();
            // When we reach our goal... It . (The nodes we do not vist)
            if(((explorable(currNode) && isNotDestination(currNode.coord, goal)) || (currNeighBours != null &&!currNeighBours.isEmpty()))) {
                /* (if it's explorable or it has no children left to vist) and not the goal */
                ArrayList<Node> unvisited = ScanAround(currNode); // this is where we add neighbours
                try {
                    currNode = unvisited.get(0); // set the node to be the first children
                } catch (Exception e){
                    // If there is no children left to visit, go back to the parent.
                    currNode = currNode.parent;
                }
                
            } else {
                /* 
                   This is likely happens when the weight of the path is larger
                   or it is the goal.
                   
                   We simply don't need to traverse further
                */
                if(currNode.parent != null) {
                    currNode.parent.removeNeighbour(currNode);
                }
                currNode.visited = true;
                currNode = currNode.parent;
            }
            count += 1;
        }
        if(shortestPaths.get(goal) != null) {
            Collections.reverse(shortestPaths.get(goal));
        } else {
            return null;
        }
        return new GoalPath(shortestPaths.get(goal), totalWeight.get(goal));
    }

    @Override
    public int coordinatesExplored() {
        return count;
    }
    
    public ArrayList<Node> ScanAround(Node node) {
        /*
         * Scan around for the potential neighbours. The condition is:
         * - The coordinate has to passable.
         * - It should not be the same with its parent.
         * Once the two conditions are met, we add the coordinate to the neighbours 
         * of the node.
         */
        int c = node.coord.getColumn();
        int r = node.coord.getRow();
        int i = 0;

        if(node.scanned == false) {

            if(isPassible(r+1, c) && notColliseWithParent(r+1, c, node)) {
                /* UP */
                Node neighbour = new Node(map.cells[r + 1][c], node);
                node.addNeighbour(neighbour);
                i++;
            }

            if(isPassible(r, c+1) && notColliseWithParent(r, c+1, node)) {
                /* RIGHT */
                Node neighbour = new Node(map.cells[r][c + 1], node);
                node.addNeighbour(neighbour);
                i++;
            }

            if(isPassible(r-1, c) && notColliseWithParent(r-1, c, node)) {
                /* DOWN */
                Node neighbour = new Node(map.cells[r - 1][c], node);
                node.addNeighbour(neighbour);
                i++;
            }

            if(isPassible(r, c-1) && notColliseWithParent(r, c-1, node)) {
                /* LEFT */
                Node neighbour = new Node(map.cells[r][c - 1], node);
                node.addNeighbour(neighbour);
            }
        }
        return node.notVisited();

    } // end of ScanAround()
    public boolean notColliseWithParent(int r, int c, Node sourceNode) {
        return sourceNode.parent == null || 
        (sourceNode.parent.coord.getColumn() != c || sourceNode.parent.coord.getRow() != r);
    }
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
