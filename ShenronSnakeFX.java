// ShenronSnakeFX.java
// Full Snake Game with Glow Effects, Gradient Balls, Background UI, and Start Menu

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ShenronSnakeFX {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shenron Snake 🐉");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 120;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean showMenu = true;
    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(10, 10, 30));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }

    public void startGame() {
        newApple();
        running = true;
        showMenu = false;
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        for (int i = 0; i < x.length; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showMenu) {
            drawStartMenu(g2d);
            return;
        }

        if (running) {
            // Draw gradient Dragon Ball
            GradientPaint ballPaint = new GradientPaint(appleX, appleY, Color.ORANGE, appleX + UNIT_SIZE, appleY + UNIT_SIZE, Color.YELLOW, true);
            g2d.setPaint(ballPaint);
            g2d.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            g2d.setColor(Color.RED);
            int starSize = UNIT_SIZE / 4;
            g2d.fillOval(appleX + UNIT_SIZE / 2 - starSize / 2, appleY + UNIT_SIZE / 2 - starSize / 2, starSize, starSize);

            // Draw Shenron-style Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g2d.setColor(new Color(0, 255, 0));
                } else {
                    g2d.setColor(new Color(255, 215, 0));
                }
                g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);

                // Glow effect
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
            }

            // Score
            g2d.setColor(Color.CYAN);
            g2d.setFont(new Font("Consolas", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g2d.getFont());
            g2d.drawString("Dragon Balls: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Dragon Balls: " + applesEaten)) / 2, 35);
        } else {
            drawGameOver(g2d);
        }
    }

    public void drawStartMenu(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Consolas", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g2d.getFont());
        g2d.drawString("Shenron Snake", (SCREEN_WIDTH - metrics.stringWidth("Shenron Snake")) / 2, SCREEN_HEIGHT / 2 - 50);

        g2d.setFont(new Font("Consolas", Font.PLAIN, 25));
        FontMetrics sub = getFontMetrics(g2d.getFont());
        g2d.drawString("Press ENTER to Begin", (SCREEN_WIDTH - sub.stringWidth("Press ENTER to Begin")) / 2, SCREEN_HEIGHT / 2 + 10);
    }

    public void drawGameOver(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Consolas", Font.BOLD, 60));
        FontMetrics metrics2 = getFontMetrics(g2d.getFont());
        g2d.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g2d.setFont(new Font("Consolas", Font.PLAIN, 30));
        FontMetrics metrics3 = getFontMetrics(g2d.getFont());
        g2d.drawString("Press any key to play again", (SCREEN_WIDTH - metrics3.stringWidth("Press any key to play again")) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (showMenu && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();
                return;
            }

            if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') direction = 'D';
                        break;
                }
            } else {
                startGame();
            }
        }
    }
}