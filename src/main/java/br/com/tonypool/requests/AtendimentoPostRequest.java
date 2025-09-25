package br.com.tonypool.requests;

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
public class AtendimentoPostRequest {

	private Integer idServico;
	private Integer idProfissional;
	private String data;
	private String hora;
	private String observacoes;
}


