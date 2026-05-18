package src.ro.uvt.fi.dp;

import java.io.Serializable;

public class RetrieveCommand implements Command, Serializable {
    private static final long serialVersionUID = 1L;
    private Account account;
    private double amount;

    public RetrieveCommand(Account account, double amount) {
        this.account = account;
        this.amount  = amount;
    }

    @Override public void execute() throws Exception { account.retrieve(amount); }
    @Override public void undo()                     { account.depose(amount); }
}
