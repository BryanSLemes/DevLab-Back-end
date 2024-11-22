package com.bryanmzili.DevLab.games.domino;

import com.bryanmzili.DevLab.games.GameServer;
import java.io.IOException;
import org.springframework.web.socket.WebSocketSession;

public class DominoServer extends GameServer {
    
    public DominoServer(WebSocketSession player1, WebSocketSession player2) {
        super(new Domino());
//        this.jogador1 = player1;
//        this.jogador2 = player2;
//        this.jogadorAtual = player1; //O Jogador 1 começa o jogo
    }

    @Override
    public void iniciarJogo() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void lidarComMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void lidarComJogadorDesconectado(WebSocketSession session) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void enviarMensagem(WebSocketSession sessao, String mensagem) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void enviarMensagemParaAmbosJogadores(String mensagem) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
