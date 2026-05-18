package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Decorator base class. All balance/interest queries are delegated to the
// wrapped account so the GUI always sees correct values.
public abstract class AccountDecorator extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Account decoratedAccount;

    public AccountDecorator(Account decoratedAccount) {
        super(decoratedAccount.getAccountCode(), 0, decoratedAccount.type);
        this.decoratedAccount = decoratedAccount;
        this.amount = decoratedAccount.amount;
    }

    @Override public double getInterest()    { return decoratedAccount.getInterest(); }
    @Override public double getBalance()     { return decoratedAccount.getBalance(); }
    @Override public double getTotalAmount() { return decoratedAccount.getTotalAmount(); }

    @Override
    public void depose(double amount) {
        if (decoratedAccount != null) decoratedAccount.depose(amount);
    }

    @Override
    public void retrieve(double amount) throws Exception {
        if (decoratedAccount != null) decoratedAccount.retrieve(amount);
    }
}