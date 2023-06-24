/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.utility;

import ch.aplu.jgamegrid.Location;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import src.gameGrid.GridElementType;
import src.gameGrid.PacGameGrid;

import java.util.List;

public class PathFindingFacade {
    private DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath;

    public PathFindingFacade(PacGameGrid model) {
        Graph<String, DefaultEdge> graph = getGraph(model);
        dijkstraShortestPath = new DijkstraShortestPath<String, DefaultEdge>(graph);
    }

    public Location findPath(Location source, Location target) {
        GraphPath<String, DefaultEdge> path = dijkstraShortestPath.getPath(source.toString(), target.toString());
        if (path != null) {
            List<String> paths = path.getVertexList();
            String nextLocationStr = paths.get(1);
            return parseLocationString(nextLocationStr);
        }
        return null;
    }

    private Graph<String, DefaultEdge> getGraph(PacGameGrid model) {
        Graph<String, DefaultEdge> graph =
                new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        // Add all vertexs
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                Location loc = new Location(x, y);
                GridElementType type = GridElementType.charToGridElementType(model.getTile(x, y));
                if (type != GridElementType.WallTile) {
                    graph.addVertex(loc.toString());
                }
            }
        }
        // Add all edges
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                Location loc = new Location(x, y);
                GridElementType type = GridElementType.charToGridElementType(model.getTile(x, y));
                if (type == GridElementType.WallTile) {
                    continue;
                }
                if (type == GridElementType.PortalDarkGoldTile || type == GridElementType.PortalDarkGrayTile || type == GridElementType.PortalWhiteTile || type == GridElementType.PortalYellowTile) {
                    if (isValidPortalPair(model, type)) {
                        graph.addEdge(loc.toString(), findPortalTwinLoc(model, loc, type).toString());
                    }
                }
                for (Location.CompassDirection dir : Location.CompassDirection.values()) {
                    Location neighbourLoc = loc.getNeighbourLocation(dir);
                    if (!model.isInBound(neighbourLoc.getX(), neighbourLoc.getY())) {
                        continue;
                    }
                    GridElementType neighbourType = GridElementType.charToGridElementType(model.getTile(neighbourLoc.getX(), neighbourLoc.getY()));
                    if (neighbourType == GridElementType.WallTile) {
                        continue;
                    }
                    graph.addEdge(loc.toString(), neighbourLoc.toString());
                }
            }
        }
        return graph;
    }

    private boolean isValidPortalPair(PacGameGrid model, GridElementType type) {
        char c = type.toChar();
        int num = 0;
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                if (model.getTile(x, y) == c) {
                    num++;
                }
            }
        }
        if (num != 2) {
            return false;
        }
        return true;
    }

    private Location findPortalTwinLoc(PacGameGrid model, Location portalLoc, GridElementType type) {
        char c = type.toChar();
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                if ((x != portalLoc.getX() || y != portalLoc.getY()) && model.getTile(x, y) == c) {
                    return new Location(x, y);
                }
            }
        }
        return null;
    }

    private Location parseLocationString(String str) {
        str  = str.replace("(", "");
        str = str.replace(")", "");
        String[] locationStrings = str.split(", ");
        int[] locationCoord = new int[locationStrings.length];
        for (int i = 0; i < locationStrings.length; i++) {
            locationCoord[i] = Integer.parseInt(locationStrings[i]);
        }
        return new Location(locationCoord[0], locationCoord[1]);
    }

}
