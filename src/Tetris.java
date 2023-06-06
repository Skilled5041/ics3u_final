import java.util.Scanner;
import java.util.stream.IntStream;

public class Tetris {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TetrisBoard board = new TetrisBoard();
        board.spawnNewTetromino();
        System.out.println();
        board.printBoard();

        while (true) {
            String direction = sc.nextLine();
            if (direction.equals("q")) {
                break;
            }
            try {
                int times = Integer.parseInt(sc.nextLine());
                if (direction.equals("r")) {
                    board.rotatePiece(times);
                    board.printBoard();
                    System.out.println();
                    continue;
                }
                IntStream.range(0, times).forEach(i -> {
                    board.movePiece(TetrisBoard.MovementDirection.valueOf(direction));
                });
                board.printBoard();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
