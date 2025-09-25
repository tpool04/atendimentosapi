package br.com.tonypool.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.tonypool.entities.Atendimento;
import br.com.tonypool.entities.Cliente;
import br.com.tonypool.entities.Profissional;
import br.com.tonypool.entities.Servico;
import br.com.tonypool.repositories.IAtendimentoRepository;
import br.com.tonypool.repositories.IClienteRepository;
import br.com.tonypool.repositories.IProfissionalRepository;
import br.com.tonypool.repositories.IServicoRepository;
import br.com.tonypool.requests.AtendimentoPostRequest;
import br.com.tonypool.requests.ReagendarAtendimentoRequest;
import br.com.tonypool.responses.AtendimentoGetResponse;
import br.com.tonypool.security.TokenSecurity;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class AtendimentosController {

	@Autowired
	private IAtendimentoRepository atendimentoRepository;

	@Autowired
	private IClienteRepository clienteRepository;

	@Autowired
	private IServicoRepository servicoRepository;

	@Autowired
	private IProfissionalRepository profissionalRepository;

	@CrossOrigin
	@ApiOperation("Endpoint para o cliente cadastrar um atendimento.")
	@RequestMapping(value = "/api/atendimentos", method = RequestMethod.POST)
	public ResponseEntity<String> post(@RequestBody AtendimentoPostRequest request, HttpServletRequest httpRequest) {

		try {

			// consultar o cliente no banco de dados através do CPF
			Cliente cliente = getClienteByAccessToken(httpRequest);

			// consultar o serviço no banco de dados através do ID
			Servico servico = null;
			Optional<Servico> consultaServico = servicoRepository.findById(request.getIdServico());
			if (consultaServico.isPresent())
				servico = consultaServico.get();
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Serviço não encontrado.");

			// consultar o profissional no banco de dados através do ID
			Profissional profissional = null;
			Optional<Profissional> consultaProfissional = profissionalRepository.findById(request.getIdProfissional());
			if (consultaProfissional.isPresent())
				profissional = consultaProfissional.get();
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Profissional não encontrado.");

			// verificar se o profissional informado realizar o servico informado
			boolean profissionalValido = false;
			for (Servico item : profissional.getServicos()) {
				if (item.getIdServico() == servico.getIdServico()) {
					profissionalValido = true;
					break;
				}
			}

			if (!profissionalValido) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Erro: O profissional não realiza o serviço desejado.");
			}

			// criando a data do atendimento
			Date dataHora = new SimpleDateFormat("dd/MM/yyyy-HH:mm").parse(request.getData() + "-" + request.getHora());

			// cadastrando o atendimento
			Atendimento atendimento = new Atendimento();
			atendimento.setCliente(cliente);
			atendimento.setServico(servico);
			atendimento.setProfissional(profissional);
			atendimento.setDataHora(dataHora);
			atendimento.setObservacoes(request.getObservacoes());

			atendimentoRepository.save(atendimento);

			return ResponseEntity.status(HttpStatus.CREATED).body("Atendimento cadastrado com sucesso.");
		} catch (Exception e) {
			// HTTP 401 (Unauthorized) para erro de autenticação
			if (e.getMessage().contains("Token JWT") || e.getMessage().contains("Cliente não encontrado")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
			}
			// HTTP 500 (SERVER ERROR) -> INTERNAL SERVER ERROR
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@CrossOrigin
	@ApiOperation("Endpoint para o cliente consultar os atendimentos agendados.")
	@RequestMapping(value = "/api/atendimentos", method = RequestMethod.GET)
	public ResponseEntity<List<AtendimentoGetResponse>> get(HttpServletRequest httpRequest) {
		try {
			Cliente cliente = getClienteByAccessToken(httpRequest);
			List<AtendimentoGetResponse> lista = new ArrayList<>();
			// consultar apenas os atendimentos do cliente logado
			List<Atendimento> atendimentos = atendimentoRepository.findByCliente(cliente.getIdCliente());
			for (Atendimento atendimento : atendimentos) {
				AtendimentoGetResponse response = new AtendimentoGetResponse();
				response.setIdAtendimento(atendimento.getIdAtendimento());
				response.setDataHora(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(atendimento.getDataHora()));
				response.setNomeServico(atendimento.getServico().getNome());
				response.setPrecoServico(atendimento.getServico().getPreco());
				response.setNomeProfissional(atendimento.getProfissional().getNome());
				response.setTelefoneProfissional(atendimento.getProfissional().getTelefone());
				response.setNomeCliente(cliente.getNome());
				response.setCpfCliente(cliente.getCpf());
				response.setObservacoes(atendimento.getObservacoes());
				lista.add(response);
			}
			return ResponseEntity.ok(lista);
		} catch (Exception e) {
			// HTTP 401 (Unauthorized) para erro de autenticação
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	private Cliente getClienteByAccessToken(HttpServletRequest httpRequest) throws Exception {
		// capturar o cliente autenticado na API, através do TOKEN
		String accessToken = httpRequest.getHeader("Authorization");
		if (accessToken == null || !accessToken.startsWith("Bearer ")) {
			throw new Exception("Token JWT ausente ou malformado.");
		}
		accessToken = accessToken.replace("Bearer ", "").trim();
		String cpf = TokenSecurity.getUserFromToken(accessToken);
		Cliente cliente = clienteRepository.findByCpf(cpf);
		if (cliente == null) {
			throw new Exception("Cliente não encontrado para o CPF extraído do token.");
		}
		return cliente;
	}

	@ApiOperation("Endpoint para o cliente cancelar os atendimentos agendados.")
	@DeleteMapping("/api/atendimentos/{id}")
	public ResponseEntity<String> cancelarAtendimento(@PathVariable Integer id,
			@RequestHeader("Authorization") String authHeader) throws Exception {
			String cpf = TokenSecurity.getUserFromToken(authHeader.replace("Bearer ", ""));
			Cliente cliente = clienteRepository.findByCpf(cpf);

			Atendimento atendimento = atendimentoRepository.findById(id).orElse(null);
			if (atendimento == null || !atendimento.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
				return ResponseEntity.status(403).body("Acesso negado ou atendimento não encontrado.");
			}

			atendimentoRepository.delete(atendimento);
			return ResponseEntity.ok("Atendimento cancelado com sucesso.");
	}

	@ApiOperation("Endpoint para o cliente reagendar atendimentos agendados.")
	@PutMapping("/api/atendimentos/{id}/reagendar")
	public ResponseEntity<String> reagendarAtendimento(@PathVariable Integer id,
			@RequestHeader("Authorization") String authHeader, @RequestBody ReagendarAtendimentoRequest request)
			throws Exception {
			String cpf = TokenSecurity.getUserFromToken(authHeader.replace("Bearer ", ""));
			Cliente cliente = clienteRepository.findByCpf(cpf);

			Atendimento atendimento = atendimentoRepository.findById(id).orElse(null);
			if (atendimento == null || !atendimento.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
				return ResponseEntity.status(403).body("Acesso negado ou atendimento não encontrado.");
			}

			// Buscar novo serviço
			Servico servico = atendimento.getServico();
			if (request.getIdServico() != null) {
				Optional<Servico> consultaServico = servicoRepository.findById(request.getIdServico());
				if (consultaServico.isPresent()) {
					servico = consultaServico.get();
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Serviço não encontrado.");
				}
			}

			// Buscar novo profissional
			Profissional profissional = atendimento.getProfissional();
			if (request.getIdProfissional() != null) {
				Optional<Profissional> consultaProfissional = profissionalRepository.findById(request.getIdProfissional());
				if (consultaProfissional.isPresent()) {
					profissional = consultaProfissional.get();
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Profissional não encontrado.");
				}
			}

			// Validar se o profissional realiza o serviço
			boolean profissionalValido = false;
			for (Servico item : profissional.getServicos()) {
				if (item.getIdServico() == servico.getIdServico()) {
					profissionalValido = true;
					break;
				}
			}
			if (!profissionalValido) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Erro: O profissional não realiza o serviço desejado.");
			}

			Date novaData = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(request.getNovaDataHora());
			atendimento.setDataHora(novaData);
			atendimento.setObservacoes(request.getNovaObservacao());
			atendimento.setProfissional(profissional);
			atendimento.setServico(servico);
			atendimentoRepository.save(atendimento);

			return ResponseEntity.ok("Atendimento reagendado com sucesso.");
	}

	@ApiOperation("Endpoint para o cliente consultar um atendimento específico pelo ID.")
	@CrossOrigin
	@RequestMapping(value = "/api/atendimentos/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getAtendimentoById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
		try {
			String cpf = TokenSecurity.getUserFromToken(authHeader.replace("Bearer ", ""));
			Cliente cliente = clienteRepository.findByCpf(cpf);

			Atendimento atendimento = atendimentoRepository.findById(id).orElse(null);
			if (atendimento == null || !atendimento.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
				return ResponseEntity.status(403).body("Acesso negado ou atendimento não encontrado.");
			}

			AtendimentoGetResponse response = new AtendimentoGetResponse();
			response.setIdAtendimento(atendimento.getIdAtendimento());
			response.setDataHora(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(atendimento.getDataHora()));
			response.setNomeServico(atendimento.getServico().getNome());
			response.setPrecoServico(atendimento.getServico().getPreco());
			response.setNomeProfissional(atendimento.getProfissional().getNome());
			response.setTelefoneProfissional(atendimento.getProfissional().getTelefone());
			response.setNomeCliente(cliente.getNome());
			response.setCpfCliente(cliente.getCpf());
			response.setObservacoes(atendimento.getObservacoes());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

}