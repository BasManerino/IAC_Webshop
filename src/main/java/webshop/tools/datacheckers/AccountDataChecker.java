package webshop.tools.datacheckers;

import org.springframework.stereotype.Component;

import model.Account;
import repositories.AccountRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class AccountDataChecker {

	private final AccountRepository repository;
	private final AddressDataChecker addressDataChecker;
	private final RoleDataChecker roleDataChecker;

	AccountDataChecker(AccountRepository repository, AddressDataChecker addressDataChecker,
			RoleDataChecker roleDataChecker) {
		this.repository = repository;
		this.addressDataChecker = addressDataChecker;
		this.roleDataChecker = roleDataChecker;
	}

	// Check alle gegevens van het account en relateerde address en role
	public boolean accountChecker(Account accountToCheck) {
		if (accountToCheck.getName().equals(null)) {
			return false;
		} else if (accountToCheck.getEmail().equals(null)) {
			return false;
		} else if (accountToCheck.getCreated_on().equals(null)) {
			return false;
		} else if (accountToCheck.giveAddress().equals(null)) {
			return false;
		} else if (!addressDataChecker.findAddressAndCheck(accountToCheck.giveAddress())) {
			return false;
		} else if (accountToCheck.giveRole().equals(null)) {
			return false;
		} else if (!roleDataChecker.findRoleAndCheck(accountToCheck.giveRole())) {
			return false;
		} else {
			return true;
		}
	}

	// Check of dit account bestaat wel of niet en check de gegevens
	public boolean findAccountAndCheck(Account accountToFind) {
		try {
			Long id = accountToFind.getId();
			Account accountToCheck = repository.findById(id)
					.orElseThrow(() -> new RequestNotFoundException("account", id));
			return accountChecker(accountToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}
}
