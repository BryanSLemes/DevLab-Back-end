package com.bryanmzili.Freela.games.jogo_da_velha;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;

public class JogoDaVelhaServer implements Runnable {

    private final WebSocketSession jogador1;
    private final WebSocketSession jogador2;
    private WebSocketSession jogadorAtual;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private JogoDaVelha jogoDaVelha = new JogoDaVelha();

    public JogoDaVelhaServer(WebSocketSession player1, WebSocketSession player2) {
        this.jogador1 = player1;
        this.jogador2 = player2;
        this.jogadorAtual = player1; //O Jogador 1 começa o jogo
    }

    @Override
    public void run() {
        try {
            iniciarJogo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarJogo() throws IOException {
        enviarMensagem(getJogador1(), "Jogo Iniciado! Você está jogando contra:" + getJogador2().getId() + "\nSeu ID: " + getJogador1().getId());
        enviarMensagem(getJogador2(), "Jogo Iniciado! Você está jogando contra:" + getJogador1().getId() + "\nSeu ID: " + getJogador2().getId());

        enviarMensagemParaAmbosJogadores(jogoDaVelha.toString());

        enviarMensagem(jogadorAtual, "Sua vez.");
    }

    public void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        if (sessao != jogadorAtual) {
            enviarMensagem(sessao, "Não é sua vez");
            return;
        }

        Movimento movimento = objectMapper.readValue(mensagem, Movimento.class);

        if (jogoDaVelha.isMovimentoValido(movimento, ((jogadorAtual == getJogador1()) ? 1 : 2))) {
            if (jogoDaVelha.isJogoFinalizado()) {
                enviarMensagemParaAmbosJogadores(jogoDaVelha.getVencedor());
                jogador1.close();
            } else {
                enviarMensagemParaAmbosJogadores(jogoDaVelha.toString());

                jogadorAtual = (jogadorAtual == getJogador1()) ? getJogador2() : getJogador1();
                enviarMensagem(jogadorAtual, "Sua vez.");
            }
        } else {
            enviarMensagem(sessao, "Movimento inválido!");
        }
    }

    public void lidarComJogadorDesconectado(WebSocketSession session) throws IOException {
        WebSocketSession outroJogador = (session == getJogador1()) ? getJogador2() : getJogador1();
        if (outroJogador.isOpen()) {
            enviarMensagem(outroJogador, "O outro jogador se desconectou. Fim da Partida.");
            outroJogador.close();
        }
    }

    private void enviarMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        if (sessao.isOpen()) {
            sessao.sendMessage(new TextMessage(mensagem));
        }
    }

    private void enviarMensagemParaAmbosJogadores(String mensagem) throws IOException {
        enviarMensagem(getJogador1(), mensagem);
        enviarMensagem(getJogador2(), mensagem);
    }

    public WebSocketSession getJogador1() {
        return jogador1;
    }

    public WebSocketSession getJogador2() {
        return jogador2;
    }
}
