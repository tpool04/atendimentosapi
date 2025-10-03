package br.com.tonypool.controllers;

import br.com.tonypool.entities.Profissional;
import br.com.tonypool.repositories.IProfissionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfissionaisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IProfissionalRepository profissionalRepository;

    @Autowired
    private br.com.tonypool.repositories.IServicoRepository servicoRepository;

    private Profissional profissional;

    @BeforeEach
    @Transactional
    void setUp() {
        // Remove vínculos do profissional com serviços usando fetch join
        for (Profissional p : profissionalRepository.findAll()) {
            for (br.com.tonypool.entities.Servico s : servicoRepository.findAllWithProfissionais()) {
                if (s.getProfissionais() != null && s.getProfissionais().contains(p)) {
                    s.getProfissionais().remove(p);
                    servicoRepository.save(s);
                }
            }
            // Atualiza o profissional para garantir que não está mais vinculado
            p.setServicos(null);
            profissionalRepository.save(p);
        }
        profissionalRepository.deleteAll();
        profissional = new Profissional();
        profissional.setNome("Teste");
        profissional.setTelefone("999999999");
        profissional.setServicos(null); // Garante que não há vínculos
        profissionalRepository.save(profissional);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirProfissionalExistente() throws Exception {
        Integer id = profissional.getIdProfissional();
        ResultActions result = mockMvc.perform(delete("/api/profissionais/" + id)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
        assertThat(profissionalRepository.findById(id)).isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundParaProfissionalInexistente() throws Exception {
        Integer idInexistente = 99999;
        ResultActions result = mockMvc.perform(delete("/api/profissionais/" + idInexistente)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}