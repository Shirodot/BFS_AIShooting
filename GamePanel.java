import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * GamePanel.java — Game loop, rendering, input, collision
 *
 * Responsibilities:
 *  - 60 FPS game loop via javax.swing.Timer
 *  - Keyboard input (Arrow keys / WASD = move, SPACE = shoot, R = restart, P = pause)
 *  - Spawn waves of enemies that use BFS pathfinding to chase the player
 *  - Collision detection delegated to CollisionManager
 *  - Draws all game objects and the ScoreBoard HUD
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // ── Layout ──────────────────────────────────────────────────────
    public static final int GAME_WIDTH  = 600;
    public static final int GAME_HEIGHT = 700;
    public static final int HUD_WIDTH   = 200;
    public static final int TOTAL_WIDTH = GAME_WIDTH + HUD_WIDTH;

    // ── Grid (used by BFS pathfinding) ──────────────────────────────
    public static final int CELL = 40;  // pixels per grid cell
    public static final int COLS = GAME_WIDTH  / CELL;  // 15
    public static final int ROWS = GAME_HEIGHT / CELL;  // 17

    // ── Timing ──────────────────────────────────────────────────────
    private static final int FPS           = 60;
    private static final int TICK_MS       = 1000 / FPS;
    private static final int ENEMY_MOVE_TICKS  = 30;  // enemy moves every N ticks
    private static final int BULLET_MOVE_TICKS = 2;
    private static final int SPAWN_TICKS       = 180; // new enemy every 3 s
    private static final int SURVIVAL_BONUS_TICKS = 300; // bonus every 5 s

    // ── Game objects ─────────────────────────────────────────────────
    private Player            player;
    private List<Enemy>       enemies;
    private List<Bullet>      bullets;
    private GameMap           map;
    private ScoreBoard        scoreBoard;
    private CollisionManager  collisionManager;

    // ── State ────────────────────────────────────────────────────────
    private boolean gameOver;
    private boolean paused;
    private int     tickCount;
    private int     level;

    // ── Key state map ─────────────────────────────────────────────────
    private Set<Integer> keysDown = new HashSet<>();

    // ── Swing timer ──────────────────────────────────────────────────
    private Timer timer;

    // ── Stars background ─────────────────────────────────────────────
    private int[][] stars; // [i][0]=x [i][1]=y [i][2]=brightness

    public GamePanel() {
        setPreferredSize(new Dimension(TOTAL_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initStars();
    }

    private void initStars() {
        Random r = new Random(42);
        stars = new int[120][3];
        for (int i = 0; i < 120; i++) {
            stars[i][0] = r.nextInt(GAME_WIDTH);
            stars[i][1] = r.nextInt(GAME_HEIGHT);
            stars[i][2] = 80 + r.nextInt(175);
        }
    }

    // ── Public API ────────────────────────────────────────────────────
    public void startGame() {
        initGame();
        timer = new Timer(TICK_MS, this);
        timer.start();
        requestFocusInWindow();
    }

    private void initGame() {
        map              = new GameMap();
        player           = new Player(GAME_WIDTH / 2 - 20, GAME_HEIGHT - 100);
        enemies          = new ArrayList<>();
        bullets          = new ArrayList<>();
        scoreBoard       = new ScoreBoard();
        collisionManager = new CollisionManager();
        gameOver  = false;
        paused    = false;
        tickCount = 0;
        level     = 1;
        spawnEnemyWave(2);
    }

    private void spawnEnemyWave(int count) {
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            // Random top position, avoiding obstacles
            int col, row;
            do {
                col = r.nextInt(COLS);
                row = r.nextInt(4);  // spawn in top 4 rows
            } while (map.isObstacle(col, row));
            enemies.add(new Enemy(col * CELL + 5, row * CELL + 5));
        }
    }

    // ── Game loop ─────────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !paused) {
            tickCount++;
            handleInput();
            updateBullets();
            updateEnemies();
            checkSpawns();
            collisionManager.check(player, enemies, bullets, scoreBoard, map);
            if (player.getHp() <= 0) gameOver = true;
        }
        repaint();
    }

    private void handleInput() {
        int speed = 4;
        if (keysDown.contains(KeyEvent.VK_LEFT)  || keysDown.contains(KeyEvent.VK_A))
            player.moveLeft(speed, map);
        if (keysDown.contains(KeyEvent.VK_RIGHT) || keysDown.contains(KeyEvent.VK_D))
            player.moveRight(speed, GAME_WIDTH, map);
        if (keysDown.contains(KeyEvent.VK_UP)    || keysDown.contains(KeyEvent.VK_W))
            player.moveUp(speed, map);
        if (keysDown.contains(KeyEvent.VK_DOWN)  || keysDown.contains(KeyEvent.VK_S))
            player.moveDown(speed, GAME_HEIGHT, map);
    }

    private void updateBullets() {
        if (tickCount % BULLET_MOVE_TICKS == 0) {
            for (Bullet b : bullets) b.update();
            bullets.removeIf(b -> b.getY() < -20);
        }
    }

    private void updateEnemies() {
        if (tickCount % ENEMY_MOVE_TICKS == 0) {
            int px = player.getCenterX() / CELL;
            int py = player.getCenterY() / CELL;
            for (Enemy en : enemies) {
                int ex = en.getCenterX() / CELL;
                int ey = en.getCenterY() / CELL;
                int[] next = BFSPathfinder.nextStep(ex, ey, px, py, map, COLS, ROWS);
                if (next != null) {
                    en.moveTo(next[0] * CELL + 5, next[1] * CELL + 5);
                }
            }
        }
    }

    private void checkSpawns() {
        // Survival bonus
        if (tickCount % SURVIVAL_BONUS_TICKS == 0 && tickCount > 0) {
            scoreBoard.addScore(50);
        }
        // Spawn more enemies
        if (tickCount % SPAWN_TICKS == 0 && tickCount > 0) {
            level = 1 + scoreBoard.getScore() / 500;
            scoreBoard.setLevel(level);
            spawnEnemyWave(1 + level / 2);
        }
    }

    // ── Rendering ──────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawGrid(g2);
        map.draw(g2);
        for (Bullet b  : bullets)  b.draw(g2);
        for (Enemy  en : enemies)  en.draw(g2);
        player.draw(g2);
        drawHUD(g2);

        if (paused)   drawOverlay(g2, "PAUSED",    "Press P to resume", new Color(0, 150, 255, 180));
        if (gameOver) drawOverlay(g2, "GAME OVER", "Press R to restart", new Color(200, 0, 0, 180));
    }

    private void drawBackground(Graphics2D g) {
        // Deep space gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(5, 5, 20),
                                              0, GAME_HEIGHT, new Color(10, 10, 40));
        g.setPaint(gp);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        // Stars
        for (int[] s : stars) {
            int br = s[2];
            g.setColor(new Color(br, br, br));
            g.fillRect(s[0], s[1], 2, 2);
        }
        // HUD background
        g.setColor(new Color(8, 12, 30));
        g.fillRect(GAME_WIDTH, 0, HUD_WIDTH, GAME_HEIGHT);
        g.setColor(new Color(0, 120, 200));
        g.drawLine(GAME_WIDTH, 0, GAME_WIDTH, GAME_HEIGHT);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 18));
        for (int c = 0; c <= COLS; c++) g.drawLine(c * CELL, 0, c * CELL, GAME_HEIGHT);
        for (int r = 0; r <= ROWS; r++) g.drawLine(0, r * CELL, GAME_WIDTH, r * CELL);
    }

    private void drawHUD(Graphics2D g) {
        int x = GAME_WIDTH + 15;
        // Title
        g.setColor(new Color(0, 180, 255));
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("AI SHOOTER", x, 30);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("SCORE", x, 65);
        g.setColor(new Color(255, 220, 0));
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.drawString(String.valueOf(scoreBoard.getScore()), x, 90);

        // HP
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("HP", x, 120);
        for (int i = 0; i < player.getHp(); i++) {
            g.setColor(new Color(220, 40, 60));
            g.fillOval(x + i * 22, 125, 18, 18);
        }

        // Level
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("LEVEL", x, 165);
        g.setColor(new Color(0, 220, 120));
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.drawString(String.valueOf(scoreBoard.getLevel()), x, 190);

        // Enemies alive
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("ENEMIES", x, 225);
        g.setColor(new Color(255, 80, 80));
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString(String.valueOf(enemies.size()), x, 248);

        // Controls
        g.setColor(new Color(0, 150, 200));
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString("CONTROLS", x, 290);
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String[] ctrl = {"↑↓←→ / WASD", " Move", "SPACE", " Shoot", "P", " Pause", "R", " Restart"};
        int cy = 308;
        for (int i = 0; i < ctrl.length; i += 2) {
            g.setColor(new Color(255, 200, 0));
            g.drawString(ctrl[i], x, cy);
            g.setColor(new Color(180, 180, 180));
            g.drawString(ctrl[i+1], x + 45, cy);
            cy += 16;
        }

        // BFS info
        g.setColor(new Color(0, 150, 200));
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("BFS AI", x, cy + 15);
        g.setColor(new Color(140, 140, 140));
        g.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g.drawString("Enemies use BFS", x, cy + 28);
        g.drawString("shortest path", x, cy + 40);
        g.drawString("O(V+E) per move", x, cy + 52);
    }

    private void drawOverlay(Graphics2D g, String title, String sub, Color bg) {
        g.setColor(bg);
        g.fillRoundRect(60, GAME_HEIGHT / 2 - 70, GAME_WIDTH - 120, 140, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, GAME_WIDTH / 2 - fm.stringWidth(title) / 2, GAME_HEIGHT / 2 - 10);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        g.drawString(sub, GAME_WIDTH / 2 - fm.stringWidth(sub) / 2, GAME_HEIGHT / 2 + 30);
        g.drawString("Final Score: " + scoreBoard.getScore(),
                GAME_WIDTH / 2 - fm.stringWidth("Final Score: " + scoreBoard.getScore()) / 2,
                GAME_HEIGHT / 2 + 52);
    }

    // ── Keyboard ──────────────────────────────────────────────────────
    @Override public void keyPressed(KeyEvent e) {
        keysDown.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver && !paused) {
            bullets.add(new Bullet(player.getCenterX() - 3, player.getY() - 10));
        }
        if (e.getKeyCode() == KeyEvent.VK_P && !gameOver) paused = !paused;
        if (e.getKeyCode() == KeyEvent.VK_R) initGame();
    }
    @Override public void keyReleased(KeyEvent e) { keysDown.remove(e.getKeyCode()); }
    @Override public void keyTyped(KeyEvent e) {}
}
