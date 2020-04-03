package webshop.tools.datacheckers;

import java.util.List;
import org.springframework.stereotype.Component;
import model.Order;
import model.Product;
import repositories.OrderRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class OrderDataChecker {

	private final OrderRepository repository;
	private final ProductDataChecker productDataChecker;
	private final AccountDataChecker accountDataChecker;

	OrderDataChecker(OrderRepository repository, ProductDataChecker productDataChecker,
			AccountDataChecker accountDataChecker) {
		this.repository = repository;
		this.productDataChecker = productDataChecker;
		this.accountDataChecker = accountDataChecker;
	}

	// Check alle gegevens van de order en relateerde account
	public boolean orderChecker(Order orderToCheck) {
		if (orderToCheck.getDate().equals(null)) {
			return false;
		} else if (orderToCheck.getTotal_price() != calculateTotalPrice(orderToCheck)
				|| orderToCheck.getTotal_price() == 0.0) { // This checks also the products
			return false;
		} else if (orderToCheck.giveAccount().equals(null)
				|| !accountDataChecker.findAccountAndCheck(orderToCheck.giveAccount())) {
			return false;
		} else if (orderToCheck.giveProducts() == null
				|| !productDataChecker.availablityCheckerToBoolean(orderToCheck.giveProducts())) {
			return false;
		} else
			return true;
	}

	public boolean findOrderAndCheck(Order orderToFind) {
		try {
			Long id = orderToFind.getId();
			Order orderToCheck = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("order", id));
			return orderChecker(orderToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	public Order findOrder(Order orderToFind) {
		try {
			Long id = orderToFind.getId();
			Order order = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("order", id));
			return order;
		} catch (RuntimeException e) {
			return null;
		}
	}

	private double calculateTotalPrice(Order order) {
		List<Product> orderProducts = productDataChecker.availablityCheckerToList(order.giveProducts());

		if (order.giveProducts().equals(null) || order.giveProducts().isEmpty()) {
			return 0;
		} else {
			double total = 0;
			for (Product product : orderProducts) {
				total += product.getNormal_price();
			}
			return total;
		}
	}
}
