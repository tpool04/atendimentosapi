package br.com.tonypool.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilResponse {
    private Integer idCliente;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String perfilNome;
}
