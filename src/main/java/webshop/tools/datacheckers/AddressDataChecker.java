package webshop.tools.datacheckers;

import org.springframework.stereotype.Component;

import model.Address;
import repositories.AddressRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class AddressDataChecker {

	private final AddressRepository repository;

	AddressDataChecker(AddressRepository repository) {
		this.repository = repository;
	}

	// Check alle gegevens van het address
	public boolean addressChecker(Address addressToCheck) {
		if (addressToCheck.getCity().equals(null)) {
			return false;
		} else if (addressToCheck.getCountry().equals(null)) {
			return false;
		} else if (addressToCheck.getHouse_number().equals(null)) {
			return false;
		} else if (addressToCheck.getPostal_code().equals(null)) {
			return false;
		} else if (addressToCheck.getState().equals(null)) {
			return false;
		} else if (addressToCheck.getStreet().equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	// Check of dit address bestaat wel of niet en check de gegevens
	public boolean findAddressAndCheck(Address addressToFind) {
		try {
			Long id = addressToFind.getId();
			Address addressToCheck = repository.findById(id)
					.orElseThrow(() -> new RequestNotFoundException("address", id));
			return addressChecker(addressToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}

}
