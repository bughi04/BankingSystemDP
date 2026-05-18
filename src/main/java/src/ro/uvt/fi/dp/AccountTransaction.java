package src.ro.uvt.fi.dp;

public interface AccountTransaction {
    void depose(double amount);
    void retrieve(double amount) throws Exception;
}