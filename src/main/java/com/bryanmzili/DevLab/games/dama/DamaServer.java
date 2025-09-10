package com.bryanmzili.DevLab.games.dama;

import com.bryanmzili.DevLab.SpringContext;
import com.bryanmzili.DevLab.data.Partida;
import com.bryanmzili.DevLab.games.GameServer;
import com.bryanmzili.DevLab.games.Jogada;
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

public class DamaServer extends GameServer {

    private final WebSocketSession jogador1;
    private final WebSocketSession jogador2;
    private WebSocketSession jogadorAtual;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PartidaService partidaService;
    private boolean finalizadoManual = false;
    private Instant ultimaAtividade = Instant.now();
    private final ScheduledExecutorService monitorInatividade = Executors.newSingleThreadScheduledExecutor();
    private final long TIMEOUT_SECONDS = 90;

    public DamaServer(WebSocketSession player1, WebSocketSession player2) {
        super(new Dama());
        this.jogador1 = player1;
        this.jogador2 = player2;
        this.jogadorAtual = player1; //O Jogador 1 começa o jogo
        this.partidaService = SpringContext.getBean(PartidaService.class);
        iniciarMonitoramentoInatividade();
    }

    @Override
    public void run() {
        try {
            iniciarJogo();
        } catch (IOException e) {
        }
    }

    @Override
    public void iniciarJogo() throws IOException {
        enviarMensagem(getJogador1(), "Jogo Iniciado!");
        enviarMensagem(getJogador2(), "Jogo Iniciado!");

        enviarMensagemParaAmbosJogadores(getGame().toString());

        enviarMensagem(jogadorAtual, "Sua vez.");
        enviarMensagem(jogadorAdversario(), "Vez do adversário");
    }

    @Override
    public void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        ultimaAtividade = Instant.now();

        int turno = ((Dama) getGame()).jogadorAtual(); // 1 = jogador1, 2 = jogador2
        jogadorAtual = (turno == 1) ? jogador1 : jogador2;

        if (sessao != jogadorAtual) {
            enviarMensagem(sessao, "Não é sua vez");
            return;
        }

        Jogada movimento = objectMapper.readValue(mensagem, MovimentoDama.class);

        if (getGame().isJogadaValida(movimento, turno)) {

            if (getGame().isJogoFinalizado()) {
                enviarMensagemParaAmbosJogadores(getGame().toString());
                finalizadoManual = true;

                int vencedor = getGame().getVencedor();

                switch (vencedor) {
                    case 1 -> {
                        enviarMensagem(jogador1, "Você venceu a partida");
                        enviarMensagem(jogador2, "Vitória do adversário");
                    }
                    case 2 -> {
                        enviarMensagem(jogador2, "Você venceu a partida");
                        enviarMensagem(jogador1, "Vitória do adversário");
                    }
                    default ->
                        enviarMensagemParaAmbosJogadores("Empate");
                }

                montarPartida(
                        (String) jogador1.getAttributes().get("idUsuario"),
                        (String) jogador2.getAttributes().get("idUsuario"),
                        vencedor,
                        "finalizada"
                );

                jogador1.close();
            } else {
                enviarMensagemParaAmbosJogadores(getGame().toString());
                turno = ((Dama) getGame()).jogadorAtual();
                jogadorAtual = (turno == 1) ? jogador1 : jogador2;
                enviarMensagem(jogadorAtual, "Sua vez.");
                enviarMensagem(jogadorAdversario(), "Vez do adversário");
            }
        } else {
            enviarMensagem(sessao, "Movimento inválido!");
        }
    }

    @Override
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

    @Override
    public void enviarMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        if (sessao.isOpen()) {
            sessao.sendMessage(new TextMessage(mensagem));
        }
    }

    @Override
    public void enviarMensagemParaAmbosJogadores(String mensagem) throws IOException {
        enviarMensagem(getJogador1(), mensagem);
        enviarMensagem(getJogador2(), mensagem);
    }

    public WebSocketSession getJogador1() {
        return jogador1;
    }

    public WebSocketSession getJogador2() {
        return jogador2;
    }

    private WebSocketSession jogadorAdversario() {
        return (jogadorAtual == getJogador1()) ? getJogador2() : getJogador1();
    }

    private void montarPartida(String idJogador1, String idJogador2, int vencedor, String status) {
        Partida partida = new Partida();

        partida.setJogador1(idJogador1);
        partida.setJogador2(idJogador2);
        partida.setData(LocalDateTime.now());
        partida.setStatus(status);
        partida.setJogo("Dama");

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

    private void iniciarMonitoramentoInatividade() {
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
