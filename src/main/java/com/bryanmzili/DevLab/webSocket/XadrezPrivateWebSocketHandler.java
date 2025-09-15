package com.bryanmzili.DevLab.webSocket;

import com.bryanmzili.DevLab.SpringContext;
import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.games.xadrez.XadrezServer;
import com.bryanmzili.DevLab.service.UsuarioService;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class XadrezPrivateWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, XadrezServer> jogosEmExecucao = new ConcurrentHashMap<>();
    private final ExecutorService executarGames = Executors.newCachedThreadPool();
    private final Map<String, WebSocketSession> codigosDeConvite = new ConcurrentHashMap<>();
    private final ScheduledExecutorService expirarCodigos = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) throws Exception {
        String codigo = obterCodigoDaQuery(sessao);

        if (codigo != null && codigosDeConvite.containsKey(codigo)) {
            WebSocketSession jogador1 = codigosDeConvite.remove(codigo);
            if (jogador1 != null && jogador1.isOpen()) {
                iniciarJogo(jogador1, sessao);
                return;
            }

            sessao.sendMessage(new TextMessage("Código expirado ou inválido."));
            sessao.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        if (codigo != null) {
            sessao.sendMessage(new TextMessage("Código inválido ou já utilizado."));
            sessao.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String novoCodigo = gerarCodigoUnico();
        codigosDeConvite.put(novoCodigo, sessao);

        sessao.sendMessage(new TextMessage("codigo-gerado: " + novoCodigo));

        expirarCodigos.schedule(() -> {
            codigosDeConvite.remove(novoCodigo);
        }, 5, TimeUnit.MINUTES);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        codigosDeConvite.values().remove(session);
        XadrezServer game = jogosEmExecucao.remove(session);
        setJogadorOffline(session);

        if (game != null) {
            WebSocketSession outroJogador = (game.getJogador1() == session) ? game.getJogador2() : game.getJogador1();
            jogosEmExecucao.remove(outroJogador);
            setJogadorOffline(outroJogador);
            game.lidarComJogadorDesconectado(session);
            game.interrupt();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession sessao, TextMessage mensagem) throws Exception {
        XadrezServer jogo = jogosEmExecucao.get(sessao);
        if (jogo != null) {
            jogo.lidarComMensagem(sessao, mensagem.getPayload());
        }
    }

    private void iniciarJogo(WebSocketSession jogador1, WebSocketSession jogador2) {
        XadrezServer game = new XadrezServer(jogador1, jogador2);
        jogosEmExecucao.put(jogador1, game);
        jogosEmExecucao.put(jogador2, game);
        executarGames.execute(game);
    }

    private String obterCodigoDaQuery(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            for (String param : uri.getQuery().split("&")) {
                String[] partes = param.split("=");
                if (partes.length == 2 && partes[0].equals("codigo")) {
                    return partes[1];
                }
            }
        }
        return null;
    }

    private String gerarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (codigosDeConvite.containsKey(codigo));
        return codigo;
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
