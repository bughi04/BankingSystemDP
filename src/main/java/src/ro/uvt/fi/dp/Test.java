package src.ro.uvt.fi.dp;

public class Test {

	public static void main(String[] args) throws Exception {
		/**
		 * Create BCR bank with 2 clients
		 */
		Bank bcr = new Bank("BCR Bank");
		// Client Ionescu has an EUR and a RON account
		Client cl1 = new Client.ClientBuilder("Ionescu Ion", "Timisoara").build();
		Account acc1 = AccountFactory.createAccount("EUR","EUR124", 200.9);
		cl1.addAccount(acc1);
		bcr.addClient(cl1);
		cl1.addAccount(AccountFactory.createAccount("RON","RON1234", 400));
		// Client Marinescu has a RON account
		Client cl2 = new Client.ClientBuilder("Marinescu Marin", "Timisoara").build();
		cl2.addAccount(AccountFactory.createAccount("RON","RON126", 100));
		bcr.addClient(cl2);
		System.out.println(bcr);

		/**
		 * Create bank CEC with one client
		 */
		Bank cec = new Bank("CEC Bank");
		Client clientCEC = new Client.ClientBuilder("Vasilescu Vasile", "Brasov").build();
		Account accCEC = AccountFactory.createAccount("EUR","EUR128", 700);
		clientCEC.addAccount(accCEC);
		cec.addClient(clientCEC);
		System.out.println(cec);

		/**
		 * Perform operations on client accounts
		 */
		Client cl = bcr.getClient("Marinescu Marin");
		if (cl != null) {
			cl.getAccount("RON126").depose(400);
			System.out.println(cl);
		}

		if (cl != null) {
			cl.getAccount("RON126").retrieve(67);
			System.out.println(cl);
		}
		Account a1 = cl.getAccount("RON126");
		Account a2 = bcr.getClient("Ionescu Ion").getAccount("RON1234");
		a1.transfer(a2, 40);
		System.out.println(bcr);

		// Testing for the new decorator function with taking out money from another
		// account in case the main one is empty
		Account mainAccount = AccountFactory.createAccount("RON", "MAIN_ACC", 50.0);
		Account savingsAccount = AccountFactory.createAccount("RON", "SAVINGS_ACC", 1000.0);
		Account decoratedAcc = new SavingsBackupDecorator(mainAccount, savingsAccount);

		TransactionHistory history = new TransactionHistory();
		Account acc = AccountFactory.createAccount("RON", "UNDO_TEST", 100.0);
		System.out.println("Balance after deposit: " + acc.getTotalAmount());

		history.execute(new DeposeCommand(acc, 500.0));
		history.undoLast();
		System.out.println("Balance after undo: " + acc.getTotalAmount());

	}
	

}
