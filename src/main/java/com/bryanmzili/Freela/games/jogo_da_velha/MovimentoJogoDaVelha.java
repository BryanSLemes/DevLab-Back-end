package com.bryanmzili.Freela.games.jogo_da_velha;

import com.bryanmzili.Freela.games.Jogada;

public class MovimentoJogoDaVelha implements Jogada {

    private int linha;
    private int coluna;

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

}
