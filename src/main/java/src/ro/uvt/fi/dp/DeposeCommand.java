package src.ro.uvt.fi.dp;

import java.io.Serializable;

public class DeposeCommand implements Command, Serializable {
    private static final long serialVersionUID = 1L;
    private Account account;
    private double amount;

    public DeposeCommand(Account account, double amount) {
        this.account = account;
        this.amount  = amount;
    }

    @Override public void execute()               { account.depose(amount); }
    @Override public void undo() throws Exception { account.retrieve(amount); }
}
