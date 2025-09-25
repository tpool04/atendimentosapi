package br.com.tonypool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClienteCompletoDTO {
    private ClienteDTO cliente;
    private EnderecoDTO endereco;
}
