package br.com.tonypool.responses;

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
public class ProfissionalDetalhadoResponse {
    private Integer idProfissional;
    private String nome;
    private String telefone;
    private List<ServicoResponse> servicos;
}
