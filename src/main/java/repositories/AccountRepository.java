package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Account;

//Deze repository is gemaakt te kunnen communiceren met de database,
//De CRUD methoden zijn default gedefineerd ermee
public interface AccountRepository extends CrudRepository<Account, Long> {

}
