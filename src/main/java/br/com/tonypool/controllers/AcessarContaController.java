package br.com.tonypool.controllers;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.tonypool.dto.AcessarContaPostDTO;
import br.com.tonypool.entities.Cliente;
import br.com.tonypool.helpers.MD5Helper;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.requests.AcessarContaPostRequest;
import br.com.tonypool.requests.Confirmar2FARequest;
import br.com.tonypool.responses.ClientePerfilResponse;
import br.com.tonypool.security.TokenSecurity;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transactional
@Controller
public class AcessarContaController {

	private static final Logger logger = LoggerFactory.getLogger(AcessarContaController.class);

	@Autowired
	private IClienteRepository clienteRepository;
	
	@ApiOperation("Endpoint para autenticação de clientes.")
	@PostMapping("/api/acessar-conta")
	public ResponseEntity<AcessarContaPostDTO> post(@RequestBody AcessarContaPostRequest request){
		try {
			
			Cliente cliente = clienteRepository.findByCpfAndSenha
					(request.getCpf(), MD5Helper.encrypt(request.getSenha()));
			
			if(cliente == null) //Se o cliente NÃO foi encontrado
				throw new IllegalArgumentException("Acesso negado, cpf e senha inválidos.");
			
			// Verifica se 2FA está ativado
			if (Boolean.TRUE.equals(cliente.getIs2FAEnabled())) {
                AcessarContaPostDTO dto = new AcessarContaPostDTO(null, "2FA necessário. Use o ID: " + cliente.getIdCliente(), cliente.getIdCliente(), false, cliente.getPerfil() != null ? cliente.getPerfil().getNome() : null);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
            }
			
			//HTTP 200 (OK)
			//return ResponseEntity.status(HttpStatus.OK)
					//.body(TokenSecurity.generateToken(cliente.getCpf()));
			
			String perfilNome = cliente.getPerfil() != null ? cliente.getPerfil().getNome() : null;
            String token = TokenSecurity.generateToken(cliente.getCpf(), cliente.getIdCliente());
            AcessarContaPostDTO dto = new AcessarContaPostDTO(token, cliente.getNome(), cliente.getIdCliente(), true, perfilNome);

	        return ResponseEntity.status(HttpStatus.OK).body(dto);
		}
		catch (IllegalArgumentException e) {
            AcessarContaPostDTO erro = new AcessarContaPostDTO(null, e.getMessage(), null, false, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }
        catch (Exception e) {
            String tipoErro = e.getClass().getSimpleName();
            String mensagem = tipoErro + ": " + e.getMessage();
            AcessarContaPostDTO erro = new AcessarContaPostDTO(null, mensagem, null, false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }		
	}
	
	@Autowired
	private br.com.tonypool.services.TwoFactorAuthService twoFactorAuthService;

	@ApiOperation("Endpoint para confirmar o código 2FA e gerar token.")
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/api/confirmar-2fa")
	public ResponseEntity<AcessarContaPostDTO> confirmar2FA(@RequestBody Confirmar2FARequest request) {
        try {
            Cliente cliente = clienteRepository.findById(request.getIdCliente()).orElse(null);
            if (cliente == null || !Boolean.TRUE.equals(cliente.getIs2FAEnabled())) {
                logger.warn("[2FA] Cliente inválido ou 2FA não está ativado: idCliente={}", request.getIdCliente());
                AcessarContaPostDTO erro = new AcessarContaPostDTO(null, "Cliente inválido ou 2FA não está ativado.", request.getIdCliente(), false, null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
            }
            if (cliente.getSecret2FA() == null || cliente.getSecret2FA().isEmpty()) {
                logger.error("[2FA] Secret do cliente está nulo ou vazio! idCliente={}, secret2FA={}", request.getIdCliente(), cliente.getSecret2FA());
                AcessarContaPostDTO erro = new AcessarContaPostDTO(null, "2FA não está ativado para este cliente. Ative o 2FA antes de validar o código.", request.getIdCliente(), false, null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
            }
            logger.info("[2FA] Secret do cliente para validação: idCliente={}, secret2FA={}", request.getIdCliente(), cliente.getSecret2FA());
            boolean valido = twoFactorAuthService.verifyCode(cliente, request.getCodigo());
            if (!valido) {
                logger.warn("[2FA] Código 2FA inválido para idCliente={}", request.getIdCliente());
                AcessarContaPostDTO erro = new AcessarContaPostDTO(null, "Código 2FA inválido.", request.getIdCliente(), false, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
            }
            String perfilNome = cliente.getPerfil() != null ? cliente.getPerfil().getNome() : null;
            String token = TokenSecurity.generateToken(cliente.getCpf(), cliente.getIdCliente());
            logger.info("[2FA] Código válido, token gerado para idCliente={}", cliente.getIdCliente());
            AcessarContaPostDTO dto = new AcessarContaPostDTO(token, cliente.getNome(), cliente.getIdCliente(), true, perfilNome);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (Exception e) {
            logger.error("[2FA] Erro inesperado: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            String mensagem = "Erro inesperado ao validar o 2FA. Tente novamente ou contate o suporte.";
            AcessarContaPostDTO erro = new AcessarContaPostDTO(null, mensagem, request.getIdCliente(), false, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

	@ApiOperation("Endpoint para consultar o perfil do cliente autenticado.")
	@RequestMapping(value = "/api/perfil/{id}", method = RequestMethod.GET)
	public ResponseEntity<ClientePerfilResponse> getPerfil(@PathVariable Integer id, @RequestHeader("Authorization") String authorization) {
		try {
			// Extrai o token JWT do header
			String token = authorization.replace("Bearer ", "");
			// Extrai o id do cliente do token
			Integer idClienteToken = TokenSecurity.getIdClienteFromToken(token);
			if (!id.equals(idClienteToken)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
			Cliente cliente = clienteRepository.findById(id).orElse(null);
			if (cliente == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			ClientePerfilResponse response = new ClientePerfilResponse(
				cliente.getIdCliente(),
				cliente.getNome(),
				cliente.getCpf(),
				cliente.getEmail(),
				cliente.getTelefone(),
				cliente.getPerfil() != null ? cliente.getPerfil().getNome() : null
			);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
