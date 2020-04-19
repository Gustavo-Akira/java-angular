package curso.api.rest;

import java.sql.SQLException;
import java.util.List;
import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControlerExcecoes extends ResponseEntityExceptionHandler{
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String msg = "";
		if(ex instanceof MethodArgumentNotValidException) {
			System.out.println("aaaaa");
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			for(ObjectError object : list) {
				msg += object.getDefaultMessage()+"\n"; 
			}
		}else {
			msg= ex.getMessage();
		}
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msg);
		objetoError.setCode(status.value()+ "==>" + status.getReasonPhrase());
		return new ResponseEntity<>(objetoError, headers, status);
	}
	
	@ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, PSQLException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {
		String msg = "";
		if(ex instanceof DataIntegrityViolationException) {
			msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
		}else if(ex instanceof ConstraintViolationException) {
			msg = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
		}else if(ex instanceof PSQLException) {
			msg = ((PSQLException) ex).getCause().getCause().getMessage();
		}else if(ex instanceof SQLException){
			msg = ((SQLException) ex).getCause().getCause().getMessage();
		}else {
			msg = ex.getMessage();
		}
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msg);
		objetoError.setCode(HttpStatus.INTERNAL_SERVER_ERROR+ "==>"+ HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		return new ResponseEntity<>(objetoError,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
