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

@Component // Deze klasse is verantwoordelijk voor checks
public class CheckoutDataChecker {

	private final ProductDataChecker productChecker;
	private final DiscountDataChecker discountChecker;
	private final AccountDataChecker accountDataChecker;

	CheckoutDataChecker(ProductDataChecker productChecker, AccountDataChecker accountDataChecker,
			DiscountDataChecker discountChecker) {
		this.productChecker = productChecker;
		this.discountChecker = discountChecker;
		this.accountDataChecker = accountDataChecker;
	}

	// Check alle gegevens van de checkout en relateerde products en account
	// Return als string list met de foute gegevens
	public List<String> checkoutChecker(Checkout checkout) {
		List<String> errorsFound = new ArrayList<String>();
		List<String> paymentMethods = new ArrayList<String>(); //Het lijst met alle payment methoden
		paymentMethods.add("Cash");
		paymentMethods.add("Creditcard");
		paymentMethods.add("Visacard");
		paymentMethods.add("iDeal");
		paymentMethods.add("Paypal");
		List<Product> productsToCheck = checkout.giveProducts();
		boolean allProductsAvailable = productChecker.availablityCheckerToBoolean(productsToCheck);
		Account accountToCheck = checkout.giveAccount();
		Long offerCode = checkout.getOffer_code();

		if (!allProductsAvailable) {
			errorsFound.add("Products");
		} else if (offerCode != null) {
			if (!discountChecker.checkProductsOfferCode(productsToCheck, offerCode)) {
				errorsFound.add("OfferCode");
			}
		} else if (checkout.getPay_date() != null) {
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
		} else if (checkout.getPay_date() == null) {
			errorsFound.add("PaymentDate");
		} else if (!paymentMethods.contains(checkout.getPay_method())) {
			errorsFound.add("PaymentMethod");
		} else if (accountToCheck == null || !accountDataChecker.findAccountAndCheck(accountToCheck)) {
			errorsFound.add("Account details");
		}

		return errorsFound;
	}
}
