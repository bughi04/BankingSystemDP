package src.ro.uvt.fi.dp;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Bank Officer Dashboard - Java Swing GUI
public class BankOfficerGUI extends JFrame implements AccountObserver {

    // Accent colours
    private static final Color HEADER_BG    = new Color(26,  54, 112);
    private static final Color SEL_BG       = new Color(219, 234, 254);
    private static final Color SEL_FG       = new Color(26,  54, 112);

    private static final Color C_BLUE       = new Color(41,  98, 173);
    private static final Color C_GREEN      = new Color(22, 163,  74);
    private static final Color C_ORANGE     = new Color(180, 100, 20);
    private static final Color C_NAVY       = new Color(26,  54, 112);
    private static final Color C_PURPLE     = new Color(100, 60, 150);
    private static final Color C_RED        = new Color(185, 28,  28);
    private static final Color C_GRAY       = new Color(100, 100, 100);
    private static final Color WARNING_BG   = new Color(254, 249, 195);

    // State
    private Bank               bank;
    private Client             selectedClient;
    private TransactionHistory transactionHistory = new TransactionHistory();
    private TransactionHandler chainHead;
    private TransactionHandler limitChainHead;

    // UI components
    private DefaultListModel<String> clientListModel;
    private JList<String>            clientJList;
    private DefaultTableModel        accountTableModel;
    private JTable                   accountTable;
    private JLabel                   statusLabel;
    private JLabel                   clientInfoLabel;
    private JLabel                   bankTitleLabel;

    private JButton depositBtn, withdrawBtn, transferBtn, undoBtn,
            addAccountBtn, savingsBackupBtn;

    // Constructor
    public BankOfficerGUI() {
        setupChain();
        bank = new Bank("My Bank");
        setTitle("Bank Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 660);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        setupUI();
        refreshClientList();
        setVisible(true);
    }

    // Chain of responsibility
    private void setupChain() {
        BalanceCheckHandler balance = new BalanceCheckHandler();
        LimitCheckHandler   limit   = new LimitCheckHandler();
        FraudCheckHandler   fraud   = new FraudCheckHandler();
        LimitCheckHandler   limit2  = new LimitCheckHandler();
        FraudCheckHandler   fraud2  = new FraudCheckHandler();

        balance.setNext(limit);
        limit.setNext(fraud);
        chainHead = balance;

        limit2.setNext(fraud2);
        limitChainHead = limit2;
    }

    // UI construction
    private void setupUI() {
        setLayout(new BorderLayout());
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(HEADER_BG);
        menuBar.setBorder(new EmptyBorder(2, 4, 2, 4));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fileMenu.setForeground(Color.WHITE);

        fileMenu.add(menuItem("Save Bank Data", e -> saveData()));
        fileMenu.add(menuItem("Load Bank Data", e -> loadData()));
        fileMenu.addSeparator();
        fileMenu.add(menuItem("Exit",            e -> System.exit(0)));
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));

