"use client";

import { CryptoCard } from "@/components/crypto-card";
import { cryptoAssets, recentTransactions, currentUser } from "@/lib/mock-data";
import { ArrowDownRight, ArrowUpRight, History, LogOut, Settings, TrendingUp, Wallet } from "lucide-react";

interface DashboardScreenProps {
  onLogout: () => void;
  onViewHistory: () => void;
  onAdmin: () => void;
}

export function DashboardScreen({ onLogout, onViewHistory, onAdmin }: DashboardScreenProps) {
  const formatPKR = (value: number) => {
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-PK", {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getTransactionIcon = (type: string) => {
    switch (type) {
      case "buy":
      case "deposit":
        return <ArrowDownRight className="w-4 h-4 text-success" />;
      case "sell":
      case "withdraw":
      case "transfer":
        return <ArrowUpRight className="w-4 h-4 text-destructive" />;
      default:
        return null;
    }
  };

  const totalCryptoValue = cryptoAssets.reduce((sum, asset) => {
    return sum + (asset.holdings || 0) * asset.priceInPKR;
  }, 0);

  return (
    <div className="min-h-screen bg-background pb-24">
      {/* Header */}
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground">Welcome back,</p>
            <h1 className="text-lg font-bold text-foreground">{currentUser.name}</h1>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={onAdmin}
              className="p-2.5 rounded-xl bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all"
            >
              <Settings className="w-5 h-5" />
            </button>
            <button
              onClick={onLogout}
              className="p-2.5 rounded-xl bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all"
            >
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-6 py-6 space-y-6">
        {/* Portfolio Overview */}
        <section className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* PKR Balance Card */}
          <div className="relative overflow-hidden bg-gradient-to-br from-primary/20 via-primary/10 to-transparent rounded-2xl border border-primary/20 p-6 glow-primary">
            <div className="absolute top-0 right-0 w-32 h-32 bg-primary/10 rounded-full blur-2xl -translate-y-1/2 translate-x-1/2" />
            <div className="relative">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 rounded-xl bg-primary/20 flex items-center justify-center">
                  <Wallet className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">PKR Balance</p>
                  <p className="text-3xl font-bold text-foreground">{formatPKR(currentUser.pkrBalance)}</p>
                </div>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <span className="flex items-center gap-1 text-success">
                  <TrendingUp className="w-4 h-4" />
                  +12.5%
                </span>
                <span className="text-muted-foreground">this month</span>
              </div>
            </div>
          </div>

          {/* Crypto Value Card */}
          <div className="relative overflow-hidden bg-card rounded-2xl border border-border/50 p-6 card-hover">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 rounded-xl bg-success/10 flex items-center justify-center">
                <TrendingUp className="w-6 h-6 text-success" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Total Crypto Value</p>
                <p className="text-3xl font-bold text-foreground">{formatPKR(totalCryptoValue)}</p>
              </div>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <span className="flex items-center gap-1 text-success">
                <TrendingUp className="w-4 h-4" />
                +8.3%
              </span>
              <span className="text-muted-foreground">24h change</span>
            </div>
          </div>
        </section>

        {/* Crypto Cards */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-foreground">Live Prices</h2>
            <span className="text-xs text-muted-foreground px-3 py-1.5 bg-secondary/50 rounded-full">
              Updated just now
            </span>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
            {cryptoAssets.map((asset) => (
              <CryptoCard key={asset.id} asset={asset} />
            ))}
          </div>
        </section>

        {/* Recent Transactions */}
        <section>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-foreground">Recent Activity</h2>
            <button
              onClick={onViewHistory}
              className="flex items-center gap-1.5 text-sm text-primary hover:text-primary/80 font-medium transition-colors"
            >
              <History className="w-4 h-4" />
              View All
            </button>
          </div>
          <div className="bg-card rounded-2xl border border-border/50 overflow-hidden">
            {recentTransactions.slice(0, 5).map((tx, index) => (
              <div 
                key={tx.id} 
                className={`flex items-center justify-between p-4 hover:bg-secondary/30 transition-colors ${
                  index !== recentTransactions.slice(0, 5).length - 1 ? "border-b border-border/50" : ""
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${
                    tx.type === "buy" || tx.type === "deposit" 
                      ? "bg-success/10" 
                      : "bg-destructive/10"
                  }`}>
                    {getTransactionIcon(tx.type)}
                  </div>
                  <div>
                    <p className="font-medium text-foreground capitalize">
                      {tx.type} {tx.coin || "PKR"}
                    </p>
                    <p className="text-xs text-muted-foreground">{formatDate(tx.date)}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className={`font-semibold ${tx.type === "buy" || tx.type === "deposit" ? "text-success" : "text-destructive"}`}>
                    {tx.type === "buy" || tx.type === "deposit" ? "+" : "-"}{formatPKR(tx.pkrValue)}
                  </p>
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                    tx.status === "completed" ? "bg-success/10 text-success" :
                    tx.status === "pending" ? "bg-primary/10 text-primary" :
                    "bg-destructive/10 text-destructive"
                  }`}>
                    {tx.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}
