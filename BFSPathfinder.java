import java.util.*;

/**
 * BFSPathfinder.java — Enemy AI using Breadth-First Search
 *
 * ALGORITHM:
 * ──────────
 * BFS guarantees the shortest path in an unweighted grid.
 * Time complexity: O(V + E) = O(COLS × ROWS) per call.
 *
 * Steps (as per Lecture 8 slide 5):
 *  1. Treat the game map as a grid of COLS × ROWS cells.
 *  2. Start from the enemy's current grid cell (ex, ey).
 *  3. Use a Queue to visit neighbours: Up, Down, Left, Right.
 *     Skip cells that are obstacles or already visited.
 *  4. Stop when the player's cell (px, py) is reached.
 *  5. Reconstruct the path via parent pointers.
 *  6. Return the FIRST step of that path (the next cell to move to).
 *
 * This ensures enemies navigate AROUND obstacles intelligently,
 * never moving in a fixed straight line.
 */
public class BFSPathfinder {

    // 4-directional movement: Up, Down, Left, Right
    private static final int[] DX = { 0,  0, -1,  1 };
    private static final int[] DY = {-1,  1,  0,  0 };

    /**
     * Computes the next grid cell the enemy should move to.
     *
     * @param ex    enemy current column
     * @param ey    enemy current row
     * @param px    player current column
     * @param py    player current row
     * @param map   the game map (obstacle data)
     * @param cols  grid width
     * @param rows  grid height
     * @return int[]{nextCol, nextRow} or null if no path exists
     */
    public static int[] nextStep(int ex, int ey, int px, int py,
                                  GameMap map, int cols, int rows) {
        // Already on player cell
        if (ex == px && ey == py) return null;

        // BFS queue holds {col, row}
        Queue<int[]> queue = new LinkedList<>();
        // Parent map: parent[col][row] = {parentCol, parentRow}
        int[][][] parent = new int[cols][rows][];
        boolean[][] visited = new boolean[cols][rows];

        queue.add(new int[]{ex, ey});
        visited[ex][ey] = true;

        boolean found = false;

        // ── BFS expansion ─────────────────────────────────────────────
        outer:
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int cx = cur[0], cy = cur[1];

            for (int d = 0; d < 4; d++) {
                int nx = cx + DX[d];
                int ny = cy + DY[d];

                // Boundary + obstacle + visited check
                if (nx < 0 || nx >= cols || ny < 0 || ny >= rows) continue;
                if (map.isObstacle(nx, ny)) continue;
                if (visited[nx][ny]) continue;

                visited[nx][ny] = true;
                parent[nx][ny]  = new int[]{cx, cy};
                queue.add(new int[]{nx, ny});

                if (nx == px && ny == py) { found = true; break outer; }
            }
        }

        if (!found) return null; // player unreachable (fully surrounded by obstacles)

        // ── Path reconstruction ───────────────────────────────────────
        // Walk from player back to the cell whose parent IS the enemy start
        int cx = px, cy = py;
        while (true) {
            int[] par = parent[cx][cy];
            if (par[0] == ex && par[1] == ey) {
                return new int[]{cx, cy}; // next step
            }
            cx = par[0];
            cy = par[1];
        }
    }
}
