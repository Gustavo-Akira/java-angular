package curso.api.rest.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import curso.api.rest.models.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends  JpaRepository<Usuario, Long>{
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
}
