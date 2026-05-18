package src.ro.uvt.fi.dp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String address;
	private String email;
	private String phoneNumber;
	private List<Account> accounts;

	private Client(ClientBuilder builder) {
		this.name        = builder.name;
		this.address     = builder.address;
		this.email       = builder.email;
		this.phoneNumber = builder.phoneNumber;
		this.accounts    = new ArrayList<>();
	}

	// Builder

	public static class ClientBuilder {
		private final String name;
		private final String address;
		private String email;
		private String phoneNumber;

		public ClientBuilder(String name, String address) {
			this.name    = name;
			this.address = address;
		}
		public ClientBuilder setEmail(String email)           { this.email = email; return this; }
		public ClientBuilder setPhoneNumber(String phone)     { this.phoneNumber = phone; return this; }
		public Client build()                                 { return new Client(this); }
	}

	// Account management
	public void addAccount(Account a) {
		accounts.add(a);
	}

	public Account getAccount(String accountCode) {
		for (Account a : accounts) {
			if (a.getAccountCode().equals(accountCode)) return a;
		}
		return null;
	}

	public List<Account> getAccounts() {
		return Collections.unmodifiableList(accounts);
	}
	public void replaceAccount(String accountCode, Account newAccount) {
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).getAccountCode().equals(accountCode)) {
				accounts.set(i, newAccount);
				return;
			}
		}
	}
	public String getName()        { return name; }
	public String getAddress()     { return address; }
	public String getEmail()       { return email; }
	public String getPhoneNumber() { return phoneNumber; }

	@Override
	public String toString() {
		return "\n\tClient [name=" + name + ", address=" + address + ", accounts=" + accounts + "]";
	}
}
