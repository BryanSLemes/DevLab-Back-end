package com.bryanmzili.DevLab.games.xadrez;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;

public class XadrezUtils {

    private Board board;
    private int vencedor = -1;
    private boolean reiEmXeque = false;

    public XadrezUtils() {
        this.board = new Board();
    }

    public String execPlay(String origem, String destino) {
        try {
            Move move = new Move(Square.fromValue(origem), Square.fromValue(destino));
            if (board.isMoveLegal(move, true)) {
                board.doMove(move);

                if (jogoTerminou()) {
                    if (board.isMated()) {
                        Side vencedor = board.getSideToMove() == Side.WHITE ? Side.BLACK : Side.WHITE;
                        return vencedor == Side.WHITE ? "Jogador 1 venceu" : "Jogador 2 venceu";
                    } else if (vencedor == 0 && reiEmXeque) {
                        return "Empate";
                    } else if (vencedor != -1 && reiEmXeque) {
                        return "Jogador " + vencedor + " venceu";
                    }

                    return "Empate";
                } else {
                    return "Movimento aplicado";
                }
            }
        } catch (Exception e) {
            return "Movimento inválido";
        }
        return "Movimento inválido";
    }

    public boolean jogoTerminou() {
        if (board.isMated()) {
            return true;
        }

        if (board.isStaleMate()) {
            return true;
        }

        if (board.isInsufficientMaterial()) {
            return true;
        }

        if (board.isRepetition()) {
            return true;
        }

        if (board.isDraw()) {
            return true;
        }

        if (reiEmXeque(gameToString())) {
            return true;
        }

        return false;
    }

    public int turnoAtual() {
        return board.getSideToMove() == Side.WHITE ? 1 : 2;
    }

    public String gameToString() {
        String boardStr = board.toString();
        int idx = boardStr.lastIndexOf("Side:");
        if (idx != -1) {
            return boardStr.substring(0, idx).trim();
        }
        return boardStr;
    }

    public boolean reiEmXeque(String tabuleiro) {
        char[][] board = new char[8][8];
        String[] linhas = tabuleiro.split("\n");
        for (int i = 0; i < 8; i++) {
            board[i] = linhas[i].toCharArray();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char p = board[i][j];
                if (p == '.') {
                    continue;
                }

                int[][] dirs;
                switch (Character.toLowerCase(p)) {
                    case 'p':
                        if (Character.isUpperCase(p)) {
                            if (i - 1 >= 0 && j - 1 >= 0 && board[i - 1][j - 1] == 'k') {
                                vencedor = 1;
                                reiEmXeque = true;
                                return true;
                            }
                            if (i - 1 >= 0 && j + 1 < 8 && board[i - 1][j + 1] == 'k') {
                                vencedor = 1;
                                reiEmXeque = true;
                                return true;
                            }
                        } else {
                            if (i + 1 < 8 && j - 1 >= 0 && board[i + 1][j - 1] == 'K') {
                                vencedor = 2;
                                reiEmXeque = true;
                                return true;
                            }
                            if (i + 1 < 8 && j + 1 < 8 && board[i + 1][j + 1] == 'K') {
                                vencedor = 2;
                                reiEmXeque = true;
                                return true;
                            }
                        }
                        break;
                    case 'n':
                        int[][] moves = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
                        for (int[] m : moves) {
                            int x = i + m[0], y = j + m[1];
                            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                                if (p == 'n' && board[x][y] == 'K') {
                                    vencedor = 2;
                                    reiEmXeque = true;
                                    return true;
                                } else if (p == 'N' && board[x][y] == 'k') {
                                    vencedor = 1;
                                    reiEmXeque = true;
                                    return true;
                                }
                            }
                        }
                        break;
                    case 'b':
                    case 'r':
                    case 'q':
                        if (p == 'b' || p == 'B') {
                            dirs = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                        } else if (p == 'r' || p == 'R') {
                            dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                        } else {
                            dirs = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                        }
                        for (int[] d : dirs) {
                            int x = i + d[0], y = j + d[1];
                            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                                if (board[x][y] != '.') {
                                    if (Character.isUpperCase(p) && board[x][y] == 'k') {
                                        vencedor = 1;
                                        reiEmXeque = true;
                                        return true;
                                    } else if (Character.isLowerCase(p) && board[x][y] == 'K') {
                                        vencedor = 2;
                                        reiEmXeque = true;
                                        return true;
                                    }
                                    break;
                                }
                                x += d[0];
                                y += d[1];
                            }
                        }
                        break;
                    case 'k':
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (dx == 0 && dy == 0) {
                                    continue;
                                }
                                int x = i + dx, y = j + dy;
                                if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                                    if (p == 'k' && board[x][y] == 'K') {
                                        vencedor = 2;
                                        reiEmXeque = true;
                                        return true;
                                    } else if (p == 'K' && board[x][y] == 'k') {
                                        vencedor = 1;
                                        reiEmXeque = true;
                                        return true;
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        }

        vencedor = 0;
        return false;
    }

    public int getVencedor() {
        return vencedor;
    }

}
