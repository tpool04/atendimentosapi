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
import org.springframework.web.bind.annotation.RequestParam;

import br.com.tonypool.entities.Profissional;
import br.com.tonypool.repositories.IProfissionalRepository;
import br.com.tonypool.responses.ProfissionalGetResponse;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class ProfissionaisController {

	@Autowired
	private IProfissionalRepository profissionalRepository;
	
	@CrossOrigin
	@ApiOperation("Endpoint para consulta de profissionais, com filtro opcional por serviço.")
	@GetMapping("/api/profissionais")
	public ResponseEntity<List<ProfissionalGetResponse>> get(@RequestParam(value = "servico", required = false) String servico) {
		try {
			List<Profissional> profissionais;
			if (servico == null || servico.equalsIgnoreCase("todos")) {
				profissionais = (List<Profissional>) profissionalRepository.findAll();
			} else {
				Integer idServico = Integer.valueOf(servico);
				profissionais = profissionalRepository.findByServicoId(idServico);
			}
			List<ProfissionalGetResponse> lista = new ArrayList<>();
			for (Profissional profissional : profissionais) {
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

}