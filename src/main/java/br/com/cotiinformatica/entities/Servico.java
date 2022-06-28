package br.com.cotiinformatica.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "servico")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Servico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idservico")
	private Integer idServico;

	@Column(name = "nome", length = 150, nullable = false)
	private String nome;

	@Column(name = "preco", nullable = false)
	private Double preco;

	@ManyToMany
	@JoinTable(
			// nome da tabela associativa
			name = "servico_profissional",
			// foreign key para a tabela de serviço
			joinColumns = @JoinColumn(name = "idservico", nullable = false),
			// foreign key para a tabela de profissional
			inverseJoinColumns = @JoinColumn(name = "idprofissional", nullable = false))
	private List<Profissional> profissionais;
	
	@OneToMany(mappedBy = "servico")
	private List<Atendimento> atendimentos;
}



