package br.com.tonypool.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "profissional")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Profissional {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idprofissional")
	private Integer idProfissional;

	@Column(name = "nome", length = 150, nullable = false)
	private String nome;

	@Column(name = "telefone", length = 16, nullable = false, unique = true)
	private String telefone;

	@ManyToMany(mappedBy = "profissionais")
	private List<Servico> servicos;
	
	@OneToMany(mappedBy = "profissional")
	private List<Atendimento> atendimentos;
}



