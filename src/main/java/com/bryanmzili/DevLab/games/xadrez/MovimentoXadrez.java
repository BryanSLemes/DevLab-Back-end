package com.bryanmzili.DevLab.games.xadrez;

import com.bryanmzili.DevLab.games.Jogada;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class MovimentoXadrez implements Jogada {

    private final int linhaOrigem;
    private final int colunaOrigem;
    private final int linhaDestino;
    private final int colunaDestino;

    public MovimentoXadrez(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        this.linhaOrigem = linhaOrigem;
        this.colunaOrigem = colunaOrigem;
        this.linhaDestino = linhaDestino;
        this.colunaDestino = colunaDestino;
    }

    @JsonCreator
    public MovimentoXadrez(
            @JsonProperty("idPosicaoOriginal") String idPosicaoOriginal,
            @JsonProperty("idPosicaoNova") String idPosicaoNova
    ) {
        int[] origem = parsePosicao(idPosicaoOriginal);
        int[] destino = parsePosicao(idPosicaoNova);
        this.linhaOrigem = origem[0];
        this.colunaOrigem = origem[1];
        this.linhaDestino = destino[0];
        this.colunaDestino = destino[1];
    }

    public int getLinhaOrigem() {
        return linhaOrigem;
    }

    public int getColunaOrigem() {
        return colunaOrigem;
    }

    public int getLinhaDestino() {
        return linhaDestino;
    }

    public int getColunaDestino() {
        return colunaDestino;
    }

    public int[] parsePosicao(String posicao) {
        posicao = posicao.toUpperCase();
        char colunaChar = posicao.charAt(0);
        int coluna = colunaChar - 'A';

        int linhaFront = Character.getNumericValue(posicao.charAt(1));
        int linhaConvertida = 8 - linhaFront;

        return new int[]{linhaConvertida, coluna};
    }

    public static String toIdPosicao(int linha, int coluna) {
        char colunaChar = (char) ('A' + coluna);
        int linhaFront = 8 - linha;
        return colunaChar + String.valueOf(linhaFront);
    }

    @Override
    public String toString() {
        return "Movimento: " + toIdPosicao(linhaOrigem, colunaOrigem)
                + " -> " + toIdPosicao(linhaDestino, colunaDestino);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MovimentoXadrez)) {
            return false;
        }
        MovimentoXadrez m = (MovimentoXadrez) o;
        return linhaOrigem == m.linhaOrigem
                && colunaOrigem == m.colunaOrigem
                && linhaDestino == m.linhaDestino
                && colunaDestino == m.colunaDestino;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
    }

}
