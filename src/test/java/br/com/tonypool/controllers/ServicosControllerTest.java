package br.com.tonypool.controllers;

import br.com.tonypool.entities.Servico;
import br.com.tonypool.repositories.IServicoRepository;
import br.com.tonypool.requests.ServicoCreateRequest;
import br.com.tonypool.responses.ServicoGetResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.context.annotation.Import;
import br.com.tonypool.controllers.TestSecurityConfig;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicosController.class)
@Import(TestSecurityConfig.class)
public class ServicosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IServicoRepository servicoRepository;

    @Test
    void testUpdateServicoSuccess() throws Exception {
        Servico servico = new Servico();
        servico.setIdServico(1);
        servico.setNome("Corte");
        servico.setValor(50.0);
        servico.setProfissionais(new java.util.ArrayList<>());
        Mockito.when(servicoRepository.findById(1)).thenReturn(Optional.of(servico));
        Mockito.when(servicoRepository.save(Mockito.any(Servico.class))).thenReturn(servico);

        String json = "{\"nome\":\"Corte Atualizado\",\"valor\":60.0}";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/servicos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Corte Atualizado"))
                .andExpect(jsonPath("$.valor").value(60.0));
    }

    @Test
    void testUpdateServicoNotFound() throws Exception {
        Mockito.when(servicoRepository.findById(99)).thenReturn(Optional.empty());
        String json = "{\"nome\":\"Inexistente\",\"valor\":10.0}";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/servicos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllServicos() throws Exception {
        Servico servico1 = new Servico();
        servico1.setIdServico(1);
        servico1.setNome("Corte");
        servico1.setValor(50.0);
        servico1.setProfissionais(new java.util.ArrayList<>());
        Servico servico2 = new Servico();
        servico2.setIdServico(2);
        servico2.setNome("Barba");
        servico2.setValor(30.0);
        servico2.setProfissionais(new java.util.ArrayList<>());
        Mockito.when(servicoRepository.findAll()).thenReturn(Arrays.asList(servico1, servico2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/servicos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Corte"))
                .andExpect(jsonPath("$[1].nome").value("Barba"));
    }
}