package webshop.services.dataCheckers;

import org.springframework.stereotype.Component;

import model.Address;
import repositories.AddressRepository;
import webshop.services.exceptions.RequestNotFoundException;

@Component
public class AddressDataChecker {
	
	private final AddressRepository repository;
	
	AddressDataChecker(AddressRepository repository){
		this.repository = repository;
	}
	
	public boolean addressChecker(Address addressToCheck) {
		try {
			Long id = addressToCheck.getId();
			Address address = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("address", id));
			
			if (address.getCity().equals(null)) {
				return false;
			}
			if (address.getCountry().equals(null)) {
				return false;
			}
			if (address.getHouse_number().equals(null)) {
				return false;
			}
			if (address.getPostal_code().equals(null)) {
				return false;
			}
			if (address.getState().equals(null)) {
				return false;
			}
			if (address.getStreet().equals(null)) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
	
}
