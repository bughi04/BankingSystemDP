package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Decorator Pattern: if the main account has insufficient funds, the
// withdrawal is automatically retried against a backup savings account.
public class SavingsBackupDecorator extends AccountDecorator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Account backupAccount;
    public SavingsBackupDecorator(Account mainAccount, Account backup) {
        super(mainAccount);
        this.backupAccount = backup;
    }
    @Override
    public void retrieve(double amount) throws Exception {
        try {
            decoratedAccount.retrieve(amount);
        } catch (Exception e) {
            if (e.getMessage().equals("Insufficient balance")) {
                Logger.getInstance().log("Main account " + decoratedAccount.getAccountCode()
                        + " insufficient. Trying backup: " + backupAccount.getAccountCode());
                backupAccount.retrieve(amount);
            } else {
                throw e;
            }
        }
    }
    public Account getBackupAccount() { return backupAccount; }
}
