package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
	private Map<Product, Integer> products = new LinkedHashMap<>(); // linkedHashMap zagwarantuje kolejnosc produktow

	public void addProduct(Product product) {
		this.products.put(product, 1);
	}

	public void addProduct(Product product, Integer quantity) {
		if (quantity == 0 || quantity < 0) {
			throw new IllegalArgumentException("You cannot add zero or negate quantity of products.");
		}
						
		this.products.put(product, quantity);
	}

	public BigDecimal getNetPrice() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Product product : this.products.keySet()) {
			Integer quantity = this.products.get(product);
			sum = sum.add(product.getPrice().multiply(new BigDecimal(quantity)));
		}
		return sum;
	}

	public BigDecimal getTax() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Product product : this.products.keySet()) {
			Integer quantity = this.products.get(product);
			sum = sum.add(product.getPrice().multiply(new BigDecimal(quantity)).multiply(product.getTaxPercent()));
		}
		return sum;
	}

	public BigDecimal getTotal() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Product product : this.products.keySet()) {
			Integer quantity = this.products.get(product);
			sum = sum.add(product.getPriceWithTax().multiply(new BigDecimal(quantity)));
		}
		return sum;
	}
}
