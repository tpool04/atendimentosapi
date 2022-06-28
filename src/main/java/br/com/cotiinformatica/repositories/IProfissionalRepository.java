package br.com.cotiinformatica.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.entities.Profissional;

public interface IProfissionalRepository extends CrudRepository<Profissional, Integer> {

	@Query("select p from Profissional p join p.servicos where p.idProfissional = :param1")
	Optional<Profissional> findById(@Param("param1") Integer id);

}



