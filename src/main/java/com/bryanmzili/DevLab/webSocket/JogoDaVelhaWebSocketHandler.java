package com.bryanmzili.DevLab.webSocket;

import com.bryanmzili.DevLab.SpringContext;
import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.games.jogo_da_velha.JogoDaVelhaServer;
import com.bryanmzili.DevLab.service.UsuarioService;
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
        setJogadorOffline(session);
        if (game != null) {
            WebSocketSession otherPlayer = (game.getJogador1() == session) ? game.getJogador2() : game.getJogador1();
            jogosEmExecucao.remove(otherPlayer);
            setJogadorOffline(otherPlayer);
            game.lidarComJogadorDesconectado(session);
            game.interrupt();  
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
    
    private void setJogadorOffline(WebSocketSession session) {
    Object idObj = session.getAttributes().get("idUsuario");
    UsuarioService usuarioService = SpringContext.getBean(UsuarioService.class);
    if (idObj instanceof String id) {
        Usuario usuario = usuarioService.listarUsuarioById(id);
        if (usuario != null) {
            usuario.setOnline(false);
            usuarioService.atualizarUsuario(usuario);
        }
    }
}

}
