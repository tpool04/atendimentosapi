package br.com.tonypool.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.tonypool.entities.Perfil;

public interface IPerfilRepository extends JpaRepository<Perfil, Integer> {
}
