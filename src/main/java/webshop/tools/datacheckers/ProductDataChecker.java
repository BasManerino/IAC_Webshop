package webshop.tools.datacheckers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import model.Category;
import model.Discount;
import model.Product;
import repositories.ProductRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class ProductDataChecker {

	private final ProductRepository repository;

	ProductDataChecker(ProductRepository repository) {
		this.repository = repository;
	}

	// Check alle gegevens van het product
	public boolean productChecker(Product productToCheck) {
		if (productToCheck.getName().equals(null)) {
			return false;
		} else if (productToCheck.getDescription().equals(null)) {
			return false;
		} else if (productToCheck.getNormal_price() == 0.0) {
			return false;
		} else if (productToCheck.getImageId().equals(null)) {
			return false;
		} else {
			return true;
		}
	}

	// Check of dit product bestaat wel of niet en check de gegevens
	public boolean findProductAndCheck(Product productToFind) {
		try {
			Long id = productToFind.getId();
			Product productToCheck = repository.findById(id)
					.orElseThrow(() -> new RequestNotFoundException("product", id));
			return productChecker(productToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}

	// Als het product aan geen category toegevoegd is, wordt die product in
	// category 'New' toegevoegd
	public Product categoriesListChecker(Product newProduct) {
		int count = 0;
		List<Category> categories = newProduct.giveCategories();
		if (categories != null) {
			for (Category category : categories) {
				if (category.getId() != null) {
					count += 1;
				}
			}
		}

		if (categories == null || count == 0) {
			categories = new ArrayList<Category>();
			Category newCategory = new Category();
			// ID 1 is de ID van de category 'New'
			newCategory.setId((long) 1);
			categories.add(newCategory);
		}
		newProduct.setCategories(categories);
		return newProduct;
	}

	// De discount van het product berekenen en plaatsen in discount_price attribute
	public Product productDiscountCalculator(Product product) {
		List<Discount> discounts = product.giveDiscounts();
		if (discounts != null) {
			Date now = new Date();
			for (Discount discount : discounts) {
				if (discount != null) {
					if (discount.getFrom_date().before(now) && discount.getUntil_date().after(now)) {
						double normalPrice = product.getNormal_price();
						int percentage = discount.getPercentage();
						String discountPrice = String.format("%.2f",
								(normalPrice - ((normalPrice * percentage) / 100)));
						product.setDiscount_price(discountPrice);
					}
				}
			}
		}

		return product;
	}

	// Check of de products van een lijst beschikbaar of niet is, de niet
	// beschikbare gaan verwijderd worden en alleen maar de beschikbare producten
	// gaan returned worden
	public List<Product> availablityCheckerToList(Iterable<Product> productsToCheck) {
		List<Product> availableProducts = new ArrayList<Product>();
		List<Long> productsIds = new ArrayList<Long>();

		for (Product product : productsToCheck) {
			productsIds.add(product.getId());
		}

		Iterable<Product> products = repository.findAllById(productsIds);

		for (Product product : products) {
			if (product.isAvailable()) {
				availableProducts.add(product);
			}
		}

		return availableProducts;
	}

	// Check of alle producten van een lijst beschikbaar of niet is en wordt true
	// returned als alle producten zijn beschikbaar
	public boolean availablityCheckerToBoolean(Iterable<Product> productsToCheck) {
		List<Long> productsIds = new ArrayList<Long>();

		for (Product product : productsToCheck) {
			productsIds.add(product.getId());
		}

		Iterable<Product> products = repository.findAllById(productsIds);

		for (Product product : products) {
			if (!product.isAvailable()) {
				return false;
			}
		}

		return true;
	}

	// Check of een product met id beschikbaar of niet is
	public boolean availablityCheckerProductId(Long productId) {
		try {
			Product product = repository.findById(productId)
					.orElseThrow(() -> new RequestNotFoundException("product", productId));

			return product.isAvailable();
		} catch (RuntimeException e) {
			return false;
		}
	}
}
