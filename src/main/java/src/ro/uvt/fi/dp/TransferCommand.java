package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Executes a transfer
public class TransferCommand implements Command, Serializable {
    private static final long serialVersionUID = 1L;
    private Account source;
    private Account target;
    private double  amount;
    public TransferCommand(Account source, Account target, double amount) {
        this.source = source;
        this.target = target;
        this.amount = amount;
    }
    @Override
    public void execute() throws Exception {
        source.transfer(target, amount);
    }
    @Override
    public void undo() throws Exception {
        source.retrieve(amount);
        target.depose(amount);
    }
}
