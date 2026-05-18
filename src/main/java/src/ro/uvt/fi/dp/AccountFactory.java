package src.ro.uvt.fi.dp;

public class AccountFactory
{
    public static Account createAccount(String type, String accountCode, double amount) {
        if (type.equalsIgnoreCase("RON"))
        {
            return new RonAccount(accountCode, amount);
        } else if (type.equalsIgnoreCase("EUR"))
        {
            return new EurAccount(accountCode, amount);
        }
        return null;
    }
}