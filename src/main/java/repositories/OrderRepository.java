package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Order;

public interface OrderRepository extends CrudRepository<Order, Long>{

}
