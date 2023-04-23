import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Box;

public class Game implements Runnable {
    public void run() {
        SwingUtilities.invokeLater(new StartMenu());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}

class GameWindow {
    private JFrame gameWindow;

    public Clock blackClock;
    public Clock whiteClock;

    private Timer timer;

    private Board board;

    public GameWindow(String blackName, String whiteName, int hh,
            int mm, int ss) {

        blackClock = new Clock(hh, ss, mm);
        whiteClock = new Clock(hh, ss, mm);

        gameWindow = new JFrame("Chess");

        try {
            Image whiteImg = ImageIO.read(getClass().getResource("wp.png"));
            gameWindow.setIconImage(whiteImg);
        } catch (Exception e) {
            System.out.println("Game file wp.png not found");
        }

        gameWindow.setLocation(100, 100);

        gameWindow.setLayout(new BorderLayout(20, 20));

        // Game Data window
        JPanel gameData = gameDataPanel(blackName, whiteName, hh, mm, ss);
        gameData.setSize(gameData.getPreferredSize());
        gameWindow.add(gameData, BorderLayout.NORTH);

        this.board = new Board(this);

        gameWindow.add(board, BorderLayout.CENTER);

        gameWindow.add(buttons(), BorderLayout.SOUTH);

        gameWindow.setMinimumSize(gameWindow.getPreferredSize());
        gameWindow.setSize(gameWindow.getPreferredSize());
        gameWindow.setResizable(false);

        gameWindow.pack();
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Helper function to create data panel

    private JPanel gameDataPanel(final String bn, final String wn,
            final int hh, final int mm, final int ss) {

        JPanel gameData = new JPanel();
        gameData.setLayout(new GridLayout(3, 2, 0, 0));

        // PLAYER NAMES

        JLabel w = new JLabel(wn);
        JLabel b = new JLabel(bn);

        w.setHorizontalAlignment(JLabel.CENTER);
        w.setVerticalAlignment(JLabel.CENTER);
        b.setHorizontalAlignment(JLabel.CENTER);
        b.setVerticalAlignment(JLabel.CENTER);

        w.setSize(w.getMinimumSize());
        b.setSize(b.getMinimumSize());

        gameData.add(w);
        gameData.add(b);

        // CLOCKS

        final JLabel bTime = new JLabel(blackClock.getTime());
        final JLabel wTime = new JLabel(whiteClock.getTime());

        bTime.setHorizontalAlignment(JLabel.CENTER);
        bTime.setVerticalAlignment(JLabel.CENTER);
        wTime.setHorizontalAlignment(JLabel.CENTER);
        wTime.setVerticalAlignment(JLabel.CENTER);

        if (!(hh == 0 && mm == 0 && ss == 0)) {
            timer = new Timer(1000, null);
            timer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean turn = board.getTurn();

                    if (turn) {
                        whiteClock.decr();
                        wTime.setText(whiteClock.getTime());

                        if (whiteClock.outOfTime()) {
                            timer.stop();
                            int n = JOptionPane.showConfirmDialog(
                                    gameWindow,
                                    bn + " wins by time! Play a new game? \n" +
                                            "Choosing \"No\" quits the game.",
                                    bn + " wins!",
                                    JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                new GameWindow(bn, wn, hh, mm, ss);
                                gameWindow.dispose();
                            } else
                                gameWindow.dispose();
                        }
                    } else {
                        blackClock.decr();
                        bTime.setText(blackClock.getTime());

                        if (blackClock.outOfTime()) {
                            timer.stop();
                            int n = JOptionPane.showConfirmDialog(
                                    gameWindow,
                                    wn + " wins by time! Play a new game? \n" +
                                            "Choosing \"No\" quits the game.",
                                    wn + " wins!",
                                    JOptionPane.YES_NO_OPTION);

                            if (n == JOptionPane.YES_OPTION) {
                                new GameWindow(bn, wn, hh, mm, ss);
                                gameWindow.dispose();
                            } else
                                gameWindow.dispose();
                        }
                    }
                }
            });
            timer.start();
        } else {
            wTime.setText("Untimed game");
            bTime.setText("Untimed game");
        }

        gameData.add(wTime);
        gameData.add(bTime);

        gameData.setPreferredSize(gameData.getMinimumSize());

