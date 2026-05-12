import javax.swing.*;

/**
 * AI Shooter Challenge — Lecture 8
 * GameFrame.java — Main window entry point
 *
 * Authors: 翁祺展 (Chi-Zhan Weng) & 黃祥睿 (Xiang-Rui Huang)
 * National Penghu University of Science and Technology
 * Department of Computer Science & Information Engineering
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("AI Shooter Challenge — BFS Enemy AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();

        setLocationRelativeTo(null); // centre on screen
        setVisible(true);

        panel.startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}
