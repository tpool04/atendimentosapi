package br.com.cotiinformatica.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.cotiinformatica.entities.Profissional;
import br.com.cotiinformatica.repositories.IProfissionalRepository;
import br.com.cotiinformatica.responses.ProfissionalGetResponse;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class ProfissionaisController {

	@Autowired
	private IProfissionalRepository profissionalRepository;
	
	@CrossOrigin
	@ApiOperation("Endpoint para consulta de profissionais.")
	@RequestMapping(value = "/api/profissionais", method = RequestMethod.GET)
	public ResponseEntity<List<ProfissionalGetResponse>> get() {
		
		try {
			
			List<ProfissionalGetResponse> lista = new ArrayList<ProfissionalGetResponse>();
			
			//consultando os profissionais cadastrados no banco de dados
			for(Profissional profissional : profissionalRepository.findAll()) {
				
				ProfissionalGetResponse response = new ProfissionalGetResponse();
				response.setIdProfissional(profissional.getIdProfissional());
				response.setNome(profissional.getNome());
				response.setTelefone(profissional.getTelefone());
				
				lista.add(response);
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

}



