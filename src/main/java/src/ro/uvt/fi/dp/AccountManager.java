package src.ro.uvt.fi.dp;

import java.io.Serializable;

public class AccountManager implements Serializable {

    private static final long serialVersionUID = 1L;

    public void depose(Account account, double amount) {
        account.amount += amount;
        Logger.getInstance().log("Deposited " + amount + " to account " + account.getAccountCode());
    }

    public void retrieve(Account account, double amount) throws Exception {
        if (amount > account.amount) {
            Logger.getInstance().log("Withdrawal failed! Insufficient funds in " + account.getAccountCode());
            throw new Exception("Insufficient balance");
        }
        account.amount -= amount;
        Logger.getInstance().log("Withdrew " + amount + " from account " + account.getAccountCode());
    }

    public void transfer(Transfer source, AccountTransaction c, double s) throws Exception {
        if (source instanceof Account && Account.TYPE.RON == ((Account) source).type) {
            c.retrieve(s);
            ((AccountTransaction) source).depose(s);
            Logger.getInstance().log("Transfer successful from " + ((Account) source).getAccountCode());
        } else {
            Logger.getInstance().log("Transfer failed! Unsupported account type");
            throw new Exception("Transfer is only supported for RON accounts.");
        }
    }
}