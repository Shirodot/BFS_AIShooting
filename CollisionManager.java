import java.util.*;

/**
 * CollisionManager.java — Detects and resolves all collisions.
 *
 * Three collision types:
 *  1. Bullet  × Enemy  → enemy destroyed, +100 score
 *  2. Enemy   × Player → player loses 1 HP, enemy removed
 *  3. (Obstacle collisions handled inside BFSPathfinder by skipping obstacle cells)
 */
public class CollisionManager {

    /**
     * Check all collisions in one pass.
     * Uses java.awt.Rectangle.intersects() for AABB detection — O(B×E + E).
     */
    public void check(Player player, List<Enemy> enemies,
                      List<Bullet> bullets, ScoreBoard score, GameMap map) {

        Iterator<Enemy> ei = enemies.iterator();
        while (ei.hasNext()) {
            Enemy en = ei.next();

            // ── Bullet × Enemy ───────────────────────────────────────
            Iterator<Bullet> bi = bullets.iterator();
            boolean killed = false;
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (b.getBounds().intersects(en.getBounds())) {
                    bi.remove();
                    killed = true;
                    break;
                }
            }
            if (killed) {
                score.addScore(100);
                ei.remove();
                continue;
            }

            // ── Enemy × Player ───────────────────────────────────────
            if (en.getBounds().intersects(player.getBounds())) {
                player.loseHp();
                ei.remove(); // enemy removed on collision
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * ScoreBoard.java — Tracks score, HP display, and level.
 */
class ScoreBoard {
    private int score = 0;
    private int level = 1;

    public void addScore(int pts) { score += pts; }
    public int  getScore()        { return score; }
    public int  getLevel()        { return level; }
    public void setLevel(int l)   { level = l; }
}
