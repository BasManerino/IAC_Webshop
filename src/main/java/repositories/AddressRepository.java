package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Address;

public interface AddressRepository extends CrudRepository<Address, Long>{

}