        bankTitleLabel = new JLabel("Bank Officer Dashboard");
        bankTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 21));
        bankTitleLabel.setForeground(Color.WHITE);

        // Header buttons
        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerBtns.setOpaque(false);
        JButton saveBtn = headerButton("Save");
        JButton loadBtn = headerButton("Load");
        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> loadData());
        headerBtns.add(loadBtn);
        headerBtns.add(saveBtn);

        header.add(bankTitleLabel, BorderLayout.WEST);
        header.add(headerBtns,    BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(5);
        split.setDividerLocation(235);
        split.setBorder(null);
        split.setBackground(Color.WHITE);
        split.setLeftComponent(buildClientPanel());
        split.setRightComponent(buildDetailPanel());
        add(split, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("  Ready  -  use the File menu or the Save / Load buttons to manage data.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBackground(new Color(242, 242, 242));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(190, 190, 190)),
                new EmptyBorder(5, 10, 5, 10)));
        add(statusLabel, BorderLayout.SOUTH);

        setOperationButtonsEnabled(false);
    }

    // Panel builders
    private JPanel buildClientPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1, new Color(190, 190, 190)),
                new EmptyBorder(12, 10, 10, 10)));

        JLabel heading = new JLabel("Clients");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        heading.setForeground(Color.BLACK);

        clientListModel = new DefaultListModel<>();
        clientJList     = new JList<>(clientListModel);
        clientJList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clientJList.setForeground(Color.BLACK);
        clientJList.setBackground(Color.WHITE);
        clientJList.setSelectionBackground(SEL_BG);
        clientJList.setSelectionForeground(SEL_FG);
        clientJList.setFixedCellHeight(30);
        clientJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onClientSelected();
        });

        JScrollPane scroll = new JScrollPane(clientJList);
        scroll.setBorder(new LineBorder(new Color(190, 190, 190)));

        JButton addClientBtn = accentButton("+ Add Client", C_NAVY);
        addClientBtn.addActionListener(e -> showAddClientDialog());

        panel.add(heading,       BorderLayout.NORTH);
        panel.add(scroll,        BorderLayout.CENTER);
        panel.add(addClientBtn,  BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Client info panel
        clientInfoLabel = new JLabel("<- Select a client to view their accounts");
        clientInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clientInfoLabel.setForeground(new Color(70, 70, 70));
        clientInfoLabel.setBackground(new Color(235, 243, 255));
        clientInfoLabel.setOpaque(true);
        clientInfoLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 180, 180)),
                new EmptyBorder(8, 12, 8, 12)));

        // Accounts table
        String[] cols = {"Account Code", "Type", "Balance", "Interest", "Total (w/ interest)"};
        accountTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        accountTable = new JTable(accountTableModel);
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountTable.setForeground(Color.BLACK);
        accountTable.setBackground(Color.WHITE);
        accountTable.setRowHeight(28);
        accountTable.setSelectionBackground(SEL_BG);
        accountTable.setSelectionForeground(SEL_FG);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.getTableHeader().setForeground(Color.BLACK);
        accountTable.getTableHeader().setBackground(new Color(235, 235, 235));
        accountTable.getTableHeader().setReorderingAllowed(false);
        accountTable.setGridColor(new Color(220, 220, 220));
        accountTable.setIntercellSpacing(new Dimension(8, 4));
        accountTable.setShowGrid(true);

        JScrollPane tableScroll = new JScrollPane(accountTable);
        tableScroll.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)),
                        "  Accounts  ", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12), Color.BLACK),
                new EmptyBorder(4, 4, 4, 4)));
        tableScroll.setBackground(Color.WHITE);

        panel.add(clientInfoLabel, BorderLayout.NORTH);
        panel.add(tableScroll,     BorderLayout.CENTER);
        panel.add(buildButtonBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBackground(new Color(245, 245, 245));
        bar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(190, 190, 190)),
                new EmptyBorder(4, 2, 4, 2)));

        addAccountBtn    = accentButton("Add Account",           C_BLUE);
        depositBtn       = accentButton("Deposit",               C_GREEN);
        withdrawBtn      = accentButton("Withdraw",              C_ORANGE);
        transferBtn      = accentButton("Transfer",              C_NAVY);
        savingsBackupBtn = accentButton("Enable Savings Backup", C_PURPLE);
        undoBtn          = accentButton("Undo Last Operation",   C_RED);

        addAccountBtn.addActionListener(e    -> showAddAccountDialog());
        depositBtn.addActionListener(e       -> showDepositDialog());
        withdrawBtn.addActionListener(e      -> showWithdrawDialog());
        transferBtn.addActionListener(e      -> showTransferDialog());
        savingsBackupBtn.addActionListener(e -> showEnableSavingsBackupDialog());
        undoBtn.addActionListener(e          -> performUndo());

        bar.add(addAccountBtn);
        bar.add(depositBtn);
        bar.add(withdrawBtn);
        bar.add(transferBtn);
        bar.add(savingsBackupBtn);

        JSeparator vSep = new JSeparator(JSeparator.VERTICAL);
        vSep.setPreferredSize(new Dimension(1, 28));
        vSep.setForeground(new Color(180, 180, 180));
        bar.add(vSep);

        bar.add(undoBtn);
        return bar;
    }

    // Buttons creators
    private JButton accentButton(String label, Color borderColor) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 2, true),
                new EmptyBorder(6, 14, 6, 14)));
        // Hover highlight
        btn.addMouseListener(new MouseAdapter() {
            private final Color normal = Color.WHITE;
            private final Color hover  = borderColor.brighter().brighter();
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(
                        Math.min(hover.getRed(),   240),
                        Math.min(hover.getGreen(), 240),
                        Math.min(hover.getBlue(),  240)));
            }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(normal); }
        });
        return btn;
    }

    private JButton headerButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(Color.WHITE, 2, true),
                new EmptyBorder(5, 16, 5, 16)));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(230, 230, 230)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private JMenuItem menuItem(String label, ActionListener al) {
        JMenuItem item = new JMenuItem(label);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setForeground(Color.BLACK);
        item.addActionListener(al);
        return item;
    }

    private void setOperationButtonsEnabled(boolean on) {
        addAccountBtn.setEnabled(on);
        depositBtn.setEnabled(on);
        withdrawBtn.setEnabled(on);
        transferBtn.setEnabled(on);
        savingsBackupBtn.setEnabled(on);
    }

    // Client selection panel
    private void onClientSelected() {
        String name = clientJList.getSelectedValue();
        if (name == null) {
            selectedClient = null;
            setOperationButtonsEnabled(false);
            clientInfoLabel.setText("<- Select a client to view their accounts");
            accountTableModel.setRowCount(0);
            return;
        }
        selectedClient = bank.getClient(name);
        if (selectedClient != null) {
            String email = selectedClient.getEmail()       != null ? "  |  Email: " + selectedClient.getEmail() : "";
            String phone = selectedClient.getPhoneNumber() != null ? "  |  Phone: " + selectedClient.getPhoneNumber() : "";
            clientInfoLabel.setText("<html><b>" + selectedClient.getName() + "</b>"
                    + "  -  " + selectedClient.getAddress() + email + phone + "</html>");
            setOperationButtonsEnabled(true);
            refreshAccountTable();
        }
    }

    // Refreshing clients/tables/accounts
    private void refreshClientList() {
        String sel = clientJList.getSelectedValue();
        clientListModel.clear();
        for (Client c : bank.getClients()) {
            clientListModel.addElement(c.getName());
        }
        if (sel != null) clientJList.setSelectedValue(sel, true);
        bankTitleLabel.setText(bank.getBankCode() + "  -  Officer Dashboard");
    }

    private void refreshAccountTable() {
        accountTableModel.setRowCount(0);
        if (selectedClient == null) return;
        for (Account acc : selectedClient.getAccounts()) {
            boolean isDecorated = acc instanceof AccountDecorator;
            String  code        = acc.getAccountCode() + (isDecorated ? " [BACKUP]" : "");
            accountTableModel.addRow(new Object[]{
                    code,
                    acc.type.toString(),
                    String.format("%.2f", acc.getBalance()),
                    String.format("%.0f%%", acc.getInterest() * 100),
                    String.format("%.2f", acc.getTotalAmount())
            });
        }
    }

    private Account getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row < 0 || selectedClient == null) return null;
        String code = ((String) accountTableModel.getValueAt(row, 0))
                .replace(" [BACKUP]", "").trim();
        return selectedClient.getAccount(code);
    }

    private Account findAccountByCode(String code) {
        for (Client c : bank.getClients()) {
            Account a = c.getAccount(code);
            if (a != null) return a;
        }
        return null;
    }

    // Dialogs
    private void showAddClientDialog() {
        JTextField nameField    = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField emailField   = new JTextField(20);
        JTextField phoneField   = new JTextField(20);

        JPanel form = formPanel(
                new String[]{"Name *", "Address *", "Email", "Phone"},
                new JComponent[]{nameField, addressField, emailField, phoneField});

        int ok = JOptionPane.showConfirmDialog(this, form, "Add New Client",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name    = nameField.getText().trim();
        String address = addressField.getText().trim();
        if (name.isEmpty() || address.isEmpty()) { showError("Name and Address are required."); return; }

        Client.ClientBuilder b = new Client.ClientBuilder(name, address);
        if (!emailField.getText().trim().isEmpty()) b.setEmail(emailField.getText().trim());
        if (!phoneField.getText().trim().isEmpty()) b.setPhoneNumber(phoneField.getText().trim());
        bank.addClient(b.build());
        refreshClientList();
        setStatus("Client '" + name + "' added.");
    }

    private void showAddAccountDialog() {
        if (selectedClient == null) return;

        JComboBox<String> typeBox   = new JComboBox<>(new String[]{"RON", "EUR"});
        JTextField        codeField = new JTextField(14);
        JTextField        amtField  = new JTextField("0.00", 14);

        JPanel form = formPanel(
                new String[]{"Account Type", "IBAN Code *", "Initial Balance"},
                new JComponent[]{typeBox, codeField, amtField});

        int ok = JOptionPane.showConfirmDialog(this, form, "Open New Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String type   = (String) typeBox.getSelectedItem();
            String code   = codeField.getText().trim();
            double amount = Double.parseDouble(amtField.getText().trim());
            if (code.isEmpty()) throw new IllegalArgumentException("IBAN code cannot be empty.");

            Account acc = AccountFactory.createAccount(type, code, amount);
            if (acc == null) throw new IllegalArgumentException("Unknown account type.");
            acc.addObserver(this);
            selectedClient.addAccount(acc);
            refreshAccountTable();
            setStatus("Account " + code + " (" + type + ") opened for " + selectedClient.getName() + ".");
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number for the initial balance.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void showDepositDialog() {
        Account acc = getSelectedAccount();
        if (acc == null) { showNoAccountWarning(); return; }

        String input = JOptionPane.showInputDialog(this,
                "<html>Deposit into account <b>" + acc.getAccountCode() + "</b><br>"
                        + "Current balance: <b>" + fmt(acc.getBalance()) + " " + acc.type + "</b></html>",
                "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        try {
            double amount = parsePositiveDouble(input);
            transactionHistory.execute(new DeposeCommand(acc, amount));
            setStatus("Deposited " + fmt(amount) + " " + acc.type + " into " + acc.getAccountCode() + ".");
        } catch (Exception ex) {
            showError("Deposit failed: " + ex.getMessage());
        }
    }

    private void showWithdrawDialog() {
        Account acc = getSelectedAccount();
        if (acc == null) { showNoAccountWarning(); return; }

        boolean hasBackup = acc instanceof SavingsBackupDecorator;

        String balanceInfo = hasBackup
                ? fmt(acc.getBalance()) + " " + acc.type + " (backup active)"
                : fmt(acc.getBalance()) + " " + acc.type;

        String input = JOptionPane.showInputDialog(this,
                "<html>Withdraw from account <b>" + acc.getAccountCode() + "</b><br>"
                        + "Current balance: <b>" + balanceInfo + "</b></html>",
                "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        try {
            double amount = parsePositiveDouble(input);

            // Chain of responsibility
            TransactionHandler activeChain = hasBackup ? limitChainHead : chainHead;
            try {
                activeChain.handle(acc, amount);
            } catch (Exception chainEx) {
                String msg = chainEx.getMessage();
                if (msg.contains("daily limit")) {
                    JPanel warn = new JPanel(new BorderLayout(12, 8));
                    warn.setBackground(WARNING_BG);
                    warn.setBorder(new EmptyBorder(14, 14, 14, 14));

                    JLabel icon = new JLabel("(!)", SwingConstants.CENTER);
                    icon.setFont(new Font("Segoe UI", Font.BOLD, 28));
                    icon.setForeground(new Color(160, 80, 0));
                    icon.setBorder(new EmptyBorder(0, 0, 0, 14));

                    JLabel text = new JLabel(
                            "<html><b>Transaction Limit Exceeded</b><br><br>"
                                    + "The requested amount of <b>" + fmt(amount) + "</b> exceeds<br>"
                                    + "the daily transaction limit of <b>5,000</b>.<br><br>"
                                    + "The transaction has been <b>blocked</b> by the security chain.<br>"
                                    + "<i>(LimitCheckHandler - Chain of Responsibility)</i></html>");
                    text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    text.setForeground(Color.BLACK);

                    warn.add(icon, BorderLayout.WEST);
                    warn.add(text, BorderLayout.CENTER);
                    JOptionPane.showMessageDialog(this, warn,
                            "Daily Limit Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    showError("Transaction blocked: " + msg);
                }
                return;
            }

            transactionHistory.execute(new RetrieveCommand(acc, amount));

            // Tell the officer which account actually paid when backup kicked in
            if (hasBackup) {
                setStatus("Withdrew " + fmt(amount) + " " + acc.type
                        + " from " + acc.getAccountCode()
                        + " (check logs — backup account may have been used).");
            } else {
                setStatus("Withdrew " + fmt(amount) + " " + acc.type + " from " + acc.getAccountCode() + ".");
            }
        } catch (Exception ex) {
            showError("Withdrawal failed: " + ex.getMessage());
        }
    }

    private void showTransferDialog() {
        Account sourceAcc = getSelectedAccount();
        if (sourceAcc == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select the SOURCE account in the table first.",
                    "No Account Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sourceAcc.type != Account.TYPE.RON) {
            showError("Transfers are only supported for RON accounts.");
            return;
        }

        List<String> options = new ArrayList<>();
        for (Client c : bank.getClients()) {
            for (Account a : c.getAccounts()) {
                if (!a.getAccountCode().equals(sourceAcc.getAccountCode())
                        && a.type == Account.TYPE.RON) {
                    options.add(c.getName() + "  -  " + a.getAccountCode());
                }
            }
        }
        if (options.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No other RON accounts are available for transfer.",
                    "Transfer", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> targetBox = new JComboBox<>(options.toArray(new String[0]));
        JTextField        amtField  = new JTextField("0.00", 14);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; form.add(blackLabel("From (source):"), g);
        g.gridx = 1; form.add(new JLabel("<html><b>" + sourceAcc.getAccountCode()
                + "</b>  [" + fmt(sourceAcc.getBalance()) + " RON]</html>"), g);
        g.gridx = 0; g.gridy = 1; form.add(blackLabel("To (target):"), g);
        g.gridx = 1; form.add(targetBox, g);
        g.gridx = 0; g.gridy = 2; form.add(blackLabel("Amount:"), g);
        g.gridx = 1; form.add(amtField, g);

        int ok = JOptionPane.showConfirmDialog(this, form, "Transfer Funds",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            double amount     = parsePositiveDouble(amtField.getText());
            String sel        = (String) targetBox.getSelectedItem();
            String targetCode = sel.split("  -  ")[1].trim();
            Account targetAcc = findAccountByCode(targetCode);
            if (targetAcc == null) throw new Exception("Target account not found.");

            transactionHistory.execute(new TransferCommand(targetAcc, sourceAcc, amount));
            refreshAccountTable();
            setStatus("Transferred " + fmt(amount) + " RON from "
                    + sourceAcc.getAccountCode() + " to " + targetCode + ".");
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount.");
        } catch (Exception ex) {
            showError("Transfer failed: " + ex.getMessage());
        }
    }

    private void showEnableSavingsBackupDialog() {
        Account mainAcc = getSelectedAccount();
        if (mainAcc == null) { showNoAccountWarning(); return; }
        if (mainAcc instanceof AccountDecorator) {
            showError("This account already has a backup decorator applied.");
            return;
        }

        List<String> backupOptions = new ArrayList<>();
        for (Account a : selectedClient.getAccounts()) {
            if (!a.getAccountCode().equals(mainAcc.getAccountCode())) {
                backupOptions.add(a.getAccountCode() + "  [" + fmt(a.getBalance()) + " " + a.type + "]");
            }
        }
        if (backupOptions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "The client needs at least two accounts to enable Savings Backup.",
                    "Not Enough Accounts", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> backupBox = new JComboBox<>(backupOptions.toArray(new String[0]));
        JPanel form = formPanel(
                new String[]{"Main account", "Backup account"},
                new JComponent[]{
                        new JLabel("<html><b>" + mainAcc.getAccountCode() + "</b></html>"),
                        backupBox});

        int ok = JOptionPane.showConfirmDialog(this, form,
                "Enable Savings Backup (Decorator Pattern)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String backupCode = ((String) backupBox.getSelectedItem()).split("  \\[")[0].trim();
        Account backupAcc = selectedClient.getAccount(backupCode);

        Account decorated = new SavingsBackupDecorator(mainAcc, backupAcc);
        decorated.addObserver(this);
        selectedClient.replaceAccount(mainAcc.getAccountCode(), decorated);
        refreshAccountTable();

        JLabel info = new JLabel(
                "<html><b>Savings Backup enabled!</b><br><br>"
                        + "Main account:   <b>" + mainAcc.getAccountCode() + "</b><br>"
                        + "Backup account: <b>" + backupCode + "</b><br><br>"
                        + "When the main account has insufficient funds, withdrawals<br>"
                        + "automatically fall through to the backup account.<br><br>"
                        + "<i>Uses the Decorator design pattern.</i></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(Color.BLACK);
        info.setBorder(new EmptyBorder(8, 8, 8, 8));
        JOptionPane.showMessageDialog(this, info, "Savings Backup Active", JOptionPane.INFORMATION_MESSAGE);

        setStatus("Savings Backup applied: " + mainAcc.getAccountCode() + " -> " + backupCode + ".");
    }

    private void performUndo() {
        if (!transactionHistory.hasHistory()) {
            JOptionPane.showMessageDialog(this, "There are no operations to undo.",
                    "Nothing to Undo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            transactionHistory.undoLast();
            refreshAccountTable();
            setStatus("Last operation undone successfully.");
        } catch (Exception ex) {
            showError("Undo failed: " + ex.getMessage());
        }
    }

    // File operations
    private void saveData() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Bank Data");
        fc.setSelectedFile(new File("bank_data.ser"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            BankFileManager.save(bank, fc.getSelectedFile().getAbsolutePath());
            setStatus("Saved to: " + fc.getSelectedFile().getName());
            JOptionPane.showMessageDialog(this,
                    "Bank data saved successfully!\n" + fc.getSelectedFile().getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Save failed: " + ex.getMessage());
        }
    }

    private void loadData() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load Bank Data");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            bank = BankFileManager.load(fc.getSelectedFile().getAbsolutePath());
            addObserversToAllAccounts();
            selectedClient = null;
            refreshClientList();
            accountTableModel.setRowCount(0);
            clientInfoLabel.setText("<- Select a client to view their accounts");
            setOperationButtonsEnabled(false);
            setStatus("Loaded: " + fc.getSelectedFile().getName()
                    + "  (" + bank.getClients().size() + " client(s))");
            JOptionPane.showMessageDialog(this,
                    "Bank data loaded successfully!\n" + bank.getClients().size() + " client(s) found.",
                    "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Load failed: " + ex.getMessage());
        }
    }

    private void addObserversToAllAccounts() {
        for (Client c : bank.getClients()) {
            for (Account a : c.getAccounts()) {
                a.addObserver(this);
            }
        }
    }

    @Override
    public void onAccountUpdated(Account account) {
        SwingUtilities.invokeLater(() -> {
            refreshAccountTable();
            setStatus("Account " + account.getAccountCode()
                    + " updated - balance: " + fmt(account.getBalance()) + " " + account.type);
        });
    }

    // Other utilities
    private JPanel formPanel(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(blackLabel(labels[i] + ":"), g);
            g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(fields[i], g);
        }
        return p;
    }

    private JLabel blackLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private double parsePositiveDouble(String s) throws IllegalArgumentException {
        try {
            double v = Double.parseDouble(s.trim());
            if (v <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Please enter a valid positive number.");
        }
    }

    private String fmt(double v)       { return String.format("%.2f", v); }
    private void   setStatus(String m) { statusLabel.setText("  " + m); }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showNoAccountWarning() {
        JOptionPane.showMessageDialog(this,
                "Please select an account from the table first.",
                "No Account Selected", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankOfficerGUI::new);
    }
}