        return gameData;
    }

    private JPanel buttons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3, 10, 0));

        final JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to quit?",
                        "Confirm quit", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    if (timer != null)
                        timer.stop();
                    gameWindow.dispose();
                }
            }
        });

        final JButton nGame = new JButton("New game");

        nGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to begin a new game?",
                        "Confirm new game", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(new StartMenu());
                    gameWindow.dispose();
                }
            }
        });

        final JButton instr = new JButton("How to play");

        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces on the board by clicking\n"
                                + "and dragging. The game will watch out for illegal\n"
                                + "moves. You can win either by your opponent running\n"
                                + "out of time or by checkmating your opponent.\n"
                                + "\nGood luck, hope you enjoy the game!",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);

        buttons.setPreferredSize(buttons.getMinimumSize());

        return buttons;
    }

    public void checkmateOccurred(int c) {
        if (c == 0) {
            if (timer != null)
                timer.stop();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "White wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "White wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        } else {
            if (timer != null)
                timer.stop();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "Black wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "Black wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        }
    }
}

class StartMenu implements Runnable {
    public void run() {
        final JFrame startWindow = new JFrame("Chess");
        // Set window properties
        startWindow.setLocation(300, 100);
        startWindow.setResizable(false);
        startWindow.setSize(260, 240);

        Box components = Box.createVerticalBox();
        startWindow.add(components);

        // Game title
        final JPanel titlePanel = new JPanel();
        components.add(titlePanel);
        final JLabel titleLabel = new JLabel("Chess");
        titlePanel.add(titleLabel);

        // Black player selections
        final JPanel blackPanel = new JPanel();
        components.add(blackPanel, BorderLayout.EAST);
        final JLabel blackPiece = new JLabel();
        try {
            Image blackImg = ImageIO.read(getClass().getResource("bp.png"));
            blackPiece.setIcon(new ImageIcon(blackImg));
            blackPanel.add(blackPiece);
        } catch (Exception e) {
            System.out.println("Required game file bp.png missing");
        }

        final JTextField blackInput = new JTextField("Black", 10);
        blackPanel.add(blackInput);

        // White player selections
        final JPanel whitePanel = new JPanel();
        components.add(whitePanel);
        final JLabel whitePiece = new JLabel();

        try {
            Image whiteImg = ImageIO.read(getClass().getResource("wp.png"));
            whitePiece.setIcon(new ImageIcon(whiteImg));
            whitePanel.add(whitePiece);
            startWindow.setIconImage(whiteImg);
        } catch (Exception e) {
            System.out.println("Required game file wp.png missing");
        }

        final JTextField whiteInput = new JTextField("White", 10);
        whitePanel.add(whiteInput);

        // Timer settings
        final String[] minSecInts = new String[60];
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minSecInts[i] = "0" + Integer.toString(i);
            } else {
                minSecInts[i] = Integer.toString(i);
            }
        }

        final JComboBox<String> seconds = new JComboBox<String>(minSecInts);
        final JComboBox<String> minutes = new JComboBox<String>(minSecInts);
        final JComboBox<String> hours = new JComboBox<String>(new String[] { "0", "1", "2", "3" });

        Box timerSettings = Box.createHorizontalBox();

        hours.setMaximumSize(hours.getPreferredSize());
        minutes.setMaximumSize(minutes.getPreferredSize());
        seconds.setMaximumSize(minutes.getPreferredSize());

        timerSettings.add(hours);
        timerSettings.add(Box.createHorizontalStrut(10));
        timerSettings.add(seconds);
        timerSettings.add(Box.createHorizontalStrut(10));
        timerSettings.add(minutes);

        timerSettings.add(Box.createVerticalGlue());

        components.add(timerSettings);

        // Buttons
        Box buttons = Box.createHorizontalBox();
        final JButton quit = new JButton("Quit");

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startWindow.dispose();
            }
        });

        final JButton instr = new JButton("Instructions");

        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(startWindow,
                        "To begin a new game, input player names\n" +
                                "next to the pieces. Set the clocks and\n" +
                                "click \"Start\". Setting the timer to all\n" +
                                "zeroes begins a new untimed game.",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        final JButton start = new JButton("Start");

        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bn = blackInput.getText();
                String wn = whiteInput.getText();
                int hh = Integer.parseInt((String) hours.getSelectedItem());
                int mm = Integer.parseInt((String) minutes.getSelectedItem());
                int ss = Integer.parseInt((String) seconds.getSelectedItem());

                new GameWindow(bn, wn, hh, mm, ss);
                startWindow.dispose();
            }
        });

        buttons.add(start);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(instr);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(quit);
        components.add(buttons);

        Component space = Box.createGlue();
        components.add(space);

        startWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startWindow.setVisible(true);
    }
}

