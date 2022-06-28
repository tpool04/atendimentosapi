package br.com.cotiinformatica.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.entities.Cliente;

public interface IClienteRepository extends CrudRepository<Cliente, Integer> {

	@Query("select c from Cliente c where c.cpf = :param1")
	public Cliente findByCpf(@Param("param1") String cpf) throws Exception;

	@Query("select c from Cliente c where c.email = :param1")
	public Cliente findByEmail(@Param("param1") String email) throws Exception;
	
	@Query("select c from Cliente c where c.cpf = :param1 and c.senha = :param2")
	public Cliente findByCpfAndSenha(@Param("param1") String cpf, @Param("param2") String senha) throws Exception;
}

