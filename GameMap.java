import java.awt.*;
import java.util.Random;

/**
 * GameMap.java — Grid map with asteroid obstacles.
 *
 * The map is a COLS×ROWS boolean grid. Cells marked true are obstacles.
 * BFSPathfinder treats obstacle cells as impassable walls.
 *
 * Obstacles are generated randomly but:
 *  - Top 2 rows are always clear (enemy spawn zone)
 *  - Bottom 3 rows are always clear (player zone)
 *  - Density ≈ 18% to keep paths available
 */
public class GameMap {

    private boolean[][] grid; // grid[col][row]
    private int cols, rows;

    // Pre-computed asteroid shapes for variety
    private int[] astW, astH;
    private Color[] astColor;

    public GameMap() {
        this.cols = GamePanel.COLS;
        this.rows = GamePanel.ROWS;
        grid = new boolean[cols][rows];
        astW = new int[cols * rows];
        astH = new int[cols * rows];
        astColor = new Color[cols * rows];
        generateObstacles();
    }

    private void generateObstacles() {
        Random r = new Random(); // random seed → different layout every restart
        int cell = GamePanel.CELL;
        for (int c = 0; c < cols; c++) {
            for (int row = 0; row < rows; row++) {
                // Keep top 2 and bottom 3 rows clear
                if (row < 2 || row >= rows - 3) { grid[c][row] = false; continue; }
                grid[c][row] = r.nextDouble() < 0.18;
                int idx = c * rows + row;
                astW[idx]   = cell - 8 - r.nextInt(8);
                astH[idx]   = cell - 8 - r.nextInt(8);
                int base = 80 + r.nextInt(50);
                astColor[idx] = new Color(base, base - 10, base - 20);
            }
        }
    }

    public boolean isObstacle(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return true;
        return grid[col][row];
    }

    /**
     * Returns true if the given pixel rectangle overlaps any obstacle cell.
     * Used by the player to block movement into rocks.
     */
    public boolean collidesWithObstacle(java.awt.Rectangle rect) {
        int cell = GamePanel.CELL;
        // Check all grid cells that the rectangle could touch
        int c0 = rect.x / cell;
        int c1 = (rect.x + rect.width)  / cell;
        int r0 = rect.y / cell;
        int r1 = (rect.y + rect.height) / cell;
        for (int c = c0; c <= c1; c++) {
            for (int row = r0; row <= r1; row++) {
                if (isObstacle(c, row)) {
                    // Pixel-precise: check if the rock cell rect intersects the player rect
                    java.awt.Rectangle rockRect = new java.awt.Rectangle(
                        c * cell + 4, row * cell + 4, cell - 8, cell - 8);
                    if (rect.intersects(rockRect)) return true;
                }
            }
        }
        return false;
    }

    public void draw(Graphics2D g) {
        int cell = GamePanel.CELL;
        for (int c = 0; c < cols; c++) {
            for (int row = 0; row < rows; row++) {
                if (!grid[c][row]) continue;
                int idx = c * rows + row;
                int px  = c * cell + (cell - astW[idx]) / 2;
                int py  = row * cell + (cell - astH[idx]) / 2;
                // Shadow
                g.setColor(new Color(0, 0, 0, 80));
                g.fillOval(px + 4, py + 4, astW[idx], astH[idx]);
                // Rock body
                g.setColor(astColor[idx]);
                g.fillOval(px, py, astW[idx], astH[idx]);
                // Highlight
                g.setColor(new Color(200, 200, 200, 60));
                g.fillOval(px + 4, py + 4, astW[idx] / 3, astH[idx] / 3);
                // Outline
                g.setColor(new Color(60, 55, 50));
                g.drawOval(px, py, astW[idx], astH[idx]);
            }
        }
    }
}
