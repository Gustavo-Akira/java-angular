package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
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

import curso.api.rest.models.Usuario;
import curso.api.rest.repository.UsuarioRepository;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository repository;
	
	@GetMapping(value = "/{id}",produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = repository.findById(id);
		return  ResponseEntity.ok(usuario.get());
	}
	@GetMapping(value = "/",produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarios(){
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios = repository.findAll();
		return ResponseEntity.ok(usuarios);
	}
	
	@PostMapping(value = "/",produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario){
		for(int pos=0;pos<usuario.getTelefones().size(); pos++){
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		String senhaCripto =new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCripto);
		Usuario usuarioSalvo = repository.save(usuario);
		return ResponseEntity.ok(usuarioSalvo);
	}
	@PutMapping(value="/",produces = "application/json")
	public ResponseEntity<Usuario> editar(@RequestBody Usuario usuario){
		for(int pos=0;pos<usuario.getTelefones().size(); pos++){
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		Usuario usuarioEditado = repository.save(usuario);
		return ResponseEntity.ok(usuarioEditado);
	}
	@DeleteMapping(value="/{id}", produces = "application/text")
	public String delete(@PathVariable("id")Long id ){
		repository.deleteById(id);
		return "Usuario deletado com sucesso";
	}
}
