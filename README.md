# AI Shooter Challenge 🚀
### Lecture 8 — Java Programming Challenge

> **Course:** Java程式設計(一) — Java Programming (I)  

---

## 🎮 What is this?

A **2D space shooter game** built in Java using:
- **Java Swing** — game window and real-time rendering (no external libraries)
- **BFS (Breadth-First Search)** — enemy AI pathfinding around obstacles
- **Object-Oriented Design** — clean class separation per lecture requirements

**The enemies are NOT dumb straight-line movers.** They use BFS on a grid map to find the shortest path to the player, navigating intelligently around asteroid obstacles.

---

## 🕹️ Controls

| Key | Action |
|---|---|
| `↑ ↓ ← →` or `W A S D` | Move player |
| `SPACE` | Shoot bullet |
| `P` | Pause / Resume |
| `R` | Restart game |

---

## 📐 Scoring & Rules

| Event | Points |
|---|---|
| Destroy an enemy | **+100** |
| Survive 5 seconds | **+50 bonus** |
| Enemy hits you | **−1 HP** |
| HP reaches 0 | **GAME OVER** |

---

## 🧠 BFS Pathfinding — How It Works

The enemy AI runs **Breadth-First Search** every time it needs to move.

```
1. Treat the game map as a COLS × ROWS grid
2. Start from the enemy's current grid cell
3. Use a Queue to visit neighbours (Up, Down, Left, Right)
   → Skip obstacle cells and already-visited cells
4. Stop when the player's cell is reached
5. Reconstruct the shortest path via parent pointers
6. Move ONE step along that path
```

**Why BFS?**
- Guarantees the **shortest path** in an unweighted grid
- Time complexity: **O(V + E) = O(COLS × ROWS)** per move
- Automatically avoids asteroid obstacles — enemies go around them!

```java
// Core BFS loop (BFSPathfinder.java)
while (!queue.isEmpty()) {
    int[] cur = queue.poll();
    for (int d = 0; d < 4; d++) {
        int nx = cur[0] + DX[d];
        int ny = cur[1] + DY[d];
        if (!map.isObstacle(nx, ny) && !visited[nx][ny]) {
            visited[nx][ny] = true;
            parent[nx][ny]  = cur;
            queue.add(new int[]{nx, ny});
            if (nx == playerCol && ny == playerRow) break; // found!
        }
    }
}
```

---

## 🏗️ Class Structure (OOP Design)

```
AIShooter/
├── GameFrame.java          # JFrame — main window entry point
├── GamePanel.java          # JPanel — game loop (60 FPS Timer), input, rendering
├── Player.java             # Player airplane — movement, HP, drawing
├── Enemy.java              # Enemy airplane — position, drawing
├── Bullet.java             # Bullet projectile — movement, drawing
├── GameMap.java            # Grid map — obstacle generation and rendering
├── BFSPathfinder.java      # BFS algorithm — enemy AI shortest path
├── CollisionManager.java   # AABB collision detection (bullet×enemy, enemy×player)
└── ScoreBoard.java         # Score, level, HP tracking
```

| Class | Responsibility |
|---|---|
| `GameFrame` | Creates JFrame window, starts game |
| `GamePanel` | 60 FPS `Timer`, keyboard input, calls update + draw |
| `Player` | Arrow/WASD movement, boundary clamping, draws jet |
| `Enemy` | Stores position, drawn as enemy jet, moved by BFS result |
| `Bullet` | Moves upward each tick, drawn as glowing beam |
| `GameMap` | `boolean[col][row]` obstacle grid, draws asteroids |
| `BFSPathfinder` | Static `nextStep()` — full BFS, returns first move |
| `CollisionManager` | `Rectangle.intersects()` AABB checks |
| `ScoreBoard` | Score, level counters |

---

## ▶️ How to Run

### Requirements
- **Java JDK 17+** → [Download](https://www.oracle.com/java/technologies/downloads/)
- **VS Code** (recommended) with the Java Extension Pack

### Compile & Run (Terminal)

```bash
# Clone the repository
git clone https://github.com/Shirodot/BFS_AIShooting
cd BFS_AIShooting

# Compile all Java files
javac *.java

# Run
java GameFrame
```

### Run in VS Code

1. Open the folder in VS Code
2. Open `GameFrame.java`
3. Click the **▶ Run** button (top right) or press `F5`

---

## 📊 Evaluation Criteria Coverage

| Criterion | Weight | Implementation |
|---|---|---|
| Game Functionality | 20% | Move, shoot, enemies, collision, game over, restart |
| BFS Implementation | 25% | `BFSPathfinder.java` — full BFS with obstacle avoidance |
| OOP Design | 20% | 9 classes, each with single responsibility |
| Code Quality | 15% | Javadoc comments, clean separation, no magic numbers |
| Game Experience (UI/UX) | 20% | Star background, glow effects, HUD with score/HP/level |

---