@SuppressWarnings("serial")
class Board extends JPanel implements MouseListener, MouseMotionListener {
    // Resource location constants for piece images
    private static final String RESOURCES_WBISHOP_PNG = "wbishop.png";
    private static final String RESOURCES_BBISHOP_PNG = "bbishop.png";
    private static final String RESOURCES_WKNIGHT_PNG = "wknight.png";
    private static final String RESOURCES_BKNIGHT_PNG = "bknight.png";
    private static final String RESOURCES_WROOK_PNG = "wrook.png";
    private static final String RESOURCES_BROOK_PNG = "brook.png";
    private static final String RESOURCES_WKING_PNG = "wking.png";
    private static final String RESOURCES_BKING_PNG = "bking.png";
    private static final String RESOURCES_BQUEEN_PNG = "bqueen.png";
    private static final String RESOURCES_WQUEEN_PNG = "wqueen.png";
    private static final String RESOURCES_WPAWN_PNG = "wpawn.png";
    private static final String RESOURCES_BPAWN_PNG = "bpawn.png";

    // Logical and graphical representations of board
    private final Square[][] board;
    private final GameWindow g;

    // List of pieces and whether they are movable
    public final LinkedList<Piece> Bpieces;
    public final LinkedList<Piece> Wpieces;
    public List<Square> movable;

    private boolean whiteTurn;

    private Piece currPiece;
    private int currX;
    private int currY;

    private CheckmateDetector cmd;

