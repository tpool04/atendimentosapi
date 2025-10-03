package br.com.tonypool.helpers;

import br.com.tonypool.entities.Perfil;
import br.com.tonypool.repositories.PerfilRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class PerfilSeeder implements CommandLineRunner {
    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public void run(String... args) {
        if (perfilRepository.count() == 0) {
            Perfil admin = new Perfil();
            admin.setId(1);
            admin.setNome("ADMIN");
            admin.setDescricao("Administrador do Sistema");
            perfilRepository.save(admin);

            Perfil user = new Perfil();
            user.setId(2);
            user.setNome("USER");
            user.setDescricao("Usuario do Sistema");
            perfilRepository.save(user);
        }
    }
}