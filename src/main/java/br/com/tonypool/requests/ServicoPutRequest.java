package br.com.tonypool.requests;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Serviço vinculado ao profissional")
public class ServicoPutRequest {

    @Schema(example = "4", description = "ID do serviço já existente no sistema")
    private Integer idServico;

    @Schema(example = "Limpeza de pele", description = "Nome do serviço")
    private String nome;

    @Schema(example = "Procedimento estético para remoção de impurezas da pele", description = "Descrição detalhada do serviço")
    private String descricao;

    @Schema(example = "120.0", description = "Valor do serviço para este profissional")
    private Double valor;
}
