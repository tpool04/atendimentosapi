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
import br.com.cotiinformatica.entities.Servico;
import br.com.cotiinformatica.repositories.IServicoRepository;
import br.com.cotiinformatica.responses.ProfissionalGetResponse;
import br.com.cotiinformatica.responses.ServicoGetResponse;
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
				servicoResponse.setPreco(servico.getPreco());
				
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

}
