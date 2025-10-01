package br.com.tonypool.requests;

import java.util.List;
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
public class ProfissionalPutRequest {
    private String nome;
    private String telefone;
    private String email;
    private String especialidade;
    private List<ServicoPutRequest> servicos;
}
