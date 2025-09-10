package com.bryanmzili.DevLab.games.dama;

import com.bryanmzili.DevLab.games.Game;
import com.bryanmzili.DevLab.games.Jogada;

public class Dama implements Game {

    private final DamaUtils jogo;
    private int vencedor = -1;

    public Dama() {
        jogo = new DamaUtils(true);
    }

    @Override
    public boolean isJogadaValida(Jogada jogada, int jogadorAtual) {
        MovimentoDama movimento = (MovimentoDama) jogada;
        String posAntiga = MovimentoDama.toIdPosicao(movimento.getLinhaOrigem(), movimento.getColunaOrigem());
        String posNova = MovimentoDama.toIdPosicao(movimento.getLinhaDestino(), movimento.getColunaDestino());

        String resultado = jogo.execPlay(posAntiga, posNova);
        switch (resultado) {
            case "Movimento inválido" -> {
                return false;
            }
            case "Jogador 1 venceu!" -> {
                setVencedor(1);
                return true;
            }
            case "Jogador 2 venceu!" -> {
                setVencedor(2);
                return true;
            }
            case "Empate!" -> {
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
