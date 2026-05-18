package src.ro.uvt.fi.dp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements AccountQuery, AccountTransaction, Transfer, Serializable {

	private static final long serialVersionUID = 1L;

	public static enum TYPE {
		EUR, RON
	}

	String accountCode = null;
	double amount = 0;
	Account.TYPE type = Account.TYPE.RON;
	private AccountManager manager = new AccountManager();

	private transient List<AccountObserver> observers;

	protected Account(String accountCode, double amount, Account.TYPE type) {
		this.accountCode = accountCode;
		this.type = type;
		depose(amount);
	}

	// Observer helpers

	public void addObserver(AccountObserver obs) {
		if (observers == null) observers = new ArrayList<>();
		observers.add(obs);
	}

	public void removeObserver(AccountObserver obs) {
		if (observers != null) observers.remove(obs);
	}

	protected void notifyObservers() {
		if (observers != null) {
			for (AccountObserver obs : observers) {
				obs.onAccountUpdated(this);
			}
		}
	}

	// AccountQuery

	@Override
	public double getTotalAmount() {
		return amount + amount * getInterest();
	}

	public double getBalance() {
		return amount;
	}

	// AccountTransaction

	@Override
	public void depose(double amount) {
		manager.depose(this, amount);
		notifyObservers();
	}

	@Override
	public void retrieve(double amount) throws Exception {
		manager.retrieve(this, amount);
		notifyObservers();
	}

	// Transfer

	@Override
	public void transfer(AccountTransaction c, double s) throws Exception {
		manager.transfer(this, c, s);
	}

	// Serialization helper

	private void readObject(java.io.ObjectInputStream in)
			throws java.io.IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (manager == null)   manager   = new AccountManager();
		if (observers == null) observers = new ArrayList<>();
	}

	@Override
	public String toString() {
		return (Account.TYPE.RON == this.type)
				? "Account RON: code=" + accountCode + ", amount=" + amount
				: "Account EUR: code=" + accountCode + ", amount=" + amount;
	}

	public String getAccountCode() { return accountCode; }

	public abstract double getInterest();
}
