package com.bryanmzili.DevLab.games.xadrez;

import com.bryanmzili.DevLab.games.GameServer;
import com.bryanmzili.DevLab.games.Jogada;
import java.io.IOException;
import java.time.Instant;
import org.springframework.web.socket.WebSocketSession;

public class XadrezServer extends GameServer {

    public XadrezServer(WebSocketSession player1, WebSocketSession player2) {
        super(new Xadrez(), player1, player2, "Xadrez");
        iniciarMonitoramentoInatividade();
    }

    @Override
    public void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        ultimaAtividade = Instant.now();

        int turno = ((Xadrez) getGame()).jogadorAtual(); // 1 = jogador1, 2 = jogador2
        jogadorAtual = (turno == 1) ? jogador1 : jogador2;

        if (sessao != jogadorAtual) {
            enviarMensagem(sessao, "Não é sua vez");
            return;
        }

        Jogada movimento = objectMapper.readValue(mensagem, MovimentoXadrez.class);

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
                turno = ((Xadrez) getGame()).jogadorAtual();
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

}
