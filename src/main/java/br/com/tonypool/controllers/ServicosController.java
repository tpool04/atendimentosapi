package br.com.tonypool.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.tonypool.entities.Atendimento;
import br.com.tonypool.entities.Profissional;
import br.com.tonypool.entities.Servico;
import br.com.tonypool.repositories.IAtendimentoRepository;
import br.com.tonypool.repositories.IServicoRepository;
import br.com.tonypool.requests.ServicoCreateRequest;
import br.com.tonypool.responses.ProfissionalGetResponse;
import br.com.tonypool.responses.ServicoGetResponse;
import br.com.tonypool.responses.ServicoSimpleResponse;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class ServicosController {

	@Autowired
	private IServicoRepository servicoRepository;
	
	@Autowired
    private IAtendimentoRepository atendimentoRepository;
	
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
	@ApiOperation("Endpoint para Retorna os profissionais vinculados a um serviço específico.")
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
	@ApiOperation("Endpoint para Cadastra um serviço.")
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

	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
	@ApiOperation("Endpoint para Altera os dados de um serviço pelo ID.")
	@PutMapping("/api/servicos/{id}")
	public ResponseEntity<ServicoGetResponse> update(@PathVariable Integer id, @RequestBody ServicoCreateRequest request) {
		try {
			Servico servico = servicoRepository.findById(id).orElse(null);
			if (servico == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			servico.setNome(request.getNome());
			servico.setValor(request.getValor());
			servicoRepository.save(servico);

			ServicoGetResponse response = new ServicoGetResponse();
			response.setIdServico(servico.getIdServico());
			response.setNome(servico.getNome());
			response.setValor(servico.getValor());
			response.setProfissionais(new ArrayList<ProfissionalGetResponse>());
			for (Profissional profissional : servico.getProfissionais()) {
				ProfissionalGetResponse profissionalResponse = new ProfissionalGetResponse();
				profissionalResponse.setIdProfissional(profissional.getIdProfissional());
				profissionalResponse.setNome(profissional.getNome());
				profissionalResponse.setTelefone(profissional.getTelefone());
				response.getProfissionais().add(profissionalResponse);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
	@ApiOperation("Endpoint alternativo para listar todos os serviços.")
	@GetMapping("/api/servicos/all")
	public ResponseEntity<List<ServicoSimpleResponse>> getAll() {
		try {
			List<ServicoSimpleResponse> lista = new ArrayList<>();
			for (Servico servico : servicoRepository.findAll()) {
				ServicoSimpleResponse servicoResponse = new ServicoSimpleResponse();
				servicoResponse.setIdServico(servico.getIdServico());
				servicoResponse.setNome(servico.getNome());
				servicoResponse.setValor(servico.getValor());
				lista.add(servicoResponse);
			}
			return ResponseEntity.status(HttpStatus.OK).body(lista);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
	@ApiOperation("Endpoint para excluir um serviço pelo ID, respeitando regras de integridade referencial.")
	@DeleteMapping("/api/servicos/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			Servico servico = servicoRepository.findById(id).orElse(null);
			if (servico == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serviço não encontrado.");
			}
			// Verifica atendimentos futuros
			List<Atendimento> atendimentos = atendimentoRepository.findByServico(id);
			Date agora = new Date();
			boolean temAtendimentoFuturo = false;
			for (Atendimento atendimento : atendimentos) {
				if (atendimento.getDataHora() != null && atendimento.getDataHora().after(agora)) {
					temAtendimentoFuturo = true;
					break;
				}
			}
			// Se tem atendimento futuro, não pode excluir
			if (temAtendimentoFuturo) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Não é possível excluir o serviço porque possui atendimentos futuros agendados.");
			}
			// Se está vinculado a profissional, mas só tem atendimentos passados, pode excluir
			servicoRepository.deleteById(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir serviço: " + e.getMessage());
		}
	}

}