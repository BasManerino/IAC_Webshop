package webshop.tools.datacheckers;

import java.util.List;
import org.springframework.stereotype.Component;
import model.Cart;
import model.Product;

@Component // Deze klasse is verantwoordelijk voor checks
public class CartDataChecker {
	private final ProductDataChecker productChecker;

	CartDataChecker(ProductDataChecker productChecker) {
		this.productChecker = productChecker;
	}

	// Check de beschikbaarheid van alle producten van de cart
	public Cart checkProductsAvailablity(Cart cart) {
		List<Product> products = productChecker.availablityCheckerToList(cart.giveProducts());

		cart.setProducts(products);

		return cart;
	}

	// Check de beschikbaarheid van een product
	public boolean checkProductAvailablity(Long productId) {
		return productChecker.availablityCheckerProductId(productId);
	}
}