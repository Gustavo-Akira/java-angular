package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.models.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	private static final long EXPIRATION_TIME = 172800000;
	
	private static final String SECRET = "*--*/4265a3-*3*/e9s+w9/3e*-";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		String JWT = Jwts.builder().setSubject(username).setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(SignatureAlgorithm.HS512, SECRET).compact();
		String token = TOKEN_PREFIX +" "+ JWT;
		
		response.addHeader(HEADER_STRING, token);
		corsLiberation(response);
		response.getWriter().write("{\"Authorization\":\""+ token +"\"}");
		
	}
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = request.getHeader(HEADER_STRING);
		try {
		if(token != null) {
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
			if(user !=null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);
				if(usuario !=null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(),usuario.getSenha(),usuario.getAuthorities());
				}
				
			}
			
		}
		}catch(ExpiredJwtException e) {
			try{
				response.getOutputStream().println("Your Token expired, please do login again to refresh your token");
			}catch(IOException){
				
			}
		}
		corsLiberation(response);
		return null;
	}
	private void corsLiberation(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
