# BankingSystemDP

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-4A90D9?logo=java)
![Design Patterns](https://img.shields.io/badge/Design%20Patterns-8-brightgreen)
![Serialization](https://img.shields.io/badge/Persistence-Java%20Serialization-yellow)
![JUnit](https://img.shields.io/badge/Tests-JUnit%205-25A162?logo=junit5)
![Status](https://img.shields.io/badge/Status-Complete-success)

A Java desktop application for managing a banking system, built as a university project to demonstrate and apply **8 classic software design patterns** in a real, working context. The project includes a fully functional Java Swing GUI for bank officers, persistent data storage, and live UI updates via the Observer pattern.

> **Note**: Some parts of the code were initially written with AI assistance and later reviewed/refactored/debugged by me.

---

## Design Patterns Implemented

| Pattern | Where it is used |
|---|---|
| **Singleton** | `Logger` - one shared logging instance across the whole application |
| **Builder** | `Client.ClientBuilder` - constructs clients with optional fields (email, phone) |
| **Factory** | `AccountFactory` - creates `RonAccount` or `EurAccount` from a type string |
| **Decorator** | `SavingsBackupDecorator` - wraps an account to add automatic fallthrough to a backup account when funds are insufficient |
| **Command** | `DeposeCommand`, `RetrieveCommand`, `TransferCommand` and `TransactionHistory` - supports full undo of any transaction |
| **Chain of Responsibility** | `BalanceCheckHandler`, `LimitCheckHandler`, `FraudCheckHandler` - layered transaction validation pipeline |
| **Observer** | `AccountObserver` interface - the GUI table refreshes automatically whenever any account balance changes |
| **Serialization** | `BankFileManager` - saves and restores the entire bank state to and from a `.ser` file |

---

## Features

### Bank Officer Dashboard
- View all clients and their accounts in a split-pane dashboard
- Add clients using a guided form (Builder pattern)
- Open RON or EUR accounts (Factory pattern)
- Perform deposits, withdrawals, and transfers with full undo support
- Enable Savings Backup on any account: if the main account runs out of funds, withdrawals automatically fall through to a chosen backup account (Decorator pattern)
- Save and load the entire bank state to a file so data persists between sessions

### Transaction Validation
Every withdrawal passes through a three-handler security chain before executing:
1. **BalanceCheckHandler** - blocks the transaction if funds are insufficient
2. **LimitCheckHandler** - blocks transactions above the 5,000 daily limit with a dedicated warning dialog
3. **FraudCheckHandler** - fraud screening checkpoint (extensible)

When a `SavingsBackupDecorator` is active on an account, the balance check is skipped at chain level. The decorator handles the insufficient-balance case itself by retrying against the backup account, so blocking it in the chain first would break that logic.

### Undo System
Every deposit, withdrawal, and transfer is wrapped in a `Command` object and pushed onto a stack in `TransactionHistory`. The Undo button pops the last command and calls its `undo()` method to reverse it exactly.

### Live UI Updates
`Account` holds a list of `AccountObserver` listeners. After every `depose()` or `retrieve()` call it notifies all registered observers. The GUI implements `AccountObserver` and updates the accounts table automatically without any manual refresh.

### Persistent Storage
`BankFileManager` serializes the entire `Bank` object graph (clients, accounts, balances) to a `.ser` file using Java's built-in `ObjectOutputStream`. On load, all objects are reconstructed and the GUI re-registers itself as an observer on each account.

---

## Technologies Used

- **Java 17+** - core application language
- **Java Swing** - desktop GUI
- **Java Serialization** (`java.io.Serializable`) - persistent storage
- **JUnit 5** - unit tests for account logic

---

## Project Structure

```
src/ro/uvt/fi/dp/
│
├── Account.java                 # Abstract base -- Serializable + Observer support
├── RonAccount.java              # RON account with tiered interest rates
├── EurAccount.java              # EUR account
├── AccountDecorator.java        # Decorator base class
├── SavingsBackupDecorator.java  # Concrete decorator -- backup account fallthrough
│
├── Client.java                  # Builder pattern
├── Bank.java                    # Top-level bank container
├── AccountFactory.java          # Factory pattern
├── AccountManager.java          # Core depose/retrieve/transfer logic
│
├── Command.java                 # Command interface
├── DeposeCommand.java           # Deposit + undo
├── RetrieveCommand.java         # Withdraw + undo
├── TransferCommand.java         # Transfer + undo
├── TransactionHistory.java      # Command stack for undo support
│
├── TransactionHandler.java      # Chain of Responsibility base
├── BalanceCheckHandler.java     # Handler 1 -- balance check
├── LimitCheckHandler.java       # Handler 2 -- daily limit check
├── FraudCheckHandler.java       # Handler 3 -- fraud check
│
├── AccountObserver.java         # Observer interface
├── Logger.java                  # Singleton logger
│
├── BankFileManager.java         # Save and load via serialization
├── BankOfficerGUI.java          # Main Swing dashboard
│
├── AccountTest.java             # JUnit 5 unit tests
└── Test.java                    # Original console-based integration test
```

---

## Getting Started

**Prerequisites:** Java 17+, IntelliJ IDEA or any Java IDE

```bash
# Clone the repository
git clone https://github.com/bughi04/BankingSystemDP.git
cd BankingSystemDP
```

**Run in IntelliJ:**
1. Open the project folder in IntelliJ IDEA
2. Set the SDK to Java 17+
3. Run `BankOfficerGUI.main()` for the full dashboard
4. Run `Test.main()` for the original console-based integration test

**Run unit tests:**
```bash
mvn test
```

---

## Usage

1. **Launch** -- the dashboard opens with an empty bank
2. **Add a client** -- click `+ Add Client` and fill in the form (name and address required, email and phone optional)
3. **Open an account** -- select a client, click `Add Account`, choose RON or EUR and set an initial balance
4. **Perform transactions** -- select an account row in the table, then use Deposit, Withdraw, or Transfer
5. **Enable Savings Backup** -- select a main account, click `Enable Savings Backup`, pick a backup account; future withdrawals that exceed the main balance will use the backup automatically
6. **Undo** -- click `Undo Last Operation` to reverse the most recent transaction
7. **Save / Load** -- use the File menu or the header buttons to save the bank state and reload it later

---

## Unit Tests

`AccountTest.java` covers the core account logic with JUnit 5:

| Test | What it verifies |
|---|---|
| `deposit` | Balance increases correctly after a deposit |
| `retrieve` | Balance decreases correctly after a withdrawal |
| `noBalance` | Withdrawing more than the balance throws the correct exception |
| `interest` | RON interest tiers (3% below 500, 8% at 500 and above) |
| `totalSum` | `getTotalAmount()` includes interest correctly |
| `testEurTransfer` | EUR account transfers throw an exception (RON only) |
| `transfer` | RON transfer moves funds between accounts correctly |
| `BuildTest1` | Builder creates a client with only the required fields |
| `BuildTest2` | Builder correctly sets optional fields (email, phone) |
| `AccFacRon` | Factory produces a `RonAccount` for type `"RON"` |
| `AccFacEur` | Factory produces an `EurAccount` with the correct interest rate |

---

## Key Design Decisions

**Why is the balance check skipped for SavingsBackupDecorator accounts?**
The `BalanceCheckHandler` only sees the main account's balance. If it runs before a decorated withdrawal, it blocks the transaction before the decorator's fallthrough logic ever gets a chance to run. To fix this, a second chain is built that starts from `LimitCheckHandler`, skipping the balance check entirely. The decorator handles the insufficient-funds case on its own.

**Why is the observer list marked transient?**
Java serialization would attempt to include the GUI object in the saved file if the observer list were persisted, which is both wrong and impractical. Marking it `transient` means the list is empty after loading, and `addObserversToAllAccounts()` re-registers the GUI right after deserialization.

**Why are the TransferCommand arguments swapped?**
The existing `Account.transfer(target, amount)` is pull-oriented: the caller gains funds and the target loses them. To get the intuitive result where the source loses and the target gains, `TransferCommand` is created with the arguments swapped. The undo logic is written to match this, so reversing a transfer works correctly.

---

## Possible Future Improvements

- Database backend to replace `.ser` files for larger datasets
- Interest calculation and monthly statement generation
- Transaction history visible in the GUI
- Export to PDF or CSV
- Role-based access control (teller vs. manager)
