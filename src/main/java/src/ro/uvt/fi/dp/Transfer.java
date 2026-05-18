package src.ro.uvt.fi.dp;

public interface Transfer {
	void transfer(AccountTransaction account, double amount) throws Exception;
}
