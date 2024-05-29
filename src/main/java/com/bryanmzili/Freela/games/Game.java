package com.bryanmzili.Freela.games;

public interface Game {

    public boolean isJogadaValida(Jogada jogada, int jogadorAtual);

    public boolean isJogoFinalizado();

    public String getVencedor();
}
