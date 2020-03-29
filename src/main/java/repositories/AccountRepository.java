package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {

}
