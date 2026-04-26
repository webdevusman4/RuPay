# RuPay Exchange - JavaFX Desktop Application

A cryptocurrency exchange desktop application built with JavaFX, featuring a dark theme inspired by Binance.

## Features

- **10 Complete Screens:**
  - Login & Registration
  - Dashboard with crypto prices
  - Buy/Sell crypto
  - Transfer crypto to other users
  - Wallet with deposit/withdraw
  - Transaction history with filters
  - PIN confirmation popup
  - Admin panel

- **Dark Theme** with gold accent colors
- **Mock Data** for testing without backend

## Requirements

- Java 17 or higher
- Maven 3.6+

## How to Run

### Option 1: Using Maven

```bash
cd simplex-javafx
mvn clean javafx:run
```

### Option 2: Using IDE

1. Import the project as a Maven project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Run the `SimpleXApp.java` main class

## Demo Credentials

- **User Account:** ahmed@example.com (any password)
- **Admin Account:** admin@rupay.com (any password)
- **Default PIN:** 1234 (for ahmed) or 0000 (for admin)

## Project Structure

```
simplex-javafx/
├── pom.xml                          # Maven configuration
├── src/main/java/com/simplex/
│   ├── SimpleXApp.java              # Main application entry
│   ├── controllers/
│   │   └── NavigationController.java
│   ├── models/
│   │   ├── User.java
│   │   ├── Crypto.java
│   │   ├── Transaction.java
│   │   └── WithdrawRequest.java
│   ├── services/
│   │   └── DataService.java         # Mock data & business logic
│   └── views/
│       ├── BaseView.java
│       ├── LoginView.java
│       ├── RegisterView.java
│       ├── DashboardView.java
│       ├── BuyView.java
│       ├── SellView.java
│       ├── TransferView.java
│       ├── WalletView.java
│       ├── HistoryView.java
│       ├── PinPopupView.java
│       └── AdminView.java
└── src/main/resources/styles/
    └── main.css                      # Dark theme stylesheet
```

## Color Palette

- Background: `#0B0E11`
- Card: `#1E2329`
- Gold Accent: `#F0B90B`
- Success Green: `#0ECB81`
- Error Red: `#F6465D`
- Text: `#EAECEF`
