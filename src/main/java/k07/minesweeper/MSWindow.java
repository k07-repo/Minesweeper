package k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Handles creation of the main window and of new games (includes some game logic).
 */
public class MSWindow extends JFrame {

    private static final int DEFAULT_ROWS = 16;
    private static final int DEFAULT_COLS = 16;
    private static final int DEFAULT_MINES = 40;
    private static final int DEFAULT_LIFELINES = 0;

    private static JPanel rootPanel;
    private static JToolBar toolbar;

    public static MSButton pressedButton;

    public static JFrame rootWindow;

    public static JLabel minesLeft = new JLabel();
    public static JLabel timeLabel = new JLabel();
    public static JLabel lifelinesLabel = new JLabel();

    public static boolean firstClick = false;

    public static int rows;
    public static int cols;
    public static int mines;
    public static int lifelines;
    public static boolean soundEnabled;
    public static boolean colorEnabled;

    public static Options options = new Options();
    public static Game game;

    public static AudioPlayer explosionPlayer;
    public static AudioPlayer tickPlayer;

    public MSWindow() {

        try {
            explosionPlayer = new AudioPlayer("explosion.wav");
            explosionPlayer.soften(30.0F);
            tickPlayer = new AudioPlayer("tick.wav");
        }
        catch(Exception e) {
            //issue loading sound files
            for(StackTraceElement element: e.getStackTrace())
            System.out.println(element.toString());
            System.exit(0); //temporary
        }

        game = new Game(this);

        if(options.loadFromFile()) {
            rows = options.rows;
            cols = options.columns;
            mines = options.mines;
            lifelines = options.lifelines;
            soundEnabled = options.soundEnabled;
            colorEnabled = options.colorEnabled;
        }
        else {
            rows = DEFAULT_ROWS;
            cols = DEFAULT_COLS;
            mines = DEFAULT_MINES;
            lifelines = DEFAULT_LIFELINES;
            soundEnabled = true;
            colorEnabled = true;

            options.rows = DEFAULT_ROWS;
            options.columns = DEFAULT_COLS;
            options.mines = DEFAULT_MINES;
            options.lifelines = DEFAULT_LIFELINES;
            options.soundEnabled = true;
            options.colorEnabled = true;
        }

        this.setTitle("Minesweeper");
        this.setLayout(new BorderLayout());
        this.setSize(500, 600);

        this.rootPanel = new JPanel();
        this.add(rootPanel, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w){
                try {
                    options.saveToFile();
                }
                catch(IOException e) {
                    JOptionPane.showMessageDialog(null, "Error saving options!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                System.exit(0);
            }
        });

        pressedButton = null;
        firstClick = true;

        addGridToWindow();
        setupToolbar();
    }

    public void setupToolbar() {
        this.toolbar = new JToolBar();
        this.toolbar.setLayout(new FlowLayout());
        updateToolbar();

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            game.newGame();
        });

        this.toolbar.add(newGameButton);
        this.toolbar.add(minesLeft);
        this.toolbar.add(timeLabel);
        this.toolbar.add(lifelinesLabel);

        JButton optionsButton = new JButton("Options");
        optionsButton.addActionListener(e -> {
            OptionsWindow optionsWindow = new OptionsWindow(this);
            optionsWindow.setVisible(true);
        });
        this.toolbar.add(optionsButton);
        this.add(toolbar, BorderLayout.NORTH);
    }

    public static void addGridToWindow() {
        rootPanel.removeAll();
        game.mineCount = mines;
        game.grid = new Grid(mines, rows, cols);
        game.grid.createGrid();
        game.grid.initializeGrid();
        rootPanel.setLayout(new GridLayout(rows, cols));

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                MSButton button = new MSButton("", row, col);
                Cell cell = game.grid.getCellAt(row, col);
                cell.setButton(button);
                button.setCell(cell);

                button.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {

                    }
                    public void mousePressed(MouseEvent e) {
                        pressedButton = button;
                    }
                    public void mouseReleased(MouseEvent e) {
                        if(game.gameState == Game.GameState.ONGOING) {
                            if (button == pressedButton) {
                                if (SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e)) {
                                    if (!button.isEnabled() && button.cell.getState() == Cell.State.REVEALED) {
                                        game.revealAllAdjacentWithFlagCheck(button);
                                    }
                                } else if (button.isEnabled()) {
                                    if (SwingUtilities.isLeftMouseButton(e)) {
                                        if (!(button.cell.getState() == Cell.State.FLAGGED)) {
                                            game.reveal(button);
                                        }
                                    } else if (SwingUtilities.isRightMouseButton(e)) {
                                        if (!(button.cell.getState() == Cell.State.REVEALED)) {
                                            game.rotateFlagState(button);
                                        }
                                    }
                                }
                            }
                        }

                        pressedButton = null;
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                        pressedButton = null;
                    }


                });
                rootPanel.add(button);
            }
        }

        //Redraw and refresh the grid after the new one is created
        rootPanel.repaint();
        rootPanel.revalidate();
    }

    public static void updateToolbar() {
        minesLeft.setText("Mines left: " + game.mineCount);
        timeLabel.setText("Time passed: " + game.timePassed);
        lifelinesLabel.setText("Lifelines left: " + lifelines);
        toolbar.repaint();
        toolbar.revalidate();
    }
}
