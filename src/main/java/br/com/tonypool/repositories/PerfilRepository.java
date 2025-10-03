package br.com.tonypool.repositories;

import br.com.tonypool.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    boolean existsByNome(String nome);
}
