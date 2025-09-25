package br.com.tonypool.responses;

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
public class ProfissionalGetResponse {

	private Integer idProfissional;
	private String nome;
	private String telefone;
}
