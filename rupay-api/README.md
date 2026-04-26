# RuPay Exchange - Spring Boot API

A REST API backend for the RuPay Cryptocurrency Exchange application.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security + JWT Authentication**
- **Spring Data JPA**
- **H2 Database (Development) / PostgreSQL (Production)**
- **Lombok**

## Project Structure

```
rupay-api/
├── src/main/java/com/rupay/
│   ├── RuPayApplication.java          # Main application entry
│   ├── config/
│   │   └── DataInitializer.java       # Seeds demo data
│   ├── controller/
│   │   ├── AuthController.java        # Login, Register, Profile
│   │   ├── CryptoController.java      # Get crypto prices
│   │   ├── TradeController.java       # Buy, Sell, Transfer
│   │   ├── WalletController.java      # Deposit, Withdraw, History
│   │   └── AdminController.java       # Admin dashboard, approvals
│   ├── dto/                           # Data Transfer Objects
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Crypto.java
│   │   ├── Wallet.java
│   │   ├── Transaction.java
│   │   └── WithdrawRequest.java
│   ├── repository/                    # JPA Repositories
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   ├── JwtService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   └── service/
│       ├── AuthService.java
│       ├── CryptoService.java
│       ├── TradeService.java
│       ├── WalletService.java
│       └── AdminService.java
└── src/main/resources/
    └── application.yml
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Development Mode

```bash
cd rupay-api
mvn spring-boot:run
```

The API will start at `http://localhost:8080`

### Building for Production

```bash
mvn clean package
java -jar target/rupay-api-1.0.0.jar
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| POST | `/api/auth/verify-pin` | Verify user PIN |
| GET | `/api/auth/me` | Get current user profile |

### Cryptocurrency
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/crypto` | Get all cryptocurrencies |
| GET | `/api/crypto/{symbol}` | Get crypto by symbol |

### Trading (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/trade/buy` | Buy cryptocurrency |
| POST | `/api/trade/sell` | Sell cryptocurrency |
| POST | `/api/trade/transfer` | Transfer crypto to another user |

### Wallet (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/wallet` | Get wallet balance |
| POST | `/api/wallet/deposit` | Deposit PKR |
| POST | `/api/wallet/withdraw` | Request withdrawal |
| GET | `/api/wallet/transactions` | Get transaction history |
| GET | `/api/wallet/transactions/recent` | Get recent transactions |

### Admin (Admin Only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Get admin dashboard |
| GET | `/api/admin/users` | Get all users |
| GET | `/api/admin/withdrawals/pending` | Get pending withdrawals |
| POST | `/api/admin/withdrawals/{id}/process` | Approve/reject withdrawal |

## Demo Credentials

### Admin User
- Email: `admin@rupay.com`
- Password: `admin123`
- PIN: `0000`

### Demo User
- Email: `ahmed@example.com`
- Password: `password123`
- PIN: `1234`
- Starting Balance: PKR 150,000

## Request/Response Examples

### Login
```json
POST /api/auth/login
{
  "email": "ahmed@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 2,
    "name": "Ahmed Khan",
    "email": "ahmed@example.com",
    "isAdmin": false
  }
}
```

### Buy Crypto
```json
POST /api/trade/buy
Authorization: Bearer <token>
{
  "cryptoSymbol": "BTC",
  "cryptoAmount": 0.001,
  "pin": "1234"
}

Response:
{
  "success": true,
  "message": "Purchase successful",
  "data": {
    "transactionId": 1,
    "type": "BUY",
    "cryptoSymbol": "BTC",
    "cryptoAmount": 0.001,
    "pkrAmount": 25430,
    "priceAtTransaction": 25430000,
    "status": "COMPLETED",
    "message": "Successfully bought 0.001 BTC"
  }
}
```

## Environment Variables

For production, set the following environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rupay
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_very_long_secret_key_for_jwt_tokens
```

## H2 Console (Development)

Access the H2 database console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:rupaydb`
- Username: `sa`
- Password: (empty)

## Connecting Frontend

Set the `NEXT_PUBLIC_API_URL` environment variable in your Next.js frontend:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

Then use the API service from `lib/api.ts` to make requests.
