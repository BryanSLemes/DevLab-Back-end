package com.bryanmzili.DevLab.games;

import com.bryanmzili.DevLab.SpringContext;
import com.bryanmzili.DevLab.data.Partida;
import com.bryanmzili.DevLab.service.PartidaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public abstract class GameServer extends Thread {

    protected final WebSocketSession jogador1;
    protected final WebSocketSession jogador2;
    protected WebSocketSession jogadorAtual;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected final PartidaService partidaService;
    protected boolean finalizadoManual = false;
    protected Instant ultimaAtividade = Instant.now();
    protected final ScheduledExecutorService monitorInatividade = Executors.newSingleThreadScheduledExecutor();
    protected final long TIMEOUT_SECONDS = 90;
    protected String nomeJogo;

    protected final Game game;

    public GameServer(Game game, WebSocketSession player1, WebSocketSession player2, String nomeJogo) {
        this.game = game;
        this.jogador1 = player1;
        this.jogador2 = player2;
        this.jogadorAtual = player1; //O Jogador 1 começa o jogo
        this.partidaService = SpringContext.getBean(PartidaService.class);
        this.nomeJogo = nomeJogo;
    }

    @Override
    public void run() {
        try {
            iniciarJogo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void iniciarJogo() throws IOException {
        enviarMensagem(getJogador1(), "Jogo Iniciado!");
        enviarMensagem(getJogador2(), "Jogo Iniciado!");

        enviarMensagemParaAmbosJogadores(getGame().toString());

        enviarMensagem(jogadorAtual, "Sua vez.");
        enviarMensagem(jogadorAdversario(), "Vez do adversário");
    }

    public abstract void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException;

    public void lidarComJogadorDesconectado(WebSocketSession session) throws IOException {
        if (finalizadoManual) {
            return; // Evita lógica dupla
        }
        WebSocketSession outroJogador = (session == getJogador1()) ? getJogador2() : getJogador1();
        if (outroJogador.isOpen()) {
            enviarMensagem(outroJogador, "O outro jogador se desconectou. Fim da Partida.");
            montarPartida(
                    (String) jogador1.getAttributes().get("idUsuario"),
                    (String) jogador2.getAttributes().get("idUsuario"),
                    (outroJogador == getJogador1()) ? 1 : 2,
                    "desistência"
            );
            outroJogador.close();
        }
    }

    protected void enviarMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        if (sessao.isOpen()) {
            sessao.sendMessage(new TextMessage(mensagem));
        }
    }

    protected void enviarMensagemParaAmbosJogadores(String mensagem) throws IOException {
        enviarMensagem(getJogador1(), mensagem);
        enviarMensagem(getJogador2(), mensagem);
    }

    protected Game getGame() {
        return game;
    }

    public WebSocketSession getJogador1() {
        return jogador1;
    }

    public WebSocketSession getJogador2() {
        return jogador2;
    }

    protected WebSocketSession jogadorAdversario() {
        return (jogadorAtual == getJogador1()) ? getJogador2() : getJogador1();
    }

    protected void montarPartida(String idJogador1, String idJogador2, int vencedor, String status) {
        Partida partida = new Partida();

        partida.setJogador1(idJogador1);
        partida.setJogador2(idJogador2);
        partida.setData(LocalDateTime.now());
        partida.setStatus(status);
        partida.setJogo(nomeJogo);

        switch (vencedor) {
            case 1 ->
                partida.setVencedor(idJogador1);
            case 2 ->
                partida.setVencedor(idJogador2);
            default ->
                partida.setVencedor("Empate");
        }

        partidaService.salvarPartida(partida);
    }

    protected void iniciarMonitoramentoInatividade() {
        monitorInatividade.scheduleAtFixedRate(() -> {
            try {
                if (Duration.between(ultimaAtividade, Instant.now()).getSeconds() > TIMEOUT_SECONDS) {
                    if (jogadorAtual != null && jogadorAtual.isOpen()) {
                        enviarMensagem(jogadorAtual, "Você foi desconectado por inatividade.");
                        jogadorAtual.close();
                    }
                }
            } catch (IOException e) {
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

}
