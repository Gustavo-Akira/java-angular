package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import curso.api.rest.models.Usuario;
import curso.api.rest.repository.UsuarioRepository;
@Repository
public class ImplementacaoUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = repository.findUserByLogin(username);
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuario não encontrado");
		}
		return new User(usuario.getLogin(),usuario.getPassword(),usuario.getAuthorities());
	}
	
	public void adicionarRole(Long id) {
		repository.addRole(id);
	}
}
