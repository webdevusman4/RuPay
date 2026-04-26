// RuPay API Service - Connects to Spring Boot Backend
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Types
export interface User {
  id: number;
  name: string;
  email: string;
  pkrBalance: number;
  isAdmin: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  isAdmin: boolean;
}

export interface Crypto {
  id: number;
  symbol: string;
  name: string;
  priceInPkr: number;
  change24h: number;
  icon: string;
}

export interface CryptoHolding {
  symbol: string;
  name: string;
  balance: number;
  currentPrice: number;
  valueInPkr: number;
  change24h: number;
}

export interface WalletResponse {
  pkrBalance: number;
  cryptoHoldings: CryptoHolding[];
  totalValueInPkr: number;
}

export interface Transaction {
  id: number;
  type: string;
  cryptoSymbol?: string;
  cryptoName?: string;
  cryptoAmount?: number;
  pkrAmount: number;
  priceAtTransaction?: number;
  recipientEmail?: string;
  status: string;
  createdAt: string;
}

export interface TradeResponse {
  transactionId: number;
  type: string;
  cryptoSymbol: string;
  cryptoAmount: number;
  pkrAmount: number;
  priceAtTransaction: number;
  status: string;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface AdminDashboard {
  totalUsers: number;
  pendingWithdrawals: number;
  totalDeposits: number;
  totalWithdrawals: number;
  recentUsers: UserSummary[];
  pendingRequests: WithdrawRequestSummary[];
}

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  pkrBalance: number;
  isActive: boolean;
  createdAt: string;
}

export interface WithdrawRequestSummary {
  id: number;
  userName: string;
  userEmail: string;
  amount: number;
  bankAccount: string;
  status: string;
  createdAt: string;
}

// Token management
let authToken: string | null = null;

export const setAuthToken = (token: string | null) => {
  authToken = token;
  if (token) {
    localStorage.setItem('rupay_token', token);
  } else {
    localStorage.removeItem('rupay_token');
  }
};

export const getAuthToken = (): string | null => {
  if (authToken) return authToken;
  if (typeof window !== 'undefined') {
    authToken = localStorage.getItem('rupay_token');
  }
  return authToken;
};

// API helper
const apiCall = async <T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> => {
  const token = getAuthToken();
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  const data = await response.json();
  
  if (!response.ok) {
    throw new Error(data.message || 'API request failed');
  }

  return data;
};

// Auth API
export const authApi = {
  login: async (email: string, password: string): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiCall<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
    if (response.success && response.data.token) {
      setAuthToken(response.data.token);
    }
    return response;
  },

  register: async (
    name: string,
    email: string,
    password: string,
    pin: string
  ): Promise<ApiResponse<AuthResponse>> => {
    const response = await apiCall<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password, pin }),
    });
    if (response.success && response.data.token) {
      setAuthToken(response.data.token);
    }
    return response;
  },

  verifyPin: async (pin: string): Promise<ApiResponse<boolean>> => {
    return apiCall<boolean>('/auth/verify-pin', {
      method: 'POST',
      body: JSON.stringify({ pin }),
    });
  },

  getProfile: async (): Promise<ApiResponse<User>> => {
    return apiCall<User>('/auth/me');
  },

  logout: () => {
    setAuthToken(null);
  },
};

// Crypto API
export const cryptoApi = {
  getAll: async (): Promise<ApiResponse<Crypto[]>> => {
    return apiCall<Crypto[]>('/crypto');
  },

  getBySymbol: async (symbol: string): Promise<ApiResponse<Crypto>> => {
    return apiCall<Crypto>(`/crypto/${symbol}`);
  },
};

// Trade API
export const tradeApi = {
  buy: async (
    cryptoSymbol: string,
    cryptoAmount: number,
    pin: string
  ): Promise<ApiResponse<TradeResponse>> => {
    return apiCall<TradeResponse>('/trade/buy', {
      method: 'POST',
      body: JSON.stringify({ cryptoSymbol, cryptoAmount, pin }),
    });
  },

  sell: async (
    cryptoSymbol: string,
    cryptoAmount: number,
    pin: string
  ): Promise<ApiResponse<TradeResponse>> => {
    return apiCall<TradeResponse>('/trade/sell', {
      method: 'POST',
      body: JSON.stringify({ cryptoSymbol, cryptoAmount, pin }),
    });
  },

  transfer: async (
    cryptoSymbol: string,
    cryptoAmount: number,
    recipientEmail: string,
    pin: string
  ): Promise<ApiResponse<TradeResponse>> => {
    return apiCall<TradeResponse>('/trade/transfer', {
      method: 'POST',
      body: JSON.stringify({ cryptoSymbol, cryptoAmount, recipientEmail, pin }),
    });
  },
};

// Wallet API
export const walletApi = {
  getWallet: async (): Promise<ApiResponse<WalletResponse>> => {
    return apiCall<WalletResponse>('/wallet');
  },

  deposit: async (amount: number): Promise<ApiResponse<Transaction>> => {
    return apiCall<Transaction>('/wallet/deposit', {
      method: 'POST',
      body: JSON.stringify({ amount }),
    });
  },

  withdraw: async (
    amount: number,
    bankAccount: string,
    pin: string
  ): Promise<ApiResponse<Transaction>> => {
    return apiCall<Transaction>('/wallet/withdraw', {
      method: 'POST',
      body: JSON.stringify({ amount, bankAccount, pin }),
    });
  },

  getTransactions: async (type?: string): Promise<ApiResponse<Transaction[]>> => {
    const queryParam = type && type !== 'ALL' ? `?type=${type}` : '';
    return apiCall<Transaction[]>(`/wallet/transactions${queryParam}`);
  },

  getRecentTransactions: async (): Promise<ApiResponse<Transaction[]>> => {
    return apiCall<Transaction[]>('/wallet/transactions/recent');
  },
};

// Admin API
export const adminApi = {
  getDashboard: async (): Promise<ApiResponse<AdminDashboard>> => {
    return apiCall<AdminDashboard>('/admin/dashboard');
  },

  getUsers: async (): Promise<ApiResponse<UserSummary[]>> => {
    return apiCall<UserSummary[]>('/admin/users');
  },

  getPendingWithdrawals: async (): Promise<ApiResponse<WithdrawRequestSummary[]>> => {
    return apiCall<WithdrawRequestSummary[]>('/admin/withdrawals/pending');
  },

  processWithdrawal: async (id: number, approve: boolean): Promise<ApiResponse<string>> => {
    return apiCall<string>(`/admin/withdrawals/${id}/process`, {
      method: 'POST',
      body: JSON.stringify({ approve }),
    });
  },
};
