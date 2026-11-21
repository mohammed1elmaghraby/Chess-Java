import Main.Board;

import javax.swing.*;
import java.awt.*;

/**
 * Entry point of the chess game.
 * Creates the board, puts it inside a window and shows the window.
 */
void main() {

    Board board = new Board();

    JFrame frame = new JFrame();
    frame.setMinimumSize(new Dimension(1000, 1000));
    frame.setLayout(new GridBagLayout());
    frame.getContentPane().setBackground(Color.black);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(board);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);   // always last
}
