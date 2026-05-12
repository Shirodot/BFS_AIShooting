import java.awt.*;

/**
 * Enemy.java — Enemy airplane that moves via BFS pathfinding.
 *
 * Each game tick the GamePanel calls BFSPathfinder.nextStep() and passes
 * the result to moveTo(). The enemy does NOT move in a straight line —
 * it navigates around obstacles using the shortest BFS path.
 */
public class Enemy {

    private int x, y;
    private final int W = 36, H = 40;
    private static int colorSeed = 0;
    private Color bodyColor;
    private Color accentColor;

    // Each enemy gets a slightly different colour for visual variety
    private static final Color[] BODY_COLORS   = {
        new Color(180, 30, 30), new Color(160, 20, 120),
        new Color(120, 20, 160), new Color(180, 80, 20)
    };
    private static final Color[] ACCENT_COLORS = {
        new Color(255, 80, 80), new Color(220, 60, 180),
        new Color(160, 60, 220), new Color(255, 140, 40)
    };

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        int idx = (colorSeed++) % BODY_COLORS.length;
        this.bodyColor   = BODY_COLORS[idx];
        this.accentColor = ACCENT_COLORS[idx];
    }

    /** Move toward target grid cell (called by GamePanel after BFS step) */
    public void moveTo(int nx, int ny) {
        this.x = nx;
        this.y = ny;
    }

    public void draw(Graphics2D g) {
        // Engine glow (facing DOWN — enemy comes from top)
        g.setColor(new Color(255, 80, 0, 100));
        g.fillOval(x + 11, y - 10, 14, 18);

        // Body
        int[] bx = {x + 18, x + 3,  x,      x + 3,  x + 18, x + 33, x + 36, x + 33};
        int[] by = {y + H,  y + 20,  y,      y,      y,      y,      y + 20, y + 20};
        g.setColor(bodyColor);
        g.fillPolygon(bx, by, 8);

        // Cockpit
        g.setColor(new Color(255, 60, 60, 200));
        g.fillOval(x + 11, y + 12, 14, 18);

        // Outline
        g.setColor(accentColor);
        g.drawPolygon(bx, by, 8);

        // Thruster (downward flame, enemy moves down)
        g.setColor(new Color(255, 120, 0, 200));
        g.fillOval(x + 12, y - 14, 12, 14);
        g.setColor(new Color(255, 220, 100, 160));
        g.fillOval(x + 15, y - 10, 6, 8);
    }

    public Rectangle getBounds() { return new Rectangle(x + 4, y + 4, W - 8, H - 8); }
    public int getCenterX() { return x + W / 2; }
    public int getCenterY() { return y + H / 2; }
    public int getX() { return x; }
    public int getY() { return y; }
}
