package br.com.tonypool.controllers;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Endereco;
import br.com.tonypool.helpers.MD5Helper;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.repositories.IEnderecoRepository;
import br.com.tonypool.repositories.IPerfilRepository;
import br.com.tonypool.requests.CriarContaPostRequest;
import io.swagger.annotations.ApiOperation;

@Transactional
@RestController
@RequestMapping("/api")
public class CriarContaController {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEnderecoRepository enderecoRepository;

    @Autowired
    private IPerfilRepository perfilRepository;

    @CrossOrigin
    @ApiOperation("Endpoint para cadastro de conta do cliente.")
    @PostMapping("/criar-conta")
    public ResponseEntity<String> post(@RequestBody CriarContaPostRequest request) {
        try {
            // verificar se o cpf informado já está cadastrado no banco de dados
            if (clienteRepository.findByCpf(request.getCpf()) != null)
                throw new IllegalArgumentException("O Cpf informado já está cadastrado no sistema, tente outro.");

            // verificar se o email informado já está cadastrado no banco de dados
            if (clienteRepository.findByEmail(request.getEmail()) != null)
                throw new IllegalArgumentException("O Email informado já está cadastrado no sistema, tente outro.");

            // capturando os dados do cliente
            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setCpf(request.getCpf());
            cliente.setSenha(MD5Helper.encrypt(request.getSenha()));
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());

            // capturando os dados do endereco
            Endereco endereco = new Endereco();
            endereco.setLogradouro(request.getLogradouro());
            endereco.setNumero(request.getNumero());
            endereco.setComplemento(request.getComplemento());
            endereco.setBairro(request.getBairro());
            endereco.setCidade(request.getCidade());
            endereco.setUf(request.getUf());
            endereco.setCep(request.getCep());

            // cadastrando o cliente
            if (cliente.getPerfil() == null) {
                cliente.setPerfil(perfilRepository.findById(2).orElseThrow(() -> new RuntimeException("Perfil USER (id=2) não encontrado.")));
            }
            clienteRepository.save(cliente);

            // relacionar o cliente com o endereço
            endereco.setCliente(cliente);

            // cadastrando o endereço
            enderecoRepository.save(endereco);

            // HTTP 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Parabéns! Sua conta de cliente foi criada com sucesso.");
        } catch (IllegalArgumentException e) {
            // HTTP 400 (CLIENT ERROR) -> BAD REQUEST
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            // HTTP 500 (SERVER ERROR) -> INTERNAL SERVER ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro interno. Tente novamente mais tarde.");
        }
    }
}