package br.com.tonypool.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@Autowired
	private br.com.tonypool.repositories.IServicoRepository servicoRepository;
	
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

	@CrossOrigin
	@ApiOperation("Endpoint para consulta detalhada de todos os profissionais com seus serviços.")
	@GetMapping("/api/profissionais/detalhados")
	public ResponseEntity<List<br.com.tonypool.responses.ProfissionalDetalhadoResponse>> getDetalhados() {
		try {
			List<Profissional> profissionais = (List<Profissional>) profissionalRepository.findAll();
			List<br.com.tonypool.responses.ProfissionalDetalhadoResponse> lista = new ArrayList<>();
			for (Profissional profissional : profissionais) {
				List<br.com.tonypool.responses.ServicoResponse> servicos = new ArrayList<>();
				if (profissional.getServicos() != null) {
					for (br.com.tonypool.entities.Servico servico : profissional.getServicos()) {
						br.com.tonypool.responses.ServicoResponse s = new br.com.tonypool.responses.ServicoResponse();
						s.setIdServico(servico.getIdServico());
						s.setNome(servico.getNome());
						s.setValor(servico.getValor());
						servicos.add(s);
					}
				}
				br.com.tonypool.responses.ProfissionalDetalhadoResponse response = new br.com.tonypool.responses.ProfissionalDetalhadoResponse();
				response.setIdProfissional(profissional.getIdProfissional());
				response.setNome(profissional.getNome());
				response.setTelefone(profissional.getTelefone());
				response.setServicos(servicos);
				lista.add(response);
			}
			return ResponseEntity.status(HttpStatus.OK).body(lista);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
	@ApiOperation("Endpoint para alteração de profissional e seus serviços.")
	@PutMapping("/api/profissionais/{id}")
	public ResponseEntity<br.com.tonypool.responses.ProfissionalDetalhadoResponse> alterarProfissional(
	    @PathVariable Integer id,
	    @RequestBody br.com.tonypool.requests.ProfissionalPutRequest request
	) {
	    try {
	        Profissional profissional = profissionalRepository.findById(id).orElse(null);
	        if (profissional == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	        profissional.setNome(request.getNome());
	        profissional.setTelefone(request.getTelefone());
	        // Atualizar lista de serviços vinculados
	        List<br.com.tonypool.entities.Servico> novosServicos = new ArrayList<>();
	        List<br.com.tonypool.entities.Servico> servicosAntigos = profissional.getServicos() != null ? new ArrayList<>(profissional.getServicos()) : new ArrayList<>();
	        if (request.getServicos() != null) {
	            for (br.com.tonypool.requests.ServicoPutRequest servicoReq : request.getServicos()) {
	                br.com.tonypool.entities.Servico servico = servicoRepository.findById(servicoReq.getIdServico()).orElse(null);
	                if (servico != null) {
	                    servico.setValor(servicoReq.getValor());
	                    // Adiciona o profissional à lista de profissionais do serviço se não estiver
	                    if (servico.getProfissionais() == null) servico.setProfissionais(new ArrayList<>());
	                    if (!servico.getProfissionais().contains(profissional)) {
	                        servico.getProfissionais().add(profissional);
	                    }
	                    servicoRepository.save(servico);
	                    novosServicos.add(servico);
	                }
	            }
	        }
	        // Remover o profissional dos serviços que não estão mais associados
	        for (br.com.tonypool.entities.Servico servicoAntigo : servicosAntigos) {
	            if (!novosServicos.contains(servicoAntigo)) {
	                if (servicoAntigo.getProfissionais() != null && servicoAntigo.getProfissionais().contains(profissional)) {
	                    servicoAntigo.getProfissionais().remove(profissional);
	                    servicoRepository.save(servicoAntigo);
	                }
	            }
	        }
	        profissional.setServicos(novosServicos);
	        profissionalRepository.save(profissional);
	        // Montar resposta detalhada
	        List<br.com.tonypool.responses.ServicoResponse> servicosResp = new ArrayList<>();
	        for (br.com.tonypool.entities.Servico servico : profissional.getServicos()) {
	            br.com.tonypool.responses.ServicoResponse s = new br.com.tonypool.responses.ServicoResponse();
	            s.setIdServico(servico.getIdServico());
	            s.setNome(servico.getNome());
	            s.setValor(servico.getValor());
	            servicosResp.add(s);
	        }
	        br.com.tonypool.responses.ProfissionalDetalhadoResponse response = new br.com.tonypool.responses.ProfissionalDetalhadoResponse();
	        response.setIdProfissional(profissional.getIdProfissional());
	        response.setNome(profissional.getNome());
	        response.setTelefone(profissional.getTelefone());
	        response.setServicos(servicosResp);
	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

}