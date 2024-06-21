package com.bryanmzili.DevLab.games.jogo_da_velha;

import com.bryanmzili.DevLab.games.Game;
import com.bryanmzili.DevLab.games.Jogada;

public class JogoDaVelha implements Game {

    private int[][] jogo = new int[3][3];
    private String vencedor;

    private boolean min1Max3(int numero) {
        if (numero >= 1 || numero <= 3) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isJogoFinalizado() {

        // Verificar se há uma linha horizontal com todos os elementos iguais
        for (int linha = 0; linha < 3; linha++) {

            int casa1 = jogo[linha][0];
            int casa2 = jogo[linha][1];
            int casa3 = jogo[linha][2];

            if (casa1 == casa2 && casa2 == casa3 && casa1 != 0) {
                vencedor = "Jogador " + casa1 + " venceu o Jogo \n" + this.toString();
                return true;
            }
        }

        // Verificar se há uma coluna vertical com todos os elementos iguais
        for (int coluna = 0; coluna < 3; coluna++) {

            int casa1 = jogo[0][coluna];
            int casa2 = jogo[1][coluna];
            int casa3 = jogo[2][coluna];

            if (casa1 == casa2 && casa2 == casa3 && casa1 != 0) {
                vencedor = "Jogador " + casa1 + " venceu o Jogo \n" + this.toString();
                return true;
            }
        }

        // Verificar se há uma coluna na diagonal com todos os elementos iguais
        int ponta1 = jogo[0][0];//pontas de cima
        int ponta2 = jogo[0][2];

        int meio = jogo[1][1];

        int ponta3 = jogo[2][0];//pontas de baixo
        int ponta4 = jogo[2][2];

        //Testando Diagonal da Esquerda para a Direita
        if (ponta1 == meio && meio == ponta4 && meio != 0) {
            vencedor = "Jogador " + meio + " venceu o Jogo \n" + this.toString();
            return true;
        }

        //Testando Diagonal da Direita para a Esquerda
        if (ponta2 == meio && meio == ponta3 && meio != 0) {
            vencedor = "Jogador " + meio + " venceu o Jogo \n" + this.toString();
            return true;
        }

        // Verificar se a matriz está totalmente preenchida
        for (int i = 0; i < jogo.length; i++) {
            for (int j = 0; j < jogo[i].length; j++) {
                if (jogo[i][j] == 0) {
                    return false;
                }
            }
        }

        vencedor = "O Jogo Finalizou com Empate\n" + this.toString();

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < jogo.length; i++) {
            for (int j = 0; j < jogo[i].length; j++) {
                sb.append(jogo[i][j]);
                if (j < jogo[i].length - 1) {
                    sb.append(" | ");
                }
            }
            if (i < jogo.length - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String getVencedor() {
        return vencedor;
    }

    @Override
    public boolean isJogadaValida(Jogada jogada, int jogadorAtual) {
        MovimentoJogoDaVelha movimento = (MovimentoJogoDaVelha) jogada;
        int horizontal = movimento.getLinha();
        int vertical = movimento.getColuna();

        if (min1Max3(horizontal) && min1Max3(vertical)) {
            if (jogo[horizontal - 1][vertical - 1] == 0) {
                jogo[horizontal - 1][vertical - 1] = jogadorAtual;
                return true;
            }
        }

        return false;

    }
}
