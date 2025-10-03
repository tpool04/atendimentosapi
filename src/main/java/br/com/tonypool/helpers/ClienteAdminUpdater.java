package br.com.tonypool.helpers;

import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Perfil;
import br.com.tonypool.repositories.ClienteRepository;
import br.com.tonypool.repositories.PerfilRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ClienteAdminUpdater implements CommandLineRunner {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public void run(String... args) {
        clienteRepository.findByCpf("04259983717").ifPresent(cliente -> {
            if (cliente.getPerfil() == null || cliente.getPerfil().getId() != 1) {
                Perfil adminPerfil = perfilRepository.findById(1).orElse(null);
                if (adminPerfil != null) {
                    cliente.setPerfil(adminPerfil);
                    clienteRepository.save(cliente);
                }
            }
        });
    }
}
