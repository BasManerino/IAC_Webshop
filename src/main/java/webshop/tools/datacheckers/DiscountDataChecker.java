package webshop.tools.datacheckers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import model.Discount;
import model.Product;
import repositories.DiscountRepository;
import repositories.ProductRepository;
import webshop.tools.exceptions.RequestNotFoundException;

@Component // Deze klasse is verantwoordelijk voor checks
public class DiscountDataChecker {

	private final DiscountRepository repository;
	private final ProductDataChecker productDataChecker;
	private final ProductRepository productRepository;

	DiscountDataChecker(DiscountRepository repository, ProductDataChecker productDataChecker,
			ProductRepository productRepository) {
		this.repository = repository;
		this.productDataChecker = productDataChecker;
		this.productRepository = productRepository;
	}

	// Check alle gegevens van de discount en relateerde products
	public boolean discountChecker(Discount discountToCheck) {
		Date from = discountToCheck.getFrom_date();
		Date until = discountToCheck.getUntil_date();
		Date now = new Date();

		if (discountToCheck.getAdText().equals(null)) {
			return false;
		} else if (from.equals(null)) {
			return false;
		} else if (discountToCheck.getPercentage() == 0.0) {
			return false;
		} else if (until.equals(null) || until.before(from) || until.before(now)) {
			return false;
		}

		if (!discountToCheck.giveProducts().equals(null) && !discountToCheck.giveProducts().isEmpty()) {
			List<Product> products = discountToCheck.giveProducts();
			for (Product product : products) {
				if (!product.getId().equals(null) && !product.equals(null)) {
					if (productDataChecker.findProductAndCheck(product) == false) {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	// Check of dit discount bestaat wel of niet en check de gegevens
	public boolean findDiscountAndCheck(Discount discountToFind) {
		try {
			Long id = discountToFind.getId();
			Discount discountToCheck = repository.findById(id)
					.orElseThrow(() -> new RequestNotFoundException("discount", id));
			return discountChecker(discountToCheck);
		} catch (RuntimeException e) {
			return false;
		}
	}

	// Deze methode past de adText van het discount op basis van de datums en de
	// geldigheid
	public Discount discountAdTextChecker(Discount discount) {
		if (discount != null) {
			if (discountValidityChecker(discount)) {
				int percentage = discount.getPercentage();
				DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
				String adText = "Discount " + percentage + "% from " + formatter.format(discount.getFrom_date())
						+ " until " + formatter.format(discount.getUntil_date());
				discount.setAdText(adText);
			} else {
				discount.setAdText("This discount is expired!!!");
			}
		}

		return discount;
	}

	// Check als een discount geldig of niet is
	public boolean discountValidityChecker(Discount discountToCheck) {
		Long id = discountToCheck.getId(); // Om alle gegevens van deze discount te krijgen
		Discount discount = repository.findById(id).orElseThrow(() -> new RequestNotFoundException("discount", id));
		Date now = new Date();
		Date from = discount.getFrom_date();
		Date until = discount.getUntil_date();
		if ((from.before(now) || from.equals(now)) && (until.after(now) || until.equals(now))) {
			return true;
		} else {
			return false;
		}
	}

	// Check of de discount met de id (offerCode) geldig of niet is
	public boolean checkProductsOfferCode(Iterable<Product> productsToCheck, Long offerCode) {
		Discount discountToCheck = new Discount();
		discountToCheck.setId(offerCode);
		List<Long> productsIds = new ArrayList<Long>();

		for (Product product : productsToCheck) {
			productsIds.add(product.getId());
		}

		Iterable<Product> products = productRepository.findAllById(productsIds);

		for (Product product : products) {
			List<Discount> discounts = product.giveDiscounts();
			for (Discount discount : discounts) {
				if (discount.getId() == offerCode) {
					return discountValidityChecker(discountToCheck);
				}
			}
		}

		return false;
	}
}
