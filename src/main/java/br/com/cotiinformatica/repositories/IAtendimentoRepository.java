package br.com.cotiinformatica.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.entities.Atendimento;

public interface IAtendimentoRepository extends CrudRepository<Atendimento, Integer> {

	@Query("select a from Atendimento a join a.servico join a.profissional where a.cliente.idCliente = :param1")
	public List<Atendimento> findByCliente(@Param("param1") Integer idCliente) throws Exception;

}



