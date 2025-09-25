package br.com.tonypool.dto;


public class AcessarContaPostDTO {

    private String token;
    private String nome;
    private Integer idCliente;
    private Boolean verified;
    private String perfil;

    public AcessarContaPostDTO() {}

    public AcessarContaPostDTO(String token, String nome, Integer idCliente, Boolean verified, String perfil) {
        this.token = token;
        this.nome = nome;
        this.idCliente = idCliente;
        this.verified = verified;
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}