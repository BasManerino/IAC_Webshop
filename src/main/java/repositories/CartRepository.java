package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Cart;

public interface CartRepository extends CrudRepository<Cart, Long> {

}
