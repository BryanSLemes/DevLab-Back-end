package com.bryanmzili.DevLab.data;


public class PartidaViewDTO {

    private String jogador1; 
    private String jogador2;
    private String vencedor;
    private Object data;
    private String status;

    public PartidaViewDTO() {}

    public String getJogador1() {return jogador1;}

    public void setJogador1(String jogador1) {this.jogador1 = jogador1;}

    public String getJogador2() {return jogador2;}

    public void setJogador2(String jogador2) {this.jogador2 = jogador2;}

    public String getVencedor() {return vencedor;}

    public void setVencedor(String vencedor) {this.vencedor = vencedor;}

    public Object getData() {return data;}

    public void setData(String data) {this.data = data;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}
}
