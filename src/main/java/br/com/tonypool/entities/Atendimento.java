package br.com.tonypool.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "atendimento")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Atendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idatendimento")
	private Integer idAtendimento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datahora", nullable = false)
	private Date dataHora;

	@Column(name = "observacoes", length = 500, nullable = false)
	private String observacoes;

	@ManyToOne
	@JoinColumn(name = "idcliente", nullable = false)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "idservico", nullable = false)
	private Servico servico;
	
	@ManyToOne
	@JoinColumn(name = "idprofissional", nullable = false)
	private Profissional profissional;
}



