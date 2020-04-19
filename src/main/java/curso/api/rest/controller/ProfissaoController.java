package curso.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.models.Profissao;
import curso.api.rest.repository.ProfissaoRepository;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/profissao")
public class ProfissaoController {
	@Autowired
	private ProfissaoRepository repository;
	
	@GetMapping(value = "/",produces = "application/json")
	public ResponseEntity<List<Profissao>> profissoes(){
		List<Profissao> profissao = repository.findAll();
		return ResponseEntity.ok(profissao);
	}
}
