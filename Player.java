import java.awt.*;

/**
 * Player.java — Player airplane
 *
 * Drawn as a stylised fighter jet using pure Java2D (no image files required).
 * Tracks position, HP, and provides movement methods with boundary clamping.
 */
public class Player {

    private int x, y;
    private final int W = 40, H = 50;
    private int hp;

    public Player(int x, int y) {
        this.x  = x;
        this.y  = y;
        this.hp = 5;
    }

    // ── Movement (with obstacle collision) ────────────────────────────
    public void moveLeft(int speed, GameMap map) {
        x -= speed;
        if (x < 0) x = 0;
        if (map.collidesWithObstacle(getBounds())) x += speed;
    }
    public void moveRight(int speed, int maxX, GameMap map) {
        x += speed;
        if (x > maxX - W) x = maxX - W;
        if (map.collidesWithObstacle(getBounds())) x -= speed;
    }
    public void moveUp(int speed, GameMap map) {
        y -= speed;
        if (y < 0) y = 0;
        if (map.collidesWithObstacle(getBounds())) y += speed;
    }
    public void moveDown(int speed, int maxY, GameMap map) {
        y += speed;
        if (y > maxY - H) y = maxY - H;
        if (map.collidesWithObstacle(getBounds())) y -= speed;
    }

    // ── Rendering ────────────────────────────────────────────────────
    public void draw(Graphics2D g) {
        // Engine glow
        g.setColor(new Color(0, 100, 255, 80));
        g.fillOval(x + 13, y + H - 5, 14, 22);

        // Body
        int[] bx = {x + 20, x + 5,  x,    x + 5,  x + 20, x + 35, x + 40, x + 35};
        int[] by = {y,       y + 20, y + H, y + H,  y + H,  y + H,  y + 20, y + 20};
        g.setColor(new Color(30, 80, 180));
        g.fillPolygon(bx, by, 8);

        // Cockpit
        g.setColor(new Color(100, 200, 255));
        g.fillOval(x + 13, y + 10, 14, 18);

        // Wing highlights
        g.setColor(new Color(60, 140, 220));
        g.drawLine(x + 5, y + 20, x + 20, y + 10);
        g.drawLine(x + 35, y + 20, x + 20, y + 10);

        // Outline
        g.setColor(new Color(0, 180, 255));
        g.drawPolygon(bx, by, 8);

        // Thruster flame
        g.setColor(new Color(0, 150, 255, 200));
        g.fillOval(x + 14, y + H + 2, 12, 14);
        g.setColor(new Color(255, 255, 255, 160));
        g.fillOval(x + 17, y + H + 4, 6, 8);
    }

    // ── Accessors ────────────────────────────────────────────────────
    public Rectangle getBounds() { return new Rectangle(x + 5, y + 10, W - 10, H - 10); }
    public int getCenterX() { return x + W / 2; }
    public int getCenterY() { return y + H / 2; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }
    public void loseHp() { hp = Math.max(0, hp - 1); }
}
