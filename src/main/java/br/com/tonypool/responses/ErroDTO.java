package br.com.tonypool.responses;

import java.util.List;

public class ErroDTO {
    private int status;
    private String mensagem;
    private List<String> erros;

    public ErroDTO(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public ErroDTO(int status, String mensagem, List<String> erros) {
        this.status = status;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<String> getErros() {
        return erros;
    }
}
