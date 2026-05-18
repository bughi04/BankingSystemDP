package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Chain of responsibility base

public abstract class TransactionHandler implements Serializable {
    private static final long serialVersionUID = 1L;
    protected TransactionHandler next;

    public void setNext(TransactionHandler next) { this.next = next; }

    public abstract void handle(Account account, double amount) throws Exception;

    protected void checkNext(Account account, double amount) throws Exception {
        if (next != null) next.handle(account, amount);
    }
}
