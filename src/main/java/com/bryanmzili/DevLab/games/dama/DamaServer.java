package com.bryanmzili.DevLab.games.dama;

import com.bryanmzili.DevLab.games.GameServer;
import com.bryanmzili.DevLab.games.Jogada;
import java.io.IOException;
import java.time.Instant;
import org.springframework.web.socket.WebSocketSession;

public class DamaServer extends GameServer {

    public DamaServer(WebSocketSession player1, WebSocketSession player2) {
        super(new Dama(), player1, player2, "Dama");
        iniciarMonitoramentoInatividade();
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

}
