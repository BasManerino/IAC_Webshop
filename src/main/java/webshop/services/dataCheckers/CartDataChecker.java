package webshop.services.dataCheckers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import model.Cart;
import model.Product;

@Component
public class CartDataChecker {
	private final ProductDataChecker productChecker;

	CartDataChecker(ProductDataChecker productChecker) {
		this.productChecker = productChecker;
	}

	public Cart checkProductsAvailablity(Cart cart) {
		List<Product> products = (ArrayList<Product>) cart.giveProducts();
		List<Long> productsToCheck = new ArrayList<Long>();

		for (Product product : products) {
			productsToCheck.add(product.getId());
		}

		products = productChecker.availablityCheckerListIds(productsToCheck);

		cart.setProducts(products);

		return cart;
	}
	
	public boolean checkProductAvailablity(Long productId) {
		return productChecker.availablityCheckerProductId(productId);
	}
}