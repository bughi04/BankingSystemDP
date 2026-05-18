package src.ro.uvt.fi.dp;

import java.io.Serializable;

// Balance check

public class BalanceCheckHandler extends TransactionHandler implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void handle(Account account, double amount) throws Exception {
        if (amount > account.getBalance()) {
            Logger.getInstance().log("Chain: Balance check FAILED for " + account.getAccountCode());
            throw new Exception("Insufficient balance");
        }
        Logger.getInstance().log("Chain: Balance check PASSED.");
        checkNext(account, amount);
    }
}

// Daily limit check

class LimitCheckHandler extends TransactionHandler implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void handle(Account account, double amount) throws Exception {
        if (amount > 5000) {
            Logger.getInstance().log("Chain: Limit check FAILED (Max 5000).");
            throw new Exception("Transaction exceeds daily limit");
        }
        Logger.getInstance().log("Chain: Limit check PASSED.");
        checkNext(account, amount);
    }
}

// Basic fraud check

class FraudCheckHandler extends TransactionHandler implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void handle(Account account, double amount) throws Exception {
        Logger.getInstance().log("Chain: Fraud check PASSED.");
        checkNext(account, amount);
    }
}
