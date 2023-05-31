import java.util.Scanner;

public class Tetris {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TetrisBoard board = new TetrisBoard();
        board.spawnNewTetromino();
        System.out.println();
        board.printBoard();

        for (int i = 0; i < 20; i++) {
            board.movePiece(TetrisBoard.MovementDirection.DOWN);
            board.movePiece(TetrisBoard.MovementDirection.LEFT);
            System.out.println();
            board.printBoard();
        }

        board.spawnNewTetromino();
        System.out.println();
        for (int i = 0; i < 20; i++) {
            board.movePiece(TetrisBoard.MovementDirection.DOWN);
            board.movePiece(TetrisBoard.MovementDirection.RIGHT);
            System.out.println();
            board.printBoard();
        }
    }
}
