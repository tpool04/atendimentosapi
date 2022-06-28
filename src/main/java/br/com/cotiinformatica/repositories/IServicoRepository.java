package br.com.cotiinformatica.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import br.com.cotiinformatica.entities.Servico;

public interface IServicoRepository extends CrudRepository<Servico, Integer> {

	@Query("select distinct s from Servico s join s.profissionais order by s.idServico")
	public List<Servico> findAll();
	
}

