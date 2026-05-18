package src.ro.uvt.fi.dp;

import java.io.Serializable;

public class EurAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    public EurAccount(String accountCode, double amount) {
        super(accountCode, amount, TYPE.EUR);
    }

    @Override
    public double getInterest() {
        return 0.01;
    }
}
