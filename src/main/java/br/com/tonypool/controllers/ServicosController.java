package br.com.tonypool.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.tonypool.entities.Profissional;
import br.com.tonypool.entities.Servico;
import br.com.tonypool.repositories.IServicoRepository;
import br.com.tonypool.requests.ServicoCreateRequest;
import br.com.tonypool.responses.ProfissionalGetResponse;
import br.com.tonypool.responses.ServicoGetResponse;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class ServicosController {

	@Autowired
	private IServicoRepository servicoRepository;
	
	@CrossOrigin
	@ApiOperation("Endpoint para consulta de serviços.")
	@RequestMapping(value = "/api/servicos", method = RequestMethod.GET)
	public ResponseEntity<List<ServicoGetResponse>> get() {

		try {
			
			List<ServicoGetResponse> lista = new ArrayList<ServicoGetResponse>();
			
			for(Servico servico : servicoRepository.findAll()) {
				
				ServicoGetResponse servicoResponse = new ServicoGetResponse();
				servicoResponse.setIdServico(servico.getIdServico());
				servicoResponse.setNome(servico.getNome());
				servicoResponse.setValor(servico.getValor());
				
				servicoResponse.setProfissionais(new ArrayList<ProfissionalGetResponse>());
				for(Profissional profissional : servico.getProfissionais()) {
					
					ProfissionalGetResponse profissionalResponse = new ProfissionalGetResponse();
					profissionalResponse.setIdProfissional(profissional.getIdProfissional());
					profissionalResponse.setNome(profissional.getNome());
					profissionalResponse.setTelefone(profissional.getTelefone());
					
					servicoResponse.getProfissionais().add(profissionalResponse);
				}
				
				lista.add(servicoResponse);
			}
			
			//HTTP 200 (OK)
			return ResponseEntity.status(HttpStatus.OK).body(lista);
		}
		catch(Exception e) {
			
			//HTTP 500 (INTERNAL SERVER ERROR)
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@CrossOrigin
	@ApiOperation("Retorna os profissionais vinculados a um serviço específico.")
	@GetMapping("/api/servicos/{id}/profissionais")
	public ResponseEntity<List<ProfissionalGetResponse>> getProfissionaisByServico(@PathVariable Integer id) {
		try {
			Servico servico = servicoRepository.findById(id).orElse(null);
			if (servico == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			List<ProfissionalGetResponse> lista = new ArrayList<>();
			for (Profissional profissional : servico.getProfissionais()) {
				ProfissionalGetResponse response = new ProfissionalGetResponse();
				response.setIdProfissional(profissional.getIdProfissional());
				response.setNome(profissional.getNome());
				response.setTelefone(profissional.getTelefone());
				lista.add(response);
			}
			return ResponseEntity.status(HttpStatus.OK).body(lista);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@CrossOrigin
	@ApiOperation("Cadastra um serviço.")
	@PostMapping("/api/servicos")
	public ResponseEntity<ServicoGetResponse> create(@RequestBody ServicoCreateRequest request) {
		try {
			Servico servico = new Servico();
			servico.setNome(request.getNome());
			servico.setValor(request.getValor());
			servicoRepository.save(servico);

			ServicoGetResponse response = new ServicoGetResponse();
			response.setIdServico(servico.getIdServico());
			response.setNome(servico.getNome());
			response.setValor(servico.getValor());
			response.setProfissionais(new ArrayList<ProfissionalGetResponse>());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}