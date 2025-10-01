package br.com.tonypool.requests;

import java.util.List;

public class ProfissionalCreateRequest {
    private String nome;
    private String telefone;
    private List<ServicoPutRequest> servicos;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public List<ServicoPutRequest> getServicos() { return servicos; }
    public void setServicos(List<ServicoPutRequest> servicos) { this.servicos = servicos; }
}
