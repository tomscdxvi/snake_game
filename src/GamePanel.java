import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener
{

    static final int SCREEN_WIDTH  = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE     = 25;
    static final int GAME_UNITS    = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY_TIMER   = 75;
    static boolean   gameOn        = true;
    final  int       x[]           = new int[GAME_UNITS];
    final  int       y[]           = new int[GAME_UNITS];
    int              bodyParts     = 6;
    int              fruitEaten;
    int              fruitX;
    int              fruitY;
    char             direction     = 'R'; // Beginning direction of Snake (R = Right, L = Left, U = Up, and D = Down)
    boolean          running       = false;
    Timer            timer;
    Random           random;
    GamePanel game;
    JButton          restartButton;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        start();
    }

    public void start() {
        newFruit();
        running = true;
        timer   = new Timer(DELAY_TIMER, this);
        timer.start();
    }

    public void pause() {
        GamePanel.gameOn = true;
        timer.stop();
    }

    public void resume() {
        GamePanel.gameOn = false;
        timer.start();
    }

    public void restart() {
        setVisible(false);

        new GameFrame();
    }

    public void closeCurrentGameWindow(KeyEvent e) {
        JComponent comp = (JComponent) e.getSource();

        Window window = SwingUtilities.getWindowAncestor(comp);

        window.dispose();
    }

    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if(running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // FRUITS
            g.setColor(Color.white);
            g.fillRect(fruitX, fruitY, UNIT_SIZE, UNIT_SIZE);

            // HEAD OF SNAKE

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    // Sets snake to random colours
                    // g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                }
            }

            g.setColor(Color.white);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 35));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + fruitEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + fruitEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newFruit() {
        fruitX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        fruitY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
        }
    }

    public void checkFruit() {
        if((x[0] == fruitX) && (y[0] == fruitY))
        {
            bodyParts++;
            fruitEaten++;
            newFruit();
        }
    }

    public void checkCollisions() {
        // Checks if head collides with body, if so then end game.
        for(int i = bodyParts; i > 0; i--)
        {
            if((x[0] == x[i]) && (y[0] == y[i]))
            {
                running = false;
            }
        }

        // Checks if head touches left border, if so then end game.
        if(x[0] < 0) {
            running = false;
        }

        // // Checks if head touches right border, if so then end game.
        if(x[0] > SCREEN_WIDTH)
        {
            running = false;
        }

        // Checks if head touches top border, if so then end game.
        if(y[0] < 0)
        {
            running = false;
        }

        // Checks if head touches bottom border, if so then end game.
        if(y[0] > SCREEN_HEIGHT)
        {
            running = false;
        }

        if(!running)
        {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Score Check
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 35));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + fruitEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + fruitEaten)) / 2, g.getFont().getSize());

        // Game Over Message
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Restart Message
        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press R to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press R to Restart")) / 2, SCREEN_HEIGHT / 3);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running)
        {
            move();
            checkFruit();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction  != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if(direction  != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if(direction  != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if(direction  != 'U') {
                        direction = 'D';
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    if(GamePanel.gameOn) {
                        resume();
                    }
                    else {
                        pause();
                    }
                    break;

                case KeyEvent.VK_R:
                    if(!running) {
                        try {
                            closeCurrentGameWindow(e);
                            restart();

                            System.out.println("Restart");
                        } catch(NullPointerException npe) {
                            System.out.println("NullPointerException Thrown.");
                        }
                    }
                    break;
            }
        }
    }
}
