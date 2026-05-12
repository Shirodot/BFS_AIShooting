import java.awt.*;

/**
 * Bullet.java — Player bullet projectile
 */
public class Bullet {
    private int x, y;
    private static final int SPEED = 10;
    private static final int W = 6, H = 16;

    public Bullet(int x, int y) { this.x = x; this.y = y; }

    public void update() { y -= SPEED; }

    public void draw(Graphics2D g) {
        // Glow
        g.setColor(new Color(100, 200, 255, 80));
        g.fillOval(x - 3, y - 4, W + 6, H + 8);
        // Core
        GradientPaint gp = new GradientPaint(x, y, Color.WHITE, x, y + H, new Color(0, 150, 255));
        g.setPaint(gp);
        g.fillRoundRect(x, y, W, H, 3, 3);
    }

    public Rectangle getBounds() { return new Rectangle(x, y, W, H); }
    public int getX() { return x; }
    public int getY() { return y; }
}
