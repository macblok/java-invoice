package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private final int number = Math.abs(new Random().nextInt());
    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();
    private LocalDate issueDate = LocalDate.now();
    private final LocalDate carrierDay = LocalDate.of(2021, 4, 26);
    
    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        if (products.get(product) == null) {
            products.put(product, quantity);
        } else {
            products.put(product, products.get(product) + quantity);
        }
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public void printInvoice() {
        System.out.println("Issue date: " + issueDate);
        System.out.println("Invoice number: " + this.getNumber());
        for (Product product : products.keySet()) {
            System.out.println("Product: " + product.getName()
                    + "; quantity: " + products.get(product)
                    + "; unit price: " + carrierDayPriceReductor(product));

        }

        System.out.println("Number of items: " + products.size());

    }

    public BigDecimal carrierDayPriceReductor(Product product) {
        if (product instanceof FuelCanister && issueDate.getDayOfMonth()
                == carrierDay.getDayOfMonth() && issueDate.getMonthValue()
                == carrierDay.getMonthValue()) {
            BigDecimal reducedPrice = product.getPriceWithTax()
                    .subtract(product.getPrice().multiply(product.getTaxPercent()));
            return reducedPrice;
        }
        return product.getPriceWithTax();
    }

    public int getNumber() {
        return number;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setIssueDate(int year, int month, int dayOfMonth) {
        issueDate = LocalDate.of(year, month, dayOfMonth);
    }

}
