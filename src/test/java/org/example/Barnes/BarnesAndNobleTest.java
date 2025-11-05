package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class BarnesAndNobleTest {

    // Minimal fake DB keyed by explicit ISBN string
    private static class FakeDb implements BookDatabase {
        private final Map<String, Book> data = new HashMap<>();
        void put(String isbn, Book b) { data.put(isbn, b); }
        @Override public Book findByISBN(String ISBN) { return data.get(ISBN); }
    }

    // Spy purchase process
    private static class SpyProcess implements BuyBookProcess {
        int calls; Book lastBook; int lastQty;
        @Override public void buyBook(Book book, int amount) { calls++; lastBook = book; lastQty = amount; }
    }

    @Test
    @DisplayName("specification-based: null order returns null summary & no processing")
    void nullOrder() {
        FakeDb db = new FakeDb();
        SpyProcess process = new SpyProcess();
        BarnesAndNoble api = new BarnesAndNoble(db, process);

        assertNull(api.getPriceForCart(null));
        assertEquals(0, process.calls);
    }

    @Test
    @DisplayName("specification-based: total price sums available quantities; nothing unavailable")
    void computesTotals() {
        FakeDb db = new FakeDb();
        Book a = new Book("A", 10, 5);
        Book b = new Book("B", 25, 1);
        db.put("A", a);
        db.put("B", b);

        SpyProcess process = new SpyProcess();
        BarnesAndNoble api = new BarnesAndNoble(db, process);

        Map<String,Integer> order = Map.of("A", 3, "B", 1);
        PurchaseSummary sum = api.getPriceForCart(order);

        assertNotNull(sum);
        assertEquals(10*3 + 25*1, sum.getTotalPrice());
        assertTrue(sum.getUnavailable().isEmpty());
        assertEquals(2, process.calls);
    }

    @Test
    @DisplayName("structural-based: requesting over stock records 'unavailable' remainder")
    void unavailableWhenExceedsStock() {
        FakeDb db = new FakeDb();
        Book a = new Book("A", 10, 1); // only 1 in stock
        db.put("A", a);

        SpyProcess process = new SpyProcess();
        BarnesAndNoble api = new BarnesAndNoble(db, process);

        Map<String,Integer> order = Map.of("A", 3);
        PurchaseSummary sum = api.getPriceForCart(order);

        assertNotNull(sum);
        assertEquals(10 * 1, sum.getTotalPrice()); // charged for available only
        assertEquals(1, sum.getUnavailable().size());

        // Check the single unavailable entry uses the same Book instance with remainder=2
        var entry = sum.getUnavailable().entrySet().iterator().next();
        assertSame(a, entry.getKey());
        assertEquals(2, entry.getValue());
    }
}