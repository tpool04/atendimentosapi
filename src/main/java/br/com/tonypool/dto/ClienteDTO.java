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

public class ClienteDTO {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private Boolean is2FAEnabled;


}