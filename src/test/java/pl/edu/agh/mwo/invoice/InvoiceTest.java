package pl.edu.agh.mwo.invoice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test
    public void testInvoiceHasNumber() {
        int number = invoice.getNumber();
        Assert.assertTrue(number > 0);
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumber() {
        int number = invoice.getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertNotEquals(number, number2);
    }

    @Test
    public void testTheSameInvoicesHaveTheSameNumber() {
        Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
    }

    @Test
    public void testAddOneDuplicateIncreaseQuantity() {
        OtherProduct skarpeta = new OtherProduct("skarpeta", new BigDecimal("5.25"));
        invoice.addProduct(skarpeta);
        invoice.addProduct(skarpeta);
        int quantity2 = invoice.getProducts().get(skarpeta);
        invoice.addProduct(skarpeta);
        int quantity3 = invoice.getProducts().get(skarpeta);
        Assert.assertTrue(quantity3 - quantity2 == 1);
    }

    @Test
    public void testAddDifferentProduct() {
        OtherProduct skarpeta = new OtherProduct("skarpeta", new BigDecimal("5.25"));
        OtherProduct mydlo = new OtherProduct("mydlo", new BigDecimal("4"));
        invoice.addProduct(skarpeta);
        int quantity1 = invoice.getProducts().get(skarpeta);
        invoice.addProduct(mydlo);
        int quantity2 = invoice.getProducts().get(skarpeta);
        Assert.assertEquals(quantity1, quantity2);
    }

    @Test
    public void testAddManyDuplicates() {
        OtherProduct skarpeta = new OtherProduct("skarpeta", new BigDecimal("5.25"));
        invoice.addProduct(skarpeta);
        int quantity1 = invoice.getProducts().get(skarpeta);
        invoice.addProduct(skarpeta, 7);
        int quantity2 = invoice.getProducts().get(skarpeta);
        Assert.assertNotEquals(quantity2 - quantity1, quantity1);
    }

    @Test
    public void testAddManyDifferentProducts() {
        OtherProduct skarpeta = new OtherProduct("skarpeta", new BigDecimal("5.25"));
        OtherProduct mydlo = new OtherProduct("skarpeta", new BigDecimal("5.25"));
        invoice.addProduct(skarpeta, 5);
        int quantity1 = invoice.getProducts().get(skarpeta);
        invoice.addProduct(mydlo, 3);
        int quantity2 = invoice.getProducts().get(skarpeta);
        Assert.assertEquals(quantity1, quantity2);
    }

    @Test
    public void testInvoiceHasDate() {
        LocalDate date = invoice.getIssueDate();
        Assert.assertTrue(date.getDayOfYear() > 0 && date.getDayOfYear() < 367);
    }

    @Test
    public void testNewlyCreatedInvoiceHasTodayDate() {
        Assert.assertEquals(invoice.getIssueDate().compareTo(LocalDate.now()), 0);
    }

    @Test
    public void testCarrierDayReduceTax() {
        invoice.setIssueDate(2021, 4, 26);
        FuelCanister product = new FuelCanister("Fuel canister", new BigDecimal("100"));
        invoice.addProduct(product);
        BigDecimal reducedPrice = invoice.carrierDayPriceReductor(product);
        Assert.assertEquals(product.getPrice().add(product.getExcise()), reducedPrice);
    }

    @Test
    public void testCasualDayNotReduceTax() {
        invoice.setIssueDate(2021, 6, 26);
        FuelCanister product = new FuelCanister("Fuel canister", new BigDecimal("100"));
        invoice.addProduct(product);
        BigDecimal reducedPrice = invoice.carrierDayPriceReductor(product);
        Assert.assertNotEquals(product.getPrice().add(product.getExcise()), reducedPrice);
    }

    @Test
    public void testCarrierDayNotAffectRestProducts() {
        invoice.setIssueDate(2021, 4, 26);
        Product product = new OtherProduct("buty", new BigDecimal("100"));
        invoice.addProduct(product);
        BigDecimal reducedPrice = invoice.carrierDayPriceReductor(product);
        invoice.printInvoice();
        Assert.assertEquals(product.getPriceWithTax(), reducedPrice);
    }

    @Test
    public void testPrintInvoiceOutputIsNotEmpty() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        invoice.printInvoice();

        String invoiceString = outputStreamCaptor.toString().trim();

        Assert.assertNotEquals(invoiceString, null);
        System.setOut(standardOut);
    }

    @Test
    public void testPrintInvoiceOutputIsCorrect() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        invoice.addProduct(new OtherProduct("skarpeta", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("buty", new BigDecimal("100")), 1);
        invoice.printInvoice();
        String invoiceString = outputStreamCaptor.toString().trim();

        String expectedString = "Issue date: " + invoice.getIssueDate() + "\r\n" + "Invoice number: "
                + invoice.getNumber() + "\r\n" + "Product: skarpeta; quantity: 3; unit price: 12.30\r\n"
                + "Product: buty; quantity: 1; unit price: 123.00\r\n" + "Number of items: 2";
        System.setOut(standardOut);
        Assert.assertEquals(expectedString.trim(), invoiceString);

    }

}
