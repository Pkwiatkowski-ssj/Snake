// ShenronSnakeFX.java – Phase 1 Upgrade
// Includes: Pulsing Dragon Ball, Shimmering Snake, Animated Orbs Background

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
    static int delay = 120;

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
    long frameCount = 0;
    ArrayList<Orb> orbs = new ArrayList<>();

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        generateBackgroundOrbs();
    }

    public void generateBackgroundOrbs() {
        for (int i = 0; i < 20; i++) {
            orbs.add(new Orb(
                random.nextInt(SCREEN_WIDTH),
                random.nextInt(SCREEN_HEIGHT),
                2 + random.nextInt(5),
                1 + random.nextFloat()
            ));
        }
    }

    public void startGame() {
        newApple();
        running = true;
        showMenu = false;
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        delay = 120;
        for (int i = 0; i < x.length; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background
        GradientPaint background = new GradientPaint(0, 0, new Color(0, 0, 139), 0, SCREEN_HEIGHT, new Color(173, 216, 230), false);
        g2d.setPaint(background);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Animate background orbs
        for (Orb orb : orbs) {
            orb.update();
            orb.draw(g2d);
        }

        if (showMenu) {
            drawStartMenu(g2d);
            return;
        }

        if (running) {
            // Animate Dragon Ball pulsing
            double scale = 1 + 0.1 * Math.sin(frameCount * 0.1);
            int ballSize = (int)(UNIT_SIZE * scale);
            int offset = (ballSize - UNIT_SIZE) / 2;
            GradientPaint ballPaint = new GradientPaint(appleX, appleY, Color.ORANGE, appleX + ballSize, appleY + ballSize, Color.YELLOW, true);
            g2d.setPaint(ballPaint);
            g2d.fillOval(appleX - offset, appleY - offset, ballSize, ballSize);

            g2d.setColor(Color.RED);
            int starSize = ballSize / 4;
            g2d.fillOval(appleX + UNIT_SIZE / 2 - starSize / 2, appleY + UNIT_SIZE / 2 - starSize / 2, starSize, starSize);

            // Enhanced Snake
            for (int i = 0; i < bodyParts; i++) {
                Color bodyColor = i == 0 ? new Color(0, 255, 0) : new Color(200, 180 + (i * 5 % 70), 0);
                g2d.setColor(bodyColor);
                g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 12, 12);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 12, 12);
            }

            // Score
            g2d.setColor(Color.CYAN);
            g2d.setFont(new Font("Consolas", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g2d.getFont());
            g2d.drawString("Dragon Balls: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Dragon Balls: " + applesEaten)) / 2, 35);
        } else {
            drawGameOver(g2d);
        }

        frameCount++;
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
            delay = Math.max(40, delay - 5);
            timer.setDelay(delay);
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

class Orb {
    int x, y, size;
    float speed;

    public Orb(int x, int y, int size, float speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
    }

    public void update() {
        y += speed;
        if (y > GamePanel.SCREEN_HEIGHT) {
            y = 0;
            x = new Random().nextInt(GamePanel.SCREEN_WIDTH);
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillOval(x, y, size, size);
    }
}
