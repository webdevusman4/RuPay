import { CryptoAsset, Transaction, User, Withdrawal } from "./types";

export const currentUser: User = {
  id: "1",
  name: "Ahmed Khan",
  email: "ahmed@example.com",
  pkrBalance: 125000,
  joinDate: "2024-01-15",
};

export const cryptoAssets: CryptoAsset[] = [
  {
    id: "btc",
    symbol: "BTC",
    name: "Bitcoin",
    icon: "₿",
    priceInPKR: 24850000,
    change24h: 2.45,
    holdings: 0.0025,
  },
  {
    id: "eth",
    symbol: "ETH",
    name: "Ethereum",
    icon: "Ξ",
    priceInPKR: 875000,
    change24h: -1.23,
    holdings: 0.15,
  },
  {
    id: "usdt",
    symbol: "USDT",
    name: "Tether",
    icon: "₮",
    priceInPKR: 279,
    change24h: 0.01,
    holdings: 500,
  },
  {
    id: "bnb",
    symbol: "BNB",
    name: "BNB",
    icon: "◆",
    priceInPKR: 168500,
    change24h: 3.67,
    holdings: 0.5,
  },
  {
    id: "xrp",
    symbol: "XRP",
    name: "Ripple",
    icon: "✕",
    priceInPKR: 145,
    change24h: -0.89,
    holdings: 1000,
  },
];

export const recentTransactions: Transaction[] = [
  {
    id: "1",
    type: "buy",
    coin: "BTC",
    amount: 0.001,
    pkrValue: 24850,
    date: "2024-03-15T10:30:00",
    status: "completed",
  },
  {
    id: "2",
    type: "deposit",
    amount: 50000,
    pkrValue: 50000,
    date: "2024-03-14T15:45:00",
    status: "completed",
  },
  {
    id: "3",
    type: "sell",
    coin: "ETH",
    amount: 0.05,
    pkrValue: 43750,
    date: "2024-03-13T09:20:00",
    status: "completed",
  },
  {
    id: "4",
    type: "transfer",
    coin: "USDT",
    amount: 100,
    pkrValue: 27900,
    date: "2024-03-12T14:10:00",
    status: "completed",
    recipient: "ali@example.com",
  },
  {
    id: "5",
    type: "withdraw",
    amount: 25000,
    pkrValue: 25000,
    date: "2024-03-11T11:00:00",
    status: "pending",
  },
];

export const allUsers: User[] = [
  currentUser,
  {
    id: "2",
    name: "Sara Ali",
    email: "sara@example.com",
    pkrBalance: 85000,
    joinDate: "2024-02-20",
  },
  {
    id: "3",
    name: "Usman Malik",
    email: "usman@example.com",
    pkrBalance: 320000,
    joinDate: "2024-01-05",
  },
  {
    id: "4",
    name: "Fatima Hassan",
    email: "fatima@example.com",
    pkrBalance: 45000,
    joinDate: "2024-03-01",
  },
];

export const pendingWithdrawals: Withdrawal[] = [
  {
    id: "w1",
    userId: "2",
    userName: "Sara Ali",
    amount: 15000,
    date: "2024-03-15T08:00:00",
    status: "pending",
  },
  {
    id: "w2",
    userId: "3",
    userName: "Usman Malik",
    amount: 50000,
    date: "2024-03-14T16:30:00",
    status: "pending",
  },
];
