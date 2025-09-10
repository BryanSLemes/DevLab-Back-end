package com.bryanmzili.DevLab.games.dama;

import com.bryanmzili.DevLab.games.dama.java_checkers.logic.MoveGenerator;
import com.bryanmzili.DevLab.games.dama.java_checkers.model.Board;
import com.bryanmzili.DevLab.games.dama.java_checkers.model.Game;
import com.bryanmzili.DevLab.games.dama.java_checkers.ui.CheckerBoard;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DamaUtils {

    private final CheckerBoard boardUI;

    public DamaUtils(boolean exibir) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        boardUI = new CheckerBoard(null);
    }

    public String execPlay(String originalPos, String newPos) {
        int[] o = fromString(originalPos);
        int[] n = fromString(newPos);

        Game game = boardUI.getGame();
        Board board = game.getBoard();

        if (o == null || n == null) {
            return "Movimento inválido";
        }

        List<Point> allSkips = new ArrayList<>();
        List<Point> blackPieces = board.find(Board.BLACK_CHECKER);
        blackPieces.addAll(board.find(Board.BLACK_KING));
        List<Point> whitePieces = board.find(Board.WHITE_CHECKER);
        whitePieces.addAll(board.find(Board.WHITE_KING));
        List<Point> pieces = game.isP1Turn() ? blackPieces : whitePieces;

        for (Point p : pieces) {
            int index = Board.toIndex(p);
            if (!MoveGenerator.getSkips(board, index).isEmpty()) {
                allSkips.add(p);
            }
        }

        if (!allSkips.isEmpty()) {
            Point startPoint = new Point(o[0], o[1]);
            if (!allSkips.contains(startPoint)) {
                return "Movimento inválido";
            }
        }

        boolean moved = game.move(new Point(o[0], o[1]), new Point(n[0], n[1]));
        boardUI.update();

        if (!moved) {
            return "Movimento inválido";
        }
        
        boolean hasBlack = !game.getBoard().find(Board.BLACK_CHECKER).isEmpty()
                || !game.getBoard().find(Board.BLACK_KING).isEmpty();
        boolean hasWhite = !game.getBoard().find(Board.WHITE_CHECKER).isEmpty()
                || !game.getBoard().find(Board.WHITE_KING).isEmpty();

        if (hasBlack && !hasWhite) {
            return "Jogador 1 venceu!";
        }
        if (!hasBlack && hasWhite) {
            return "Jogador 2 venceu!";
        }
        if (!hasBlack && !hasWhite) {
            return "Empate!";
        }

        return "Movimento aplicado";
    }

    public String gameToString() {
        return boardToString(boardUI.getGame().getBoard());
    }

    public int turnoAtual() {
        Game game = boardUI.getGame();
        if (game.isP1Turn()) {
            return 1;
        } else {
            return 2;
        }
    }

    public int[] fromString(String pos) {
        if (pos == null || pos.length() != 2) {
            return null;
        }
        char colChar = Character.toUpperCase(pos.charAt(0));
        char rowChar = pos.charAt(1);

        if (colChar < 'A' || colChar > 'H' || rowChar < '1' || rowChar > '8') {
            return null;
        }

        int col = colChar - 'A';
        int row = 8 - (rowChar - '0');
        return new int[]{col, row};
    }

    public String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int id = board.get(x, y);
                char c;
                c = switch (id) {
                    case Board.BLACK_CHECKER -> 'p';
                    case Board.BLACK_KING -> 'P';
                    case Board.WHITE_CHECKER -> 'b';
                    case Board.WHITE_KING -> 'B';
                    default -> '.';
                };
                sb.append(c);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
