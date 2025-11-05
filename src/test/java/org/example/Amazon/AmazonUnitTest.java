package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class AmazonUnitTest {

    private static class FakeCart implements ShoppingCart {
        private final List<Item> items = new ArrayList<>();
        @Override public void add(Item item) { items.add(item); }
        @Override public List<Item> getItems() { return items; }
        @Override public int numberOfItems() { return items.size(); }
    }

    private static class FixedRule implements PriceRule {
        private final double val;
        FixedRule(double v) { this.val = v; }
        @Override public double priceToAggregate(List<Item> cart) { return val; }
    }

    @Test
    @DisplayName("specification-based: Amazon.calculate sums all PriceRule results")
    void calculatesSumAcrossRules() {
        FakeCart cart = new FakeCart();
        cart.add(new Item(ItemType.OTHER, "book", 2, 12.5));

        Amazon amazon = new Amazon(cart, List.of(new FixedRule(25.0), new FixedRule(5.0)));
        assertEquals(30.0, amazon.calculate(), 1e-9);
    }

    @Test
    @DisplayName("structural-based: addToCart delegates to cart.add")
    void addToCartDelegation() {
        FakeCart cart = new FakeCart();
        Amazon amazon = new Amazon(cart, List.of());

        Item item = new Item(ItemType.ELECTRONIC, "usb-c", 1, 9.99);
        amazon.addToCart(item);

        assertEquals(1, cart.numberOfItems());
        assertSame(item, cart.getItems().get(0));
    }

    @Test
    @DisplayName("structural boundary: RegularCost handles empty and non-empty carts")
    void regularCostBoundaries() {
        RegularCost rule = new RegularCost();

        assertEquals(0.0, rule.priceToAggregate(List.of()), 1e-9);

        double price = rule.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "pen", 3, 2.0),
                new Item(ItemType.ELECTRONIC, "drive", 1, 8.0)
        ));
        assertEquals(3*2.0 + 8.0, price, 1e-9);
    }
}
