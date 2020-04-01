package webshop.services.exceptions;

@SuppressWarnings("serial")
public class RequestNotFoundException extends RuntimeException {

	// Een exception voor als het object niet gevonden is
	public RequestNotFoundException(String className, Long id) {
		super("Could not find " + className + " with id number " + id);
	}
}