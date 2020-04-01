package webshop.services.dataCheckers;

import org.springframework.stereotype.Component;

import model.Account;
import repositories.AccountRepository;
import webshop.services.exceptions.RequestNotFoundException;

@Component
public class AccountDataChecker {

	private final AccountRepository repository;
	private final AddressDataChecker addressDataChecker;

	AccountDataChecker(AccountRepository repository, AddressDataChecker addressDataChecker) {
		this.repository = repository;
		this.addressDataChecker = addressDataChecker;
	}

	public boolean accountChecker(Account accountToCheck) {
		try {
			Long id = accountToCheck.getId();
			Account account = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("account", id));
			if (account.getName().equals(null)) {
				return false;
			}
			if (account.getEmail().equals(null)) {
				return false;
			}
			if (account.giveAddress().equals(null)) {
				return false;
			}
			if (!addressDataChecker.addressChecker(account.giveAddress())) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
}
