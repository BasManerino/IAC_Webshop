package webshop.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice // Deze klasse is verantwoordelijk van HttpStatus te returnen op basis van de
					// exception
class NotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(RequestNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFoundHandler(RequestNotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(UnableToUpdateException.class)
	@ResponseStatus(HttpStatus.NOT_MODIFIED)
	String notUpdatedHandler(UnableToUpdateException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(UnableToSaveException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String notSavedHandler(UnableToSaveException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(UnableToDeleteException.class)
	@ResponseStatus(HttpStatus.NOT_MODIFIED)
	String notDeletedHandler(UnableToDeleteException ex) {
		return ex.getMessage();
	}
}
