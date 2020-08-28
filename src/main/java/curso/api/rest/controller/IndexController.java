package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.models.UserChart;
import curso.api.rest.models.Usuario;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;
import curso.api.rest.service.ServiceRelatorio;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private ImplementacaoUserDetailsService impservice;
	
	@Autowired
	private TelefoneRepository trepository;
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@GetMapping(value = "/{id}",produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = repository.findById(id);
		return  ResponseEntity.ok(usuario.get());
	}
	@GetMapping(value = "/",produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarios() throws InterruptedException{
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> list = repository.findAll(page);
		
		return ResponseEntity.ok(list);
	}
	@GetMapping(value = "/page/{pagina}",produces = "application/json")
	@CacheEvict(value="cacheusuarios",allEntries=true)
	@CachePut
	public ResponseEntity<Page<Usuario>> pages(@PathVariable("pagina") int pagina) throws InterruptedException{
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> list = repository.findAll(page);
		
		return ResponseEntity.ok(list);
	}
	@GetMapping(value = "/filtro/{nome}",produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuariosame(@PathVariable("nome") String nome){
		PageRequest lista = null;
		Page<Usuario> list =null;
		if(nome == null ||(nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined") ) {
			lista = PageRequest.of(0, 5, Sort.by("nome"));
			list = repository.findAll(lista);
		}else {
			lista = PageRequest.of(0, 5, Sort.by("nome"));
			list = repository.findUserByNamePage(nome,lista);
		}
		
		return ResponseEntity.ok(list);
	}
	@GetMapping(value = "/filtro/{nome}/page/{page}",produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuariospage(@PathVariable("nome") String nome, @PathVariable("page") int page){
		PageRequest lista = null;
		Page<Usuario> list =null;
		if(nome == null ||(nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined") ) {
			lista = PageRequest.of(page, 5, Sort.by("nome"));
			list = repository.findAll(lista);
		}else {
			lista = PageRequest.of(page, 5, Sort.by("nome"));
			list = repository.findUserByNamePage(nome,lista);
		}
		
		return ResponseEntity.ok(list);
	}
	@PostMapping(value = "/",produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws DataIntegrityViolationException{
		if(usuario.getTelefones() != null) {
			for(int pos=0;pos<usuario.getTelefones().size(); pos++){
				usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		}
		String senhaCripto =new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCripto);
		Usuario usuarioSalvo = repository.save(usuario);
		impservice.adicionarRole(usuarioSalvo.getId());
		return ResponseEntity.ok(usuarioSalvo);
	}
	@PutMapping(value="/",produces = "application/json")
	public ResponseEntity<Usuario> editar(@RequestBody Usuario usuario){
		if(usuario.getTelefones() != null) {
			for(int pos=0;pos<usuario.getTelefones().size(); pos++){
				usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		}
		Usuario userTemporario = repository.findById(usuario.getId()).get();
		if(!userTemporario.getSenha().toString().trim().equals(usuario.getSenha())) {
			String senha = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senha);
		}
		Usuario usuarioEditado = repository.save(usuario);
		return ResponseEntity.ok(usuarioEditado);
	}
	@DeleteMapping(value="/{id}", produces = "application/text")
	public String delete(@PathVariable("id")Long id ){
		repository.deleteById(id);
		return "Usuario deletado com sucesso";
	}
	@DeleteMapping(value="/telefone/{id}",produces="application/text")
	public String deleteTelefone(@PathVariable("id")Long id) {
		trepository.deleteById(id);
		return "ok";
	}
	@GetMapping(value = "/relatorio",produces="application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception{
		byte[] pdf = serviceRelatorio.gerarRelatorio("angular", request.getServletContext());
		String base64_pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		return new ResponseEntity<String>(base64_pdf,HttpStatus.OK);
	}
	@GetMapping(value="/chart",produces = "application/json")
	public ResponseEntity<UserChart> grafico(){
		UserChart userchart = new UserChart();
		List<String> resultado = jdbc.queryForList("select array_agg(''''||nome||'''') from usuario where salario > 0 and nome <> '' union all select cast(array_agg(salario) as character varying[]) from usuario where salario > 0  and nome <> ''", String.class);
		System.out.println(resultado);
		if(!resultado.isEmpty()) {
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salarios = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			
			userchart.setNomeFuncionario(nomes);
			userchart.setSalario(salarios);
		}
		return new ResponseEntity<UserChart>(userchart,HttpStatus.OK);
	}
}
