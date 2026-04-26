export interface User {
  id: string;
  name: string;
  email: string;
  pkrBalance: number;
  joinDate: string;
}

export interface CryptoAsset {
  id: string;
  symbol: string;
  name: string;
  icon: string;
  priceInPKR: number;
  change24h: number;
  holdings?: number;
}

export interface Transaction {
  id: string;
  type: 'buy' | 'sell' | 'deposit' | 'withdraw' | 'transfer';
  coin?: string;
  amount: number;
  pkrValue: number;
  date: string;
  status: 'completed' | 'pending' | 'failed';
  recipient?: string;
}

export interface Withdrawal {
  id: string;
  userId: string;
  userName: string;
  amount: number;
  date: string;
  status: 'pending' | 'approved' | 'rejected';
}
