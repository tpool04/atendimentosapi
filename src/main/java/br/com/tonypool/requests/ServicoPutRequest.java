package br.com.tonypool.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServicoPutRequest {
    private Integer idServico;
    private String nome;
    private String descricao;
    private Double valor;
}