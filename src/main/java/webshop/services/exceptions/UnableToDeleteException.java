package webshop.services.exceptions;

@SuppressWarnings("serial")
public class UnableToDeleteException extends RuntimeException {

	// Een exception voor als het delete proces niet gelukt is
	public UnableToDeleteException(String className, Long id) {
		super("Could not delete " + className + " with id number " + id);
	}
}
