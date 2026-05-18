package src.ro.uvt.fi.dp;

import java.io.Serializable;

public class RonAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    public RonAccount(String accountCode, double amount) {
        super(accountCode, amount, TYPE.RON);
    }

    @Override
    public double getInterest() {
        return (amount < 500) ? 0.03 : 0.08;
    }
}