package repositories;

import org.springframework.data.repository.CrudRepository;
import model.Product;

//Deze repository is gemaakt te kunnen communiceren met de database,
//De CRUD methoden zijn default gedefineerd ermee
public interface ProductRepository extends CrudRepository<Product, Long>{

}
