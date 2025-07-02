package com.bryanmzili.DevLab.games;

import java.io.IOException;
import org.springframework.web.socket.WebSocketSession;

public abstract class GameServer extends Thread {

    private final Game game;

    public GameServer(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            iniciarJogo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void iniciarJogo() throws IOException;

    public abstract void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException;

    public abstract void lidarComJogadorDesconectado(WebSocketSession session) throws IOException;

    public abstract void enviarMensagem(WebSocketSession sessao, String mensagem) throws IOException;

    public abstract void enviarMensagemParaAmbosJogadores(String mensagem) throws IOException;

    public Game getGame() {
        return game;
    }

}
