package br.com.tonypool.controllers;

import br.com.tonypool.dto.ClienteCompletoDTO;
import br.com.tonypool.dto.ClienteDTO;
import br.com.tonypool.dto.EnderecoDTO;
import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Endereco;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.repositories.IEnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteConsultaControllerTest {
    @Mock
    private IClienteRepository clienteRepository;
    @Mock
    private IEnderecoRepository enderecoRepository;
    @Mock
    private br.com.tonypool.security.TokenSecurity tokenSecurity;
    @InjectMocks
    private ClienteConsultaController controller;

    @Test
    void consultarMeusDados_sucesso() throws Exception {
        String token = "fake.jwt.token";
        String authorization = "Bearer " + token;
        String cpf = "12345678900";
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("Fulano");
        cliente.setCpf(cpf);
        cliente.setEmail("fulano@email.com");
        cliente.setTelefone("11999999999");
        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua A");
        endereco.setNumero("100");
        endereco.setComplemento("");
        endereco.setBairro("Centro");
        endereco.setCidade("Cidade");
        endereco.setUf("SP");

        when(tokenSecurity.getUserFromToken(token)).thenReturn(cpf);
        when(clienteRepository.findByCpf(cpf)).thenReturn(cliente);
        when(enderecoRepository.findByCliente(cliente)).thenReturn(endereco);

        ResponseEntity<ClienteCompletoDTO> response = controller.consultarMeusDados(authorization);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        ClienteDTO clienteDTO = response.getBody().getCliente();
        EnderecoDTO enderecoDTO = response.getBody().getEndereco();
        assertEquals("Fulano", clienteDTO.getNome());
        assertEquals("Rua A", enderecoDTO.getLogradouro());
    }

    @Test
    void consultarMeusDados_clienteNaoEncontrado() throws Exception {
        String token = "fake.jwt.token";
        String authorization = "Bearer " + token;
        String cpf = "12345678900";
        when(tokenSecurity.getUserFromToken(token)).thenReturn(cpf);
        when(clienteRepository.findByCpf(cpf)).thenReturn(null);
        ResponseEntity<ClienteCompletoDTO> response = controller.consultarMeusDados(authorization);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void consultarMeusDados_erroInterno() throws Exception {
        String token = "fake.jwt.token";
        String authorization = "Bearer " + token;
        String cpf = "12345678900";
        when(tokenSecurity.getUserFromToken(token)).thenReturn(cpf);
        when(clienteRepository.findByCpf(cpf)).thenThrow(new RuntimeException("Erro inesperado"));
        ResponseEntity<ClienteCompletoDTO> response = controller.consultarMeusDados(authorization);
        assertEquals(500, response.getStatusCodeValue());
    }
}