    public Board(GameWindow g) {
        this.g = g;
        board = new Square[8][8];
        Bpieces = new LinkedList<Piece>();
        Wpieces = new LinkedList<Piece>();
        setLayout(new GridLayout(8, 8, 0, 0));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;

                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    board[x][y] = new Square(this, 1, y, x);
                    this.add(board[x][y]);
                } else {
                    board[x][y] = new Square(this, 0, y, x);
                    this.add(board[x][y]);
                }
            }
        }

        initializePieces();

        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));

        whiteTurn = true;

    }

    private void initializePieces() {

        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], RESOURCES_BPAWN_PNG));
            board[6][x].put(new Pawn(1, board[6][x], RESOURCES_WPAWN_PNG));
        }

        board[7][3].put(new Queen(1, board[7][3], RESOURCES_WQUEEN_PNG));
        board[0][3].put(new Queen(0, board[0][3], RESOURCES_BQUEEN_PNG));

        King bk = new King(0, board[0][4], RESOURCES_BKING_PNG);
        King wk = new King(1, board[7][4], RESOURCES_WKING_PNG);
        board[0][4].put(bk);
        board[7][4].put(wk);

        board[0][0].put(new Rook(0, board[0][0], RESOURCES_BROOK_PNG));
        board[0][7].put(new Rook(0, board[0][7], RESOURCES_BROOK_PNG));
        board[7][0].put(new Rook(1, board[7][0], RESOURCES_WROOK_PNG));
        board[7][7].put(new Rook(1, board[7][7], RESOURCES_WROOK_PNG));

        board[0][1].put(new Knight(0, board[0][1], RESOURCES_BKNIGHT_PNG));
        board[0][6].put(new Knight(0, board[0][6], RESOURCES_BKNIGHT_PNG));
        board[7][1].put(new Knight(1, board[7][1], RESOURCES_WKNIGHT_PNG));
        board[7][6].put(new Knight(1, board[7][6], RESOURCES_WKNIGHT_PNG));

        board[0][2].put(new Bishop(0, board[0][2], RESOURCES_BBISHOP_PNG));
        board[0][5].put(new Bishop(0, board[0][5], RESOURCES_BBISHOP_PNG));
        board[7][2].put(new Bishop(1, board[7][2], RESOURCES_WBISHOP_PNG));
        board[7][5].put(new Bishop(1, board[7][5], RESOURCES_WBISHOP_PNG));

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                Bpieces.add(board[y][x].getOccupyingPiece());
                Wpieces.add(board[7 - y][x].getOccupyingPiece());
            }
        }

        cmd = new CheckmateDetector(this, Wpieces, Bpieces, wk, bk);
    }

    public Square[][] getSquareArray() {
        return this.board;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void setCurrPiece(Piece p) {
        this.currPiece = p;
    }

    public Piece getCurrPiece() {
        return this.currPiece;
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square sq = board[y][x];
                sq.paintComponent(g);
            }
        }

        if (currPiece != null) {
            if ((currPiece.getColor() == 1 && whiteTurn)
                    || (currPiece.getColor() == 0 && !whiteTurn)) {
                final Image i = currPiece.getImage();
                g.drawImage(i, currX, currY, null);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currX = e.getX();
        currY = e.getY();

        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (sq.isOccupied()) {
            currPiece = sq.getOccupyingPiece();
            if (currPiece.getColor() == 0 && whiteTurn)
                return;
            if (currPiece.getColor() == 1 && !whiteTurn)
                return;
            sq.setDisplay(false);
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (currPiece != null) {
            if (currPiece.getColor() == 0 && whiteTurn)
                return;
            if (currPiece.getColor() == 1 && !whiteTurn)
                return;

            List<Square> legalMoves = currPiece.getLegalMoves(this);
            movable = cmd.getAllowableSquares(whiteTurn);

            if (legalMoves.contains(sq) && movable.contains(sq)
                    && cmd.testMove(currPiece, sq)) {
                sq.setDisplay(true);
                currPiece.move(sq);
                cmd.update();

                if (cmd.blackCheckMated()) {
                    currPiece = null;
                    repaint();
                    this.removeMouseListener(this);
                    this.removeMouseMotionListener(this);
                    g.checkmateOccurred(0);
                } else if (cmd.whiteCheckMated()) {
                    currPiece = null;
                    repaint();
                    this.removeMouseListener(this);
                    this.removeMouseMotionListener(this);
                    g.checkmateOccurred(1);
                } else {
                    currPiece = null;
                    whiteTurn = !whiteTurn;
                    movable = cmd.getAllowableSquares(whiteTurn);
                }

            } else {
                currPiece.getPosition().setDisplay(true);
                currPiece = null;
            }
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currX = e.getX() - 24;
        currY = e.getY() - 24;

        repaint();
    }

    // Irrelevant methods, do nothing for these mouse behaviors
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}

@SuppressWarnings("serial")
class Square extends JComponent {
    private Board b;

    private final int color;
    private Piece occupyingPiece;
    private boolean dispPiece;

    private int xNum;
    private int yNum;

    public Square(Board b, int c, int xNum, int yNum) {

        this.b = b;
        this.color = c;
        this.dispPiece = true;
        this.xNum = xNum;
        this.yNum = yNum;

        this.setBorder(BorderFactory.createEmptyBorder());
    }

    public int getColor() {
        return this.color;
    }

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    public boolean isOccupied() {
        return (this.occupyingPiece != null);
    }

    public int getXNum() {
        return this.xNum;
    }

    public int getYNum() {
        return this.yNum;
    }

    public void setDisplay(boolean v) {
        this.dispPiece = v;
    }

    public void put(Piece p) {
        this.occupyingPiece = p;
        p.setPosition(this);
    }

    public Piece removePiece() {
        Piece p = this.occupyingPiece;
        this.occupyingPiece = null;
        return p;
    }

    public void capture(Piece p) {
        Piece k = getOccupyingPiece();
        if (k.getColor() == 0)
            b.Bpieces.remove(k);
        if (k.getColor() == 1)
            b.Wpieces.remove(k);
        this.occupyingPiece = p;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.color == 1) {
            g.setColor(new Color(221, 192, 127));
        } else {
            g.setColor(new Color(101, 67, 33));
        }

        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        if (occupyingPiece != null && dispPiece) {
            occupyingPiece.draw(g);
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + xNum;
        result = prime * result + yNum;
        return result;
    }

}

abstract class Piece {
    private final int color;
    private Square currentSquare;
    private BufferedImage img;

    public Piece(int color, Square initSq, String img_file) {
        this.color = color;
        this.currentSquare = initSq;

        try {
            if (this.img == null) {
                this.img = ImageIO.read(getClass().getResource(img_file));
            }
        } catch (IOException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    public boolean move(Square fin) {
        Piece occup = fin.getOccupyingPiece();

        if (occup != null) {
            if (occup.getColor() == this.color)
                return false;
            else
                fin.capture(this);
        }

        currentSquare.removePiece();
        this.currentSquare = fin;
        currentSquare.put(this);
        return true;
    }

    public Square getPosition() {
        return currentSquare;
    }

    public void setPosition(Square sq) {
        this.currentSquare = sq;
    }

    public int getColor() {
        return color;
    }

    public Image getImage() {
        return img;
    }

    public void draw(Graphics g) {
        int x = currentSquare.getX();
        int y = currentSquare.getY();

        g.drawImage(this.img, x, y, null);
    }

    public int[] getLinearOccupations(Square[][] board, int x, int y) {
        int lastYabove = 0;
        int lastXright = 7;
        int lastYbelow = 7;
        int lastXleft = 0;

        for (int i = 0; i < y; i++) {
            if (board[i][x].isOccupied()) {
                if (board[i][x].getOccupyingPiece().getColor() != this.color) {
                    lastYabove = i;
                } else
                    lastYabove = i + 1;
            }
        }

        for (int i = 7; i > y; i--) {
            if (board[i][x].isOccupied()) {
                if (board[i][x].getOccupyingPiece().getColor() != this.color) {
                    lastYbelow = i;
                } else
                    lastYbelow = i - 1;
            }
        }

        for (int i = 0; i < x; i++) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getOccupyingPiece().getColor() != this.color) {
                    lastXleft = i;
                } else
                    lastXleft = i + 1;
            }
        }

        for (int i = 7; i > x; i--) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getOccupyingPiece().getColor() != this.color) {
                    lastXright = i;
                } else
                    lastXright = i - 1;
            }
        }

        int[] occups = { lastYabove, lastYbelow, lastXleft, lastXright };

        return occups;
    }

    public List<Square> getDiagonalOccupations(Square[][] board, int x, int y) {
        LinkedList<Square> diagOccup = new LinkedList<Square>();

        int xNW = x - 1;
        int xSW = x - 1;
        int xNE = x + 1;
        int xSE = x + 1;
        int yNW = y - 1;
        int ySW = y + 1;
        int yNE = y - 1;
        int ySE = y + 1;

        while (xNW >= 0 && yNW >= 0) {
            if (board[yNW][xNW].isOccupied()) {
                if (board[yNW][xNW].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[yNW][xNW]);
                    break;
                }
            } else {
                diagOccup.add(board[yNW][xNW]);
                yNW--;
                xNW--;
            }
        }

        while (xSW >= 0 && ySW < 8) {
            if (board[ySW][xSW].isOccupied()) {
                if (board[ySW][xSW].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[ySW][xSW]);
                    break;
                }
            } else {
                diagOccup.add(board[ySW][xSW]);
                ySW++;
                xSW--;
            }
        }

        while (xSE < 8 && ySE < 8) {
            if (board[ySE][xSE].isOccupied()) {
                if (board[ySE][xSE].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[ySE][xSE]);
                    break;
                }
            } else {
                diagOccup.add(board[ySE][xSE]);
                ySE++;
                xSE++;
            }
        }

        while (xNE < 8 && yNE >= 0) {
            if (board[yNE][xNE].isOccupied()) {
                if (board[yNE][xNE].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[yNE][xNE]);
                    break;
                }
            } else {
                diagOccup.add(board[yNE][xNE]);
                yNE--;
                xNE++;
            }
        }

        return diagOccup;
    }

    // No implementation, to be implemented by each subclass
    public abstract List<Square> getLegalMoves(Board b);
}

