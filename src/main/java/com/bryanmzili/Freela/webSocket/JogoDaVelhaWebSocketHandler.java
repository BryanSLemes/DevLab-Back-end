package com.bryanmzili.Freela.webSocket;

import com.bryanmzili.Freela.games.jogo_da_velha.JogoDaVelhaServer;
import java.util.Map;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JogoDaVelhaWebSocketHandler extends TextWebSocketHandler {

    private final Queue<WebSocketSession> esperaPorJogadores = new ConcurrentLinkedQueue<>();
    private final Map<WebSocketSession, JogoDaVelhaServer> jogosEmExecucao = new ConcurrentHashMap<>();
    private final ExecutorService executarGames = Executors.newCachedThreadPool();

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) throws Exception {
        esperaPorJogadores.add(sessao);
        verificarAndIniciarJogo();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        esperaPorJogadores.remove(session);
        JogoDaVelhaServer game = jogosEmExecucao.remove(session);
        if (game != null) {
            WebSocketSession otherPlayer = (game.getJogador1() == session) ? game.getJogador2() : game.getJogador1();
            jogosEmExecucao.remove(otherPlayer);
            game.lidarComJogadorDesconectado(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession sessao, TextMessage mensagem) throws Exception {
        JogoDaVelhaServer jogo = jogosEmExecucao.get(sessao);
        if (jogo != null) {
            jogo.lidarComMensagem(sessao, mensagem.getPayload());
        }
    }

    private void verificarAndIniciarJogo() {
        if (esperaPorJogadores.size() >= 2) {
            WebSocketSession jogador1 = esperaPorJogadores.poll();
            WebSocketSession jogador2 = esperaPorJogadores.poll();

            JogoDaVelhaServer game = new JogoDaVelhaServer(jogador1, jogador2);
            jogosEmExecucao.put(jogador1, game);
            jogosEmExecucao.put(jogador2, game);

            executarGames.execute(game);
        }
    }
}
