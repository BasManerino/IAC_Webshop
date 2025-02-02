package webshop.tools.converters;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

@Component
public class Converter {
	
	//Converteert een iterable naar stream
	public <T> Stream<T> toStream(Iterable<T> i) {
	    return StreamSupport.stream(i.spliterator(), false);
	}
}
