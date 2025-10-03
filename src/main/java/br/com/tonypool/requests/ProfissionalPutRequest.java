package br.com.tonypool.requests;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Schema(description = "Dados para atualização de um profissional")
public class ProfissionalPutRequest {

    @Schema(example = "Paula do PP", description = "Nome completo do profissional")
    private String nome;

    @Schema(example = "21986532144", description = "Telefone do profissional (apenas números)")
    private String telefone;

    @Schema(example = "paula.pp@exemplo.com", description = "E-mail do profissional")
    private String email;

    @Schema(example = "Estética facial", description = "Especialidade do profissional")
    private String especialidade;

    @ArraySchema(arraySchema = @Schema(description = "Lista de serviços vinculados ao profissional"))
    private List<ServicoPutRequest> servicos;
}
