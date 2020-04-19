package curso.api.rest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import curso.api.rest.models.Role;
import curso.api.rest.models.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends  JpaRepository<Usuario, Long>{
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByName(String name);
	@Transactional
	@Modifying
	@Query(value = "insert into usuario_role (usuario_id, role_id) values (?1, (select id from role where nome_role = 'ROLE_USER'));", nativeQuery = true)
	void addRole(Long idUsuario);
	default Page<Usuario> findUserByNamePage(String nome, PageRequest lista){
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		ExampleMatcher example = ExampleMatcher.matchingAny().withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Usuario> list = Example.of(usuario,example);
		Page<Usuario> retorno = findAll(list, lista);
		return retorno;
	}
	@Transactional
	@Modifying
	@Query(value="Update usuario set senha = ?1 where id = ?2",nativeQuery=true)
	void updateSenha(String senha, Long codUser);
}
