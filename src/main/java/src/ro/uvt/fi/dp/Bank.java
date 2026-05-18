package src.ro.uvt.fi.dp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Client> clients;
	private String bankCode = null;

	public Bank(String codBanca) {
		this.bankCode = codBanca;
		this.clients  = new ArrayList<>();
	}

	public void addClient(Client c) {
		clients.add(c);
		Logger.getInstance().log("New client added to the bank: " + c.getName());
	}

	public Client getClient(String name) {
		for (Client c : clients) {
			if (c.getName().equals(name)) return c;
		}
		return null;
	}

	public List<Client> getClients() {
		return Collections.unmodifiableList(clients);
	}

	public String getBankCode() { return bankCode; }

	@Override
	public String toString() {
		return "Bank [code=" + bankCode + ", clients=" + clients + "]";
	}
}
