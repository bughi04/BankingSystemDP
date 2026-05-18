package src.ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private RonAccount ronAcc;
    private EurAccount eurAcc;

    @BeforeEach
    void accounts() {
        ronAcc = new RonAccount("RON123", 100.0);
        eurAcc = new EurAccount("EUR123", 200.0);
    }

    @Test
    void deposit() {
        ronAcc.depose(100.0);
        assertEquals(200.0, ronAcc.amount, "The balance should be 200 after depositing 100");
    }

    @Test
    void retrieve() throws Exception {
        ronAcc.retrieve(50.0);
        assertEquals(50.0, ronAcc.amount, "The balance should be 50 after retrieving 50");
    }

    @Test
    void noBalance() {
        Exception exception = assertThrows(Exception.class, () -> {
            ronAcc.retrieve(150.0);
        });
        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void interest() {
        assertEquals(0.03, ronAcc.getInterest(), "Interest should be 3% for balance <500");
        ronAcc.depose(600.0);
        assertEquals(0.08, ronAcc.getInterest(), "Interest should be 8% for balance >500");
    }

    @Test
    void totalSum() {
        assertEquals(103.0, ronAcc.getTotalAmount(), 0.001);
    }
    @Test
    void testEurTransfer() {
        assertThrows(Exception.class, () -> {
            eurAcc.transfer(ronAcc, 10.0);
        }, "EUR transfers should throw an exception");
    }
    @Test
    void transfer() throws Exception {
        RonAccount target = new RonAccount("TARGET", 50.0);
        ronAcc.transfer(target, 50.0);
        assertEquals(150.0, ronAcc.amount, "Receiver should have +50");
        assertEquals(0.0, target.amount, "Sender should have -50");
    }

    @Test
    void BuildTest1() {
        Client client = new Client.ClientBuilder("Ion Ion", "Timisoara").build();
        assertEquals("Ion Ion", client.getName());
        assertNull(client.getEmail(), "Email should be null if not set");
    }
    @Test
    void BuildTest2() {
        Client client = new Client.ClientBuilder("Vasile", "Brasov").setEmail("vasile@email.com")
                .setPhoneNumber("0712345678").build();
        assertEquals("vasile@email.com", client.getEmail());
        assertEquals("0712345678", client.getPhoneNumber());
    }
    @Test
    void AccFacRon() {
        Account acc = AccountFactory.createAccount("RON", "RON999", 50.0);
        assertTrue(acc instanceof RonAccount, "The AccountFactory class should create a RonAccount");
        assertEquals("RON999", acc.getAccountCode());
    }

    @Test
    void AccFacEur() {
        Account acc = AccountFactory.createAccount("EUR", "EUR999", 50.0);
        assertTrue(acc instanceof EurAccount, "The AccountFactory class should create an EurAccount");
        assertEquals(0.01, acc.getInterest(), 0.001);
    }
}