class Bishop extends Piece {

    public Bishop(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        Square[][] board = b.getSquareArray();
        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        return getDiagonalOccupations(board, x, y);
    }
}

class King extends Piece {

    public King(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 1; i > -2; i--) {
            for (int k = 1; k > -2; k--) {
                if (!(i == 0 && k == 0)) {
                    try {
                        if (!board[y + k][x + i].isOccupied() ||
                                board[y + k][x + i].getOccupyingPiece().getColor() != this.getColor()) {
                            legalMoves.add(board[y + k][x + i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }

        return legalMoves;
    }

}

class Knight extends Piece {

    public Knight(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 2; i > -3; i--) {
            for (int k = 2; k > -3; k--) {
                if (Math.abs(i) == 2 ^ Math.abs(k) == 2) {
                    if (k != 0 && i != 0) {
                        try {
                            legalMoves.add(board[y + k][x + i]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }
            }
        }

        return legalMoves;
    }

}

class Pawn extends Piece {
    private boolean wasMoved;

    public Pawn(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public boolean move(Square fin) {
        boolean b = super.move(fin);
        wasMoved = true;
        return b;
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();
        int c = this.getColor();

        if (c == 0) {
            if (!wasMoved) {
                if (!board[y + 2][x].isOccupied()) {
                    legalMoves.add(board[y + 2][x]);
                }
            }

            if (y + 1 < 8) {
                if (!board[y + 1][x].isOccupied()) {
                    legalMoves.add(board[y + 1][x]);
                }
            }

            if (x + 1 < 8 && y + 1 < 8) {
                if (board[y + 1][x + 1].isOccupied()) {
                    legalMoves.add(board[y + 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y + 1 < 8) {
                if (board[y + 1][x - 1].isOccupied()) {
                    legalMoves.add(board[y + 1][x - 1]);
                }
            }
        }

        if (c == 1) {
            if (!wasMoved) {
                if (!board[y - 2][x].isOccupied()) {
                    legalMoves.add(board[y - 2][x]);
                }
            }

            if (y - 1 >= 0) {
                if (!board[y - 1][x].isOccupied()) {
                    legalMoves.add(board[y - 1][x]);
                }
            }

            if (x + 1 < 8 && y - 1 >= 0) {
                if (board[y - 1][x + 1].isOccupied()) {
                    legalMoves.add(board[y - 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y - 1 >= 0) {
                if (board[y - 1][x - 1].isOccupied()) {
                    legalMoves.add(board[y - 1][x - 1]);
                }
            }
        }

        return legalMoves;
    }
}

class Queen extends Piece {

    public Queen(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        int[] occups = getLinearOccupations(board, x, y);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y)
                legalMoves.add(board[i][x]);
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x)
                legalMoves.add(board[y][i]);
        }

        List<Square> bMoves = getDiagonalOccupations(board, x, y);

        legalMoves.addAll(bMoves);

        return legalMoves;
    }

}

class Rook extends Piece {

    public Rook(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        int[] occups = getLinearOccupations(board, x, y);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y)
                legalMoves.add(board[i][x]);
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x)
                legalMoves.add(board[y][i]);
        }

        return legalMoves;
    }

}

class CheckmateDetector {
    private Board b;
    private LinkedList<Piece> wPieces;
    private LinkedList<Piece> bPieces;
    private LinkedList<Square> movableSquares;
    private final LinkedList<Square> squares;
    private King bk;
    private King wk;
    private HashMap<Square, List<Piece>> wMoves;
    private HashMap<Square, List<Piece>> bMoves;

    /**
     * Constructs a new instance of CheckmateDetector on a given board. By
     * convention should be called when the board is in its initial state.
     * 
     * @param b       The board which the detector monitors
     * @param wPieces White pieces on the board.
     * @param bPieces Black pieces on the board.
     * @param wk      Piece object representing the white king
     * @param bk      Piece object representing the black king
     */
    public CheckmateDetector(Board b, LinkedList<Piece> wPieces,
            LinkedList<Piece> bPieces, King wk, King bk) {
        this.b = b;
        this.wPieces = wPieces;
        this.bPieces = bPieces;
        this.bk = bk;
        this.wk = wk;

        // Initialize other fields
        squares = new LinkedList<Square>();
        movableSquares = new LinkedList<Square>();
        wMoves = new HashMap<Square, List<Piece>>();
        bMoves = new HashMap<Square, List<Piece>>();

        Square[][] brd = b.getSquareArray();

        // add all squares to squares list and as hashmap keys
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                squares.add(brd[y][x]);
                wMoves.put(brd[y][x], new LinkedList<Piece>());
                bMoves.put(brd[y][x], new LinkedList<Piece>());
            }
        }

        // update situation
        update();
    }

    /**
     * Updates the object with the current situation of the game.
     */
    public void update() {
        // Iterators through pieces
        Iterator<Piece> wIter = wPieces.iterator();
        Iterator<Piece> bIter = bPieces.iterator();

        // empty moves and movable squares at each update
        for (List<Piece> pieces : wMoves.values()) {
            pieces.removeAll(pieces);
        }

        for (List<Piece> pieces : bMoves.values()) {
            pieces.removeAll(pieces);
        }

        movableSquares.removeAll(movableSquares);

        // Add each move white and black can make to map
        while (wIter.hasNext()) {
            Piece p = wIter.next();

            if (!p.getClass().equals(King.class)) {
                if (p.getPosition() == null) {
                    wIter.remove();
                    continue;
                }

                List<Square> mvs = p.getLegalMoves(b);
                Iterator<Square> iter = mvs.iterator();
                while (iter.hasNext()) {
                    List<Piece> pieces = wMoves.get(iter.next());
                    pieces.add(p);
                }
            }
        }

        while (bIter.hasNext()) {
            Piece p = bIter.next();

            if (!p.getClass().equals(King.class)) {
                if (p.getPosition() == null) {
                    wIter.remove();
                    continue;
                }

                List<Square> mvs = p.getLegalMoves(b);
                Iterator<Square> iter = mvs.iterator();
                while (iter.hasNext()) {
                    List<Piece> pieces = bMoves.get(iter.next());
                    pieces.add(p);
                }
            }
        }
    }

    /**
     * Checks if the black king is threatened
     * 
     * @return boolean representing whether the black king is in check.
     */
    public boolean blackInCheck() {
        update();
        Square sq = bk.getPosition();
        if (wMoves.get(sq).isEmpty()) {
            movableSquares.addAll(squares);
            return false;
        } else
            return true;
    }

    /**
     * Checks if the white king is threatened
     * 
     * @return boolean representing whether the white king is in check.
     */
    public boolean whiteInCheck() {
        update();
        Square sq = wk.getPosition();
        if (bMoves.get(sq).isEmpty()) {
            movableSquares.addAll(squares);
            return false;
        } else
            return true;
    }

    /**
     * Checks whether black is in checkmate.
     * 
     * @return boolean representing if black player is checkmated.
     */
    public boolean blackCheckMated() {
        boolean checkmate = true;
        // Check if black is in check
        if (!this.blackInCheck())
            return false;

        // If yes, check if king can evade
        if (canEvade(wMoves, bk))
            checkmate = false;

        // If no, check if threat can be captured
        List<Piece> threats = wMoves.get(bk.getPosition());
        if (canCapture(bMoves, threats, bk))
            checkmate = false;

        // If no, check if threat can be blocked
        if (canBlock(threats, bMoves, bk))
            checkmate = false;

        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }

    /**
     * Checks whether white is in checkmate.
     * 
     * @return boolean representing if white player is checkmated.
     */
    public boolean whiteCheckMated() {
        boolean checkmate = true;
        // Check if white is in check
        if (!this.whiteInCheck())
            return false;

        // If yes, check if king can evade
        if (canEvade(bMoves, wk))
            checkmate = false;

        // If no, check if threat can be captured
        List<Piece> threats = bMoves.get(wk.getPosition());
        if (canCapture(wMoves, threats, wk))
            checkmate = false;

        // If no, check if threat can be blocked
        if (canBlock(threats, wMoves, wk))
            checkmate = false;

        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }

    /*
     * Helper method to determine if the king can evade the check.
     * Gives a false positive if the king can capture the checking piece.
     */
    private boolean canEvade(Map<Square, List<Piece>> tMoves, King tKing) {
        boolean evade = false;
        List<Square> kingsMoves = tKing.getLegalMoves(b);
        Iterator<Square> iterator = kingsMoves.iterator();

        // If king is not threatened at some square, it can evade
        while (iterator.hasNext()) {
            Square sq = iterator.next();
            if (!testMove(tKing, sq))
                continue;
            if (tMoves.get(sq).isEmpty()) {
                movableSquares.add(sq);
                evade = true;
            }
        }

        return evade;
    }

    /*
     * Helper method to determine if the threatening piece can be captured.
     */
    private boolean canCapture(Map<Square, List<Piece>> poss,
            List<Piece> threats, King k) {

        boolean capture = false;
        if (threats.size() == 1) {
            Square sq = threats.get(0).getPosition();

            if (k.getLegalMoves(b).contains(sq)) {
                movableSquares.add(sq);
                if (testMove(k, sq)) {
                    capture = true;
                }
            }

            List<Piece> caps = poss.get(sq);
            ConcurrentLinkedDeque<Piece> capturers = new ConcurrentLinkedDeque<Piece>();
            capturers.addAll(caps);

            if (!capturers.isEmpty()) {
                movableSquares.add(sq);
                for (Piece p : capturers) {
                    if (testMove(p, sq)) {
                        capture = true;
                    }
                }
            }
        }

        return capture;
    }

    /*
     * Helper method to determine if check can be blocked by a piece.
     */
    private boolean canBlock(List<Piece> threats,
            Map<Square, List<Piece>> blockMoves, King k) {
        boolean blockable = false;

        if (threats.size() == 1) {
            Square ts = threats.get(0).getPosition();
            Square ks = k.getPosition();
            Square[][] brdArray = b.getSquareArray();

            if (ks.getXNum() == ts.getXNum()) {
                int max = Math.max(ks.getYNum(), ts.getYNum());
                int min = Math.min(ks.getYNum(), ts.getYNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blks = blockMoves.get(brdArray[i][ks.getXNum()]);
                    ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                    blockers.addAll(blks);

                    if (!blockers.isEmpty()) {
                        movableSquares.add(brdArray[i][ks.getXNum()]);

                        for (Piece p : blockers) {
                            if (testMove(p, brdArray[i][ks.getXNum()])) {
                                blockable = true;
                            }
                        }

                    }
                }
            }

            if (ks.getYNum() == ts.getYNum()) {
                int max = Math.max(ks.getXNum(), ts.getXNum());
                int min = Math.min(ks.getXNum(), ts.getXNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blks = blockMoves.get(brdArray[ks.getYNum()][i]);
                    ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                    blockers.addAll(blks);

                    if (!blockers.isEmpty()) {

                        movableSquares.add(brdArray[ks.getYNum()][i]);

                        for (Piece p : blockers) {
                            if (testMove(p, brdArray[ks.getYNum()][i])) {
                                blockable = true;
                            }
                        }

                    }
                }
            }

            Class<? extends Piece> tC = threats.get(0).getClass();

            if (tC.equals(Queen.class) || tC.equals(Bishop.class)) {
                int kX = ks.getXNum();
                int kY = ks.getYNum();
                int tX = ts.getXNum();
                int tY = ts.getYNum();

                if (kX > tX && kY > tY) {
                    for (int i = tX + 1; i < kX; i++) {
                        tY++;
                        List<Piece> blks = blockMoves.get(brdArray[tY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[tY][i]);

                            for (Piece p : blockers) {
                                if (testMove(p, brdArray[tY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                if (kX > tX && tY > kY) {
                    for (int i = tX + 1; i < kX; i++) {
                        tY--;
                        List<Piece> blks = blockMoves.get(brdArray[tY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[tY][i]);

                            for (Piece p : blockers) {
                                if (testMove(p, brdArray[tY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                if (tX > kX && kY > tY) {
                    for (int i = tX - 1; i > kX; i--) {
                        tY++;
                        List<Piece> blks = blockMoves.get(brdArray[tY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[tY][i]);

                            for (Piece p : blockers) {
                                if (testMove(p, brdArray[tY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                if (tX > kX && tY > kY) {
                    for (int i = tX - 1; i > kX; i--) {
                        tY--;
                        List<Piece> blks = blockMoves.get(brdArray[tY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[tY][i]);

                            for (Piece p : blockers) {
                                if (testMove(p, brdArray[tY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return blockable;
    }

    /**
     * Method to get a list of allowable squares that the player can move.
     * Defaults to all squares, but limits available squares if player is in
     * check.
     * 
     * @param b boolean representing whether it's white player's turn (if yes,
     *          true)
     * @return List of squares that the player can move into.
     */
    public List<Square> getAllowableSquares(boolean b) {
        movableSquares.removeAll(movableSquares);
        if (whiteInCheck()) {
            whiteCheckMated();
        } else if (blackInCheck()) {
            blackCheckMated();
        }
        return movableSquares;
    }

    /**
     * Tests a move a player is about to make to prevent making an illegal move
     * that puts the player in check.
     * 
     * @param p  Piece moved
     * @param sq Square to which p is about to move
     * @return false if move would cause a check
     */
    public boolean testMove(Piece p, Square sq) {
        Piece c = sq.getOccupyingPiece();

        boolean movetest = true;
        Square init = p.getPosition();

        p.move(sq);
        update();

        if (p.getColor() == 0 && blackInCheck())
            movetest = false;
        else if (p.getColor() == 1 && whiteInCheck())
            movetest = false;

        p.move(init);
        if (c != null)
            sq.put(c);

        update();

        movableSquares.addAll(squares);
        return movetest;
    }

}

class Clock {
    private int hh;
    private int mm;
    private int ss;

    public Clock(int hh, int mm, int ss) {
        this.hh = hh;
        this.mm = mm;
        this.ss = ss;
    }

    public boolean outOfTime() {
        return (hh == 0 && mm == 0 && ss == 0);
    }

    public void decr() {
        if (this.mm == 0 && this.ss == 0) {
            this.ss = 59;
            this.mm = 59;
            this.hh--;
        } else if (this.ss == 0) {
            this.ss = 59;
            this.mm--;
        } else
            this.ss--;
    }

    public String getTime() {
        String fHrs = String.format("%02d", this.hh);
        String fMins = String.format("%02d", this.mm);
        String fSecs = String.format("%02d", this.ss);
        String fTime = fHrs + ":" + fMins + ":" + fSecs;
        return fTime;
    }
}
