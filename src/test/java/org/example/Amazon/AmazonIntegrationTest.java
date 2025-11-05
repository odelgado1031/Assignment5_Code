package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // <-- required for assertEquals, etc.

class AmazonIntegrationTest {

    private static class InMemoryCart implements ShoppingCart {
        private final List<Item> items = new ArrayList<>();
        @Override public void add(Item item) { items.add(item); }
        @Override public List<Item> getItems() { return items; }
        @Override public int numberOfItems() { return items.size(); }
    }

    private Item other(String name, int qty, double ppu) {
        return new Item(ItemType.OTHER, name, qty, ppu);
    }

    @Test
    @DisplayName("Spec: end-to-end subtotal + DeliveryPrice (4 entries => +12.5)")
    void e2eTotalWithDeliveryTiers() {
        InMemoryCart cart = new InMemoryCart();
        cart.add(other("a", 1, 10.0));
        cart.add(other("b", 1, 20.0));
        cart.add(other("c", 1, 30.0));
        cart.add(other("d", 1, 40.0)); // 4 entries

        List<PriceRule> rules = List.of(new RegularCost(), new DeliveryPrice());
        Amazon amazon = new Amazon(cart, rules);

        assertEquals(10 + 20 + 30 + 40 + 12.5, amazon.calculate(), 1e-6);
    }

    @Test
    @DisplayName("Structural: DeliveryPrice tiers by entry count (not quantities)")
    void emptyCartAndTierBoundaries() {
        InMemoryCart cart = new InMemoryCart();
        // 0 entries → 0.0
        assertEquals(0.0, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);

        // 1..3 entries → 5.0
        cart.add(other("a", 5, 1.0)); // qty doesn't change tier
        assertEquals(5.0, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);

        cart.add(other("b", 1, 1.0));
        cart.add(other("c", 1, 1.0));
        assertEquals(5.0, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);

        // 4..10 entries → 12.5
        cart.add(other("d", 1, 1.0)); // now 4 entries
        assertEquals(12.5, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);

        for (int i = 0; i < 6; i++) cart.add(other("x"+i, 1, 1.0)); // up to 10 entries
        assertEquals(12.5, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);

        // 11+ → 20.0
        cart.add(other("y", 1, 1.0)); // now 11
        assertEquals(20.0, new Amazon(cart, List.of(new DeliveryPrice())).calculate(), 1e-9);
    }
}