package br.com.tonypool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.tonypool.dto.ClienteCompletoDTO;
import br.com.tonypool.dto.ClienteDTO;
import br.com.tonypool.dto.EnderecoDTO;
import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Endereco;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.repositories.IEnderecoRepository;
import br.com.tonypool.requests.AtualizarClienteRequest;
import br.com.tonypool.security.TokenSecurity;

@RestController
@RequestMapping("/api/clientes")
public class ClienteConsultaController {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEnderecoRepository enderecoRepository;

    // Endpoint autenticado: retorna dados do cliente autenticado
    @GetMapping("/me")
    public ResponseEntity<ClienteCompletoDTO> consultarMeusDados(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            String cpf = TokenSecurity.getUserFromToken(token);
            Cliente cliente = clienteRepository.findByCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(404).build();
            }
            Endereco endereco = enderecoRepository.findByCliente(cliente);
            ClienteDTO clienteDTO = new ClienteDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getIs2FAEnabled()
            );
            EnderecoDTO enderecoDTO = new EnderecoDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep()
            );
            ClienteCompletoDTO resposta = new ClienteCompletoDTO(clienteDTO, enderecoDTO);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint autenticado: atualiza dados do cliente autenticado
    @PutMapping("/me")
    public ResponseEntity<String> atualizarMeusDados(
        @RequestHeader("Authorization") String authorization,
        @RequestBody AtualizarClienteRequest request
    ) {
        try {
            String token = authorization.replace("Bearer ", "");
            String cpf = TokenSecurity.getUserFromToken(token);
            Cliente cliente = clienteRepository.findByCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());
            clienteRepository.save(cliente);
            Endereco endereco = enderecoRepository.findByCliente(cliente);
            if (endereco != null) {
                endereco.setLogradouro(request.getLogradouro());
                endereco.setNumero(request.getNumero());
                endereco.setComplemento(request.getComplemento());
                endereco.setBairro(request.getBairro());
                endereco.setCidade(request.getCidade());
                endereco.setUf(request.getUf());
                endereco.setCep(request.getCep());
                enderecoRepository.save(endereco);
            }
            return ResponseEntity.ok("Dados atualizados com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar dados.");
        }
    }
}