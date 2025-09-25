package br.com.tonypool.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Endereco;

public interface IEnderecoRepository extends CrudRepository<Endereco, Integer>{
	@Query("SELECT e FROM Endereco e WHERE e.cliente = :cliente")
    Endereco findByCliente(@Param("cliente") Cliente cliente);

}

