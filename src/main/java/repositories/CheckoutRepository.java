package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Checkout;

public interface CheckoutRepository extends CrudRepository<Checkout, Long>{

}
