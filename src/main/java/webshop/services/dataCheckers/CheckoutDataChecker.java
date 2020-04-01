package webshop.services.dataCheckers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import model.Account;
import model.Checkout;
import model.Product;

@Component
public class CheckoutDataChecker {

	private final ProductDataChecker productChecker;
	private final AccountDataChecker accountDataChecker;

	CheckoutDataChecker(ProductDataChecker productChecker, AccountDataChecker accountDataChecker) {
		this.productChecker = productChecker;
		this.accountDataChecker = accountDataChecker;
	}

	public List<String> checkoutChecker(Checkout checkout) {
		List<String> errorsFound = new ArrayList<String>();
		List<String> paymentMethods = new ArrayList<String>();
		paymentMethods.add("Cash");
		paymentMethods.add("Creditcard");
		paymentMethods.add("Visacard");
		paymentMethods.add("iDeal");
		paymentMethods.add("Paypal");
		List<Product> productsToCheck = checkout.giveProducts();
		boolean allProductsAvailable = productChecker.availablityCheckerListProducts(productsToCheck);
		Account accountToCheck = checkout.giveAccount();

		if (!allProductsAvailable) {
			errorsFound.add("Products");
		}

		Long offerCode = checkout.getOffer_code();
		if (offerCode != null) {
			if (!productChecker.checkProductsOfferCode(productsToCheck, offerCode)) {
				errorsFound.add("OfferCode");
			}
		}

		if (checkout.getPay_date() != null) {
			try {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date now = new Date();
				Date pDate = checkout.getPay_date();
				Date pDay = formatter.parse(formatter.format(pDate));
				Date today = formatter.parse(formatter.format(now));
				if (!pDay.equals(today)) {
					errorsFound.add("PaymentDate");
				}
			} catch (ParseException e) {
				errorsFound.add("PaymentDate");
				e.printStackTrace();
			}
		} else {
			errorsFound.add("PaymentDate");
		}

		if (!paymentMethods.contains(checkout.getPay_method())) {
			errorsFound.add("PaymentMethod");
		}

		if (accountToCheck == null || !accountDataChecker.accountChecker(accountToCheck)) {
			errorsFound.add("Account details");
		}

		return errorsFound;
	}

}
