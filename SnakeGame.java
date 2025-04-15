import javax.swing.JFrame;

public class SnakeGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();  // Sizes the frame to fit the preferred size of the panel
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }
}
