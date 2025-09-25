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

    // Consulta protegida: retorna dados do cliente logado
    @GetMapping("/me")
    public ResponseEntity<ClienteCompletoDTO> consultarMeusDados(@RequestHeader("Authorization") String authHeader) {
        try {
            String cpf = TokenSecurity.getUserFromToken(authHeader.replace("Bearer ", ""));
            Cliente cliente = clienteRepository.findByCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(401).build();
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
            return ResponseEntity.status(401).build();
        }
    }

    // Atualização protegida: altera dados do cliente logado
    @PutMapping("/me")
    public ResponseEntity<String> atualizarMeusDados(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody AtualizarClienteRequest request
    ) {
        try {
            String cpf = TokenSecurity.getUserFromToken(authHeader.replace("Bearer ", ""));
            Cliente cliente = clienteRepository.findByCpf(cpf);
            if (cliente == null) {
                return ResponseEntity.status(401).body("Cliente não encontrado.");
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
            return ResponseEntity.status(401).body("Token inválido ou expirado.");
        }
    }
}