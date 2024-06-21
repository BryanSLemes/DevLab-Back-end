package com.bryanmzili.DevLab.games;

public interface Game {

    public boolean isJogadaValida(Jogada jogada, int jogadorAtual);

    public boolean isJogoFinalizado();

    public String getVencedor();
}
