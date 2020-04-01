package webshop.services.dataCheckers;

import org.springframework.stereotype.Component;

import model.Role;
import repositories.RoleRepository;
import webshop.services.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class RoleDataChecker {

	private final RoleRepository repository;

	RoleDataChecker(RoleRepository repository) {
		this.repository = repository;
	}

	// Check alle gegevens van de role
	public boolean roleChecker(Role roleToCheck) {
		if (roleToCheck.getName().equals(null)) {
			return false;
		} else if (roleToCheck.getDescription().equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	// Check of deze role bestaat wel of niet en check de gegevens
	public boolean findRoleAndCheck(Role roleToFine) {
		try {
			Long id = roleToFine.getId();
			Role roleToCheck = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("role", id));
			return roleChecker(roleToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}
}
