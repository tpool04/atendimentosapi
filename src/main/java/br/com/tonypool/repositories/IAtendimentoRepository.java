package br.com.tonypool.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.tonypool.entities.Atendimento;

public interface IAtendimentoRepository extends CrudRepository<Atendimento, Integer> {

	@Query("select a from Atendimento a join a.servico join a.profissional where a.cliente.idCliente = :param1")
	public List<Atendimento> findByCliente(@Param("param1") Integer idCliente) throws Exception;

	@Query("select a from Atendimento a where a.servico.idServico = :idServico")
	List<Atendimento> findByServico(@Param("idServico") Integer idServico);

}