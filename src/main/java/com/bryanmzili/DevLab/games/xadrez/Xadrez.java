package com.bryanmzili.DevLab.games.xadrez;

import com.bryanmzili.DevLab.games.Game;
import com.bryanmzili.DevLab.games.Jogada;

public class Xadrez implements Game {

    private final XadrezUtils jogo;
    private int vencedor = -1;

    public Xadrez() {
        jogo = new XadrezUtils();
    }

    @Override
    public boolean isJogadaValida(Jogada jogada, int jogadorAtual) {
        MovimentoXadrez movimento = (MovimentoXadrez) jogada;
        String posAntiga = MovimentoXadrez.toIdPosicao(movimento.getLinhaOrigem(), movimento.getColunaOrigem());
        String posNova = MovimentoXadrez.toIdPosicao(movimento.getLinhaDestino(), movimento.getColunaDestino());

        String resultado = jogo.execPlay(posAntiga, posNova);
        switch (resultado) {
            case "Movimento inválido" -> {
                return false;
            }
            case "Jogador 1 venceu" -> {
                setVencedor(1);
                return true;
            }
            case "Jogador 2 venceu" -> {
                setVencedor(2);
                return true;
            }
            case "Empate" -> {
                setVencedor(0);
                return true;
            }
            case "Movimento aplicado" -> {
                return true;
            }
        }
        return true;
    }
    
    public int jogadorAtual(){
        return jogo.turnoAtual();
    }

    @Override
    public boolean isJogoFinalizado() {
        return vencedor != -1;
    }

    @Override
    public int getVencedor() {
        return vencedor;
    }

    public void setVencedor(int vencedor) {
        this.vencedor = vencedor;
    }

    @Override
    public String toString() {
        return jogo.gameToString();
    }
}
