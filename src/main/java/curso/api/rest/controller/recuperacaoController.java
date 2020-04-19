package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoError;
import curso.api.rest.models.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.EnvioEmailService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/recuperar")
public class recuperacaoController {
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private EnvioEmailService envio;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoError> error(@RequestBody Usuario login) throws MessagingException{
		ObjetoError error = new ObjetoError();
		Usuario usuario = repository.findUserByLogin(login.getLogin());
		if(usuario == null) {
			error.setCode("400");
			error.setError("Usuario não Encontrado");
		}else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
			String senha = dateFormat.format(Calendar.getInstance().getTime());
			repository.updateSenha(new BCryptPasswordEncoder().encode(senha), usuario.getId());
			envio.enviarEmail("Recuperação de senha", usuario.getLogin(), "sua nova senha é "+senha);
			error.setCode("200");
			error.setError("Acesso enviado para seus emails");
		}
		return new ResponseEntity<ObjetoError>(error, HttpStatus.OK);
	}
}
