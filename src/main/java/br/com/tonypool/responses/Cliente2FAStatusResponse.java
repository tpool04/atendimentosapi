package br.com.tonypool.responses;

public class Cliente2FAStatusResponse {
    private Integer idCliente;
    private String nome;
    private Boolean is2FAEnabled;

    public Cliente2FAStatusResponse(Integer idCliente, String nome, Boolean is2FAEnabled) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.is2FAEnabled = is2FAEnabled;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getIs2FAEnabled() {
        return is2FAEnabled;
    }

    public void setIs2FAEnabled(Boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
    }
}
