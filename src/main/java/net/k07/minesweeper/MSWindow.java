package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MSWindow extends JFrame {

    private static final int DEFAULT_ROWS = 16;
    private static final int DEFAULT_COLS = 16;
    private static final int DEFAULT_MINES = 40;

    private static JPanel rootPanel;
    private static JToolBar toolbar;

    public static MSButton pressedButton;

    public static JFrame rootWindow;

    public static JLabel minesLeft = new JLabel();
    public static JLabel timeLabel = new JLabel();

    public static boolean firstClick = false;

    public static int rows;
    public static int cols;
    public static int mines;

    public static Options options = new Options();
    public static Game game;

    public MSWindow() {

        game = new Game(this);

        if(options.loadFromFile()) {
            rows = options.rows;
            cols = options.columns;
            mines = options.mines;
        }
        else {
            rows = DEFAULT_ROWS;
            cols = DEFAULT_COLS;
            mines = DEFAULT_MINES;

            options.rows = DEFAULT_ROWS;
            options.columns = DEFAULT_COLS;
            options.mines = DEFAULT_MINES;
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
        toolbar.repaint();
        toolbar.revalidate();
    }
}
