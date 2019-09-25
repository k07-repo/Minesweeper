package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static final int DEFAULT_ROWS = 16;
    private static final int DEFAULT_COLS = 16;
    private static final int DEFAULT_MINES = 40;

    public static MSButton pressedButton;

    public static JFrame rootWindow;
    public static JPanel rootPanel;
    public static JToolBar toolbar;
    public static JFrame optionSetWindow;

    public static JButton newGameButton;
    public static JButton optionsButton;

    public static JLabel minesLeft = new JLabel();
    public static JLabel timeLabel = new JLabel();

    public static JTextField rowField = new JTextField();
    public static JTextField colField = new JTextField();
    public static JTextField mineField = new JTextField();

    public static boolean firstClick = false;

    public static int rows = -1;
    public static int cols = -1;
    public static int mines = -1;

    public static Options options = new Options();

    public static Game game;

    public static void main(String[] args) throws Exception {

        game = new Game();
        pressedButton = null;
        firstClick = true;

        if(options.loadFromFile()) {
            rows = options.rows;
            cols = options.columns;
            mines = options.mines;
        }
        else {
            rows = DEFAULT_ROWS;
            cols = DEFAULT_COLS;
            mines = DEFAULT_MINES;
        }

        setupWindow();
    }

    public static boolean validateInput(JTextField field, String fieldName, int min, int max) {
        String text = field.getText();
        int value = -1;

        try {
            value = Integer.parseInt(text);
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, fieldName + ": numeric values only!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(value < min) {
            JOptionPane.showMessageDialog(null, fieldName + ": value too low! Minimum: " + min, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(value > max) {
            JOptionPane.showMessageDialog(null, fieldName + ": value too high! Maximum: " + max, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void setupOptionsWindow() {

        optionSetWindow = new JFrame();
        optionSetWindow.setLayout(new GridLayout(5, 1));

        JPanel sub1 = new JPanel();
        JPanel sub2 = new JPanel();
        JPanel sub3 = new JPanel();

        JButton playButton = new JButton("Play");
        optionSetWindow.add(new JLabel("Set Options"));

        sub1.setLayout(new GridLayout(1, 2));
        sub1.add(new JLabel("Rows: "));
        sub1.add(rowField);
        optionSetWindow.add(sub1);

        sub2.setLayout(new GridLayout(1, 2));
        sub2.add(new JLabel("Columns: "));
        sub2.add(colField);
        optionSetWindow.add(sub2);

        sub3.setLayout(new GridLayout(1, 2));
        sub3.add(new JLabel("Mines: "));
        sub3.add(mineField);
        optionSetWindow.add(sub3);

        playButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!validateInput(rowField, "Rows", 5, 20)) {
                    return;
                } else if (!validateInput(colField, "Rows", 5, 30)) {
                    return;
                }

                int rowsFromField = Integer.parseInt(rowField.getText());
                int colsFromField =  Integer.parseInt(colField.getText());

                if (!validateInput(mineField, "Mines", 1, (rowsFromField * colsFromField) - 1)) {
                    return;
                }

                int minesFromField =  Integer.parseInt(mineField.getText());

                rows = rowsFromField;
                cols = colsFromField;
                mines = minesFromField;

                options.rows = rows;
                options.columns = cols;
                options.mines = mines;

                game.newGame();
                optionSetWindow.dispatchEvent(new WindowEvent(optionSetWindow, WindowEvent.WINDOW_CLOSING));
            }
        });

        optionSetWindow.add(playButton);
        optionSetWindow.setSize(125, 175);
        optionSetWindow.setResizable(false);
        optionSetWindow.setLocationRelativeTo(null);
        optionSetWindow.setVisible(true);
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

    public static void setupWindow() {
        rootWindow = new JFrame("Minesweeper");
        rootWindow.setLayout(new BorderLayout());
        rootWindow.setSize(500, 600);
        rootPanel = new JPanel();
        rootWindow.add(rootPanel, BorderLayout.CENTER);

        rootWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w){
                try {
                    options.saveToFile();
                    System.out.println("Save success!");
                }
                catch(IOException e) {
                    //rip
                }

                System.exit(0);
            }
        });

        addGridToWindow();
        setupToolbar();

        rootWindow.setVisible(true);
    }

    public static void setupToolbar() {
        toolbar = new JToolBar();

        toolbar.setLayout(new FlowLayout());
        updateToolbar();
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.newGame();
            }
        });
        toolbar.add(newGameButton);
        toolbar.add(minesLeft);
        toolbar.add(timeLabel);

        optionsButton = new JButton("Options");
        optionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupOptionsWindow();
            }
        });
        toolbar.add(optionsButton);
        rootWindow.add(toolbar, BorderLayout.NORTH);
    }

    public static void updateToolbar() {
        minesLeft.setText("Mines left: " + game.mineCount);
        timeLabel.setText("Time passed: " + game.timePassed);
        toolbar.repaint();
        toolbar.revalidate();
    }


}
