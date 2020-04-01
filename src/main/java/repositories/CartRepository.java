package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Cart;

//Deze repository is gemaakt te kunnen communiceren met de database,
//De CRUD methoden zijn default gedefineerd ermee
public interface CartRepository extends CrudRepository<Cart, Long> {

}
