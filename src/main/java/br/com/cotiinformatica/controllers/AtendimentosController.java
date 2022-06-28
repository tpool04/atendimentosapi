package br.com.cotiinformatica.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.cotiinformatica.entities.Atendimento;
import br.com.cotiinformatica.entities.Cliente;
import br.com.cotiinformatica.entities.Profissional;
import br.com.cotiinformatica.entities.Servico;
import br.com.cotiinformatica.repositories.IAtendimentoRepository;
import br.com.cotiinformatica.repositories.IClienteRepository;
import br.com.cotiinformatica.repositories.IProfissionalRepository;
import br.com.cotiinformatica.repositories.IServicoRepository;
import br.com.cotiinformatica.requests.AtendimentoPostRequest;
import br.com.cotiinformatica.responses.AtendimentoGetResponse;
import br.com.cotiinformatica.security.TokenSecurity;
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
			if(consultaServico.isPresent())
				servico = consultaServico.get();
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Serviço não encontrado.");
			
			//consultar o profissional no banco de dados através do ID
			Profissional profissional = null;
			Optional<Profissional> consultaProfissional = profissionalRepository.findById(request.getIdProfissional());
			if(consultaProfissional.isPresent())
				profissional = consultaProfissional.get();
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Profissional não encontrado.");
			
			//verificar se o profissional informado realizar o servico informado
			boolean profissionalValido = false;
			for(Servico item : profissional.getServicos()) {
				if(item.getIdServico() == servico.getIdServico()) {
					profissionalValido = true;
					break;
				}
			}
			
			if(!profissionalValido) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: O profissional não realiza o serviço desejado.");
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

			List<AtendimentoGetResponse> lista = new ArrayList<AtendimentoGetResponse>();
			
			//consultar todos os atendimentos do cliente
			for(Atendimento atendimento : atendimentoRepository.findByCliente(cliente.getIdCliente())) {
				
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
			
			return ResponseEntity.status(HttpStatus.OK).body(lista);

		} catch (Exception e) {

			// HTTP 500 (SERVER ERROR) -> INTERNAL SERVER ERROR
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	private Cliente getClienteByAccessToken(HttpServletRequest httpRequest) throws Exception {
		
		// capturar o cliente autenticado na API, através do TOKEN
		String accessToken = httpRequest.getHeader("Authorization").replace("Bearer", "").trim();
		String cpf = TokenSecurity.getUserFromToken(accessToken);

		// consultar o cliente no banco de dados através do CPF
		return clienteRepository.findByCpf(cpf);
		
	}

}



