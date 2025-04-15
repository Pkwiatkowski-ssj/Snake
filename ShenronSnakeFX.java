// ShenronSnakeFX.java – Final Version with Hardcore Mode (No Shenron Wishes)
// Hardcore mode: Super fast + no wishes

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ShenronSnakeFX extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    int delay = 120;

    javax.swing.Timer timer;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX, appleY;
    char direction = 'R';
    boolean running = false;
    boolean invincible = false;
    long invincibilityEndTime = 0;
    boolean showMenu = true;
    boolean showShenron = false;
    boolean wishPending = false;
    boolean zenMode = false;
    boolean hardcoreMode = false;

    Random random = new Random();
    List<Particle> particles = new ArrayList<>();
    List<Orb> orbs = new ArrayList<>();

    public ShenronSnakeFX() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        generateOrbs(10);
        timer = new javax.swing.Timer(delay, this);
        timer.start();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        Arrays.fill(x, 0);
        Arrays.fill(y, 0);
        newApple();
        running = true;
        showShenron = false;
        wishPending = false;
        timer.setDelay(delay);
        timer.start();
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
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
        if (zenMode) wrapAround();
    }

    public void wrapAround() {
        if (x[0] < 0) x[0] = SCREEN_WIDTH - UNIT_SIZE;
        if (x[0] >= SCREEN_WIDTH) x[0] = 0;
        if (y[0] < 0) y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        if (y[0] >= SCREEN_HEIGHT) y[0] = 0;
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
            spawnParticles();
            if (!hardcoreMode && applesEaten % 7 == 0 && applesEaten != 0) {
                showShenron = true;
                running = false;
                wishPending = true;
            } else {
                if (delay > 50) delay -= 5;
                timer.setDelay(delay);
            }
        }
    }

    public void spawnParticles() {
        for (int i = 0; i < 10; i++) {
            particles.add(new Particle(appleX + UNIT_SIZE / 2, appleY + UNIT_SIZE / 2));
        }
    }

    public void checkCollisions() {
        if (!invincible) {
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i]) running = false;
            }
            if (!zenMode && (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT)) running = false;
        }
        if (!running && !showShenron) timer.stop();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint bg = new GradientPaint(0, 0, new Color(72, 61, 139), 0, SCREEN_HEIGHT, new Color(255, 140, 0), false);
        g2d.setPaint(bg);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        for (Orb orb : orbs) orb.draw(g2d);
        updateOrbs();

        if (showMenu) { drawMenu(g2d); return; }
        if (showShenron && wishPending) { drawWishMenu(g2d); return; }
        drawGame(g2d);
    }

    public void drawMenu(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Ink Free", Font.BOLD, 40));
        drawCentered(g2d, "SHENRON SNAKE FX", SCREEN_HEIGHT / 2 - 60);
        g2d.setFont(new Font("Ink Free", Font.PLAIN, 24));
        drawCentered(g2d, "Press 1 - Classic", SCREEN_HEIGHT / 2);
        drawCentered(g2d, "Press 2 - Zen (no walls)", SCREEN_HEIGHT / 2 + 30);
        drawCentered(g2d, "Press 3 - Hardcore", SCREEN_HEIGHT / 2 + 60);
    }

    public void drawWishMenu(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Ink Free", Font.BOLD, 32));
        drawCentered(g2d, "SHENRON HAS APPEARED!", 180);
        g2d.setFont(new Font("Ink Free", Font.PLAIN, 24));
        drawCentered(g2d, "1 - Slow Time", 240);
        drawCentered(g2d, "2 - Grow +5", 270);
        drawCentered(g2d, "3 - Invincibility (10s)", 300);
    }

    public void drawGame(Graphics2D g2d) {
        double pulse = 1 + Math.sin(System.currentTimeMillis() / 200.0) * 0.1;
        int ballSize = (int) (UNIT_SIZE * pulse);
        int offset = (UNIT_SIZE - ballSize) / 2;
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(appleX + offset, appleY + offset, ballSize, ballSize);
        g2d.setColor(Color.RED);
        g2d.fillOval(appleX + UNIT_SIZE / 2 - 4, appleY + UNIT_SIZE / 2 - 4, 8, 8);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g2d.setColor(Color.GREEN);
            } else {
                float hue = (i * 0.05f) % 1.0f;
                g2d.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
            }
            g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
        }

        if (invincible) {
            long timeLeft = (invincibilityEndTime - System.currentTimeMillis()) / 1000;
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Ink Free", Font.BOLD, 20));
            g2d.drawString("Invincible: " + timeLeft + "s", 10, 20);
        }

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Ink Free", Font.BOLD, 28));
        g2d.drawString("Dragon Balls: " + applesEaten, 10, SCREEN_HEIGHT - 10);

        for (Particle p : particles) p.draw(g2d);
    }

    public void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (SCREEN_WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    public void updateOrbs() {
        for (Orb orb : orbs) orb.update();
    }

    public void generateOrbs(int count) {
        for (int i = 0; i < count; i++) orbs.add(new Orb());
    }

    public void updateParticles() {
        particles.removeIf(p -> !p.update());
    }

    public void applyWish(int wish) {
        switch (wish) {
            case 1:
                delay = 300;
                timer.setDelay(delay);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override public void run() {
                        for (int i = 0; i <= 10; i++) {
                            int newDelay = 300 - (i * 20);
                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                @Override public void run() {
                                    delay = newDelay;
                                    timer.setDelay(delay);
                                }
                            }, i * 500);
                        }
                    }
                }, 5000);
                break;
            case 2: bodyParts += 5; break;
            case 3:
                invincible = true;
                invincibilityEndTime = System.currentTimeMillis() + 10000;
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override public void run() {
                        invincible = false;
                    }
                }, 10000);
                break;
        }
        showShenron = false;
        wishPending = false;
        running = true;
        timer.start();
    }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (running) {
            move(); checkApple(); checkCollisions(); updateParticles();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override public void keyPressed(KeyEvent e) {
            if (showMenu) {
                if (e.getKeyCode() == KeyEvent.VK_1) { delay = 120; zenMode = false; hardcoreMode = false; showMenu = false; startGame(); }
                else if (e.getKeyCode() == KeyEvent.VK_2) { delay = 120; zenMode = true; hardcoreMode = false; showMenu = false; startGame(); }
                else if (e.getKeyCode() == KeyEvent.VK_3) { delay = 40; zenMode = false; hardcoreMode = true; showMenu = false; startGame(); }
            } else if (wishPending && showShenron && !hardcoreMode) {
                if (e.getKeyCode() == KeyEvent.VK_1) applyWish(1);
                else if (e.getKeyCode() == KeyEvent.VK_2) applyWish(2);
                else if (e.getKeyCode() == KeyEvent.VK_3) applyWish(3);
            } else if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                    case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                    case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                    case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
                }
            } else {
                showMenu = true;
                running = false;
                repaint();
            }
        }
    }

    public class Particle {
        int x, y, dx, dy, life = 20;
        public Particle(int x, int y) {
            this.x = x; this.y = y;
            dx = random.nextInt(5) - 2;
            dy = random.nextInt(5) - 2;
        }
        boolean update() {
            x += dx; y += dy; return --life > 0;
        }
        void draw(Graphics2D g) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, 4, 4);
        }
    }

    public class Orb {
        int x = random.nextInt(SCREEN_WIDTH);
        int y = random.nextInt(SCREEN_HEIGHT);
        int speed = 1 + random.nextInt(2);
        int size = 8 + random.nextInt(10);
        Color color = new Color(255, 255, 255, 80);
        void update() {
            y -= speed;
            if (y < 0) {
                y = SCREEN_HEIGHT;
                x = random.nextInt(SCREEN_WIDTH);
            }
        }
        void draw(Graphics2D g) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shenron Snake FX");
        ShenronSnakeFX gamePanel = new ShenronSnakeFX();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
