package br.com.cotiinformatica.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cliente")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcliente")
	private Integer idCliente;

	@Column(name = "nome", length = 150, nullable = false)
	private String nome;

	@Column(name = "cpf", length = 14, nullable = false, unique = true)
	private String cpf;

	@Column(name = "senha", length = 40, nullable = false)
	private String senha;

	@Column(name = "email", length = 100, nullable = false, unique = true)
	private String email;

	@Column(name = "telefone", length = 16, nullable = false)
	private String telefone;

	@OneToOne(mappedBy = "cliente")
	private Endereco endereco;

	@OneToMany(mappedBy = "cliente")
	private List<Atendimento> atendimentos;
}
