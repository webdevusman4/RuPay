"use client";

import { useState } from "react";
import { recentTransactions } from "@/lib/mock-data";
import { ArrowLeft, ArrowDownRight, ArrowUpRight, History, Search } from "lucide-react";

interface HistoryScreenProps {
  onBack: () => void;
}

const filterOptions = ["All", "Buy", "Sell", "Deposit", "Withdraw", "Transfer"];

export function HistoryScreen({ onBack }: HistoryScreenProps) {
  const [activeFilter, setActiveFilter] = useState("All");

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
      year: "numeric",
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

  const filteredTransactions = activeFilter === "All"
    ? recentTransactions
    : recentTransactions.filter((tx) => tx.type.toLowerCase() === activeFilter.toLowerCase());

  return (
    <div className="min-h-screen bg-background">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-2xl mx-auto">
          <div className="flex items-center gap-4 mb-4">
            <button
              onClick={onBack}
              className="p-2.5 rounded-xl bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div className="flex-1">
              <h1 className="text-xl font-bold text-foreground">Transaction History</h1>
              <p className="text-sm text-muted-foreground">All your transactions</p>
            </div>
            <button className="p-2.5 rounded-xl bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all">
              <Search className="w-5 h-5" />
            </button>
          </div>
          
          {/* Filter Bar */}
          <div className="flex gap-2 overflow-x-auto pb-2 -mx-2 px-2">
            {filterOptions.map((filter) => (
              <button
                key={filter}
                onClick={() => setActiveFilter(filter)}
                className={`px-4 py-2 rounded-xl text-sm font-medium whitespace-nowrap transition-all ${
                  activeFilter === filter
                    ? "bg-primary text-primary-foreground"
                    : "bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary"
                }`}
              >
                {filter}
              </button>
            ))}
          </div>
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-6 py-6">
        <div className="bg-card rounded-2xl border border-border/50 overflow-hidden">
          {filteredTransactions.length === 0 ? (
            <div className="p-12 text-center">
              <div className="w-16 h-16 rounded-2xl bg-secondary/50 flex items-center justify-center mx-auto mb-4">
                <History className="w-8 h-8 text-muted-foreground" />
              </div>
              <p className="text-muted-foreground font-medium">No transactions found</p>
              <p className="text-sm text-muted-foreground mt-1">Try adjusting your filters</p>
            </div>
          ) : (
            filteredTransactions.map((tx, index) => (
              <div 
                key={tx.id} 
                className={`flex items-center justify-between p-4 hover:bg-secondary/30 transition-colors ${
                  index !== filteredTransactions.length - 1 ? "border-b border-border/50" : ""
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
                    <div className="flex items-center gap-2">
                      <p className="font-bold text-foreground capitalize">
                        {tx.type} {tx.coin || "PKR"}
                      </p>
                    </div>
                    <p className="text-xs text-muted-foreground">{formatDate(tx.date)}</p>
                    {tx.recipient && (
                      <p className="text-xs text-muted-foreground">To: {tx.recipient}</p>
                    )}
                  </div>
                </div>
                <div className="text-right">
                  <p className={`font-bold ${
                    tx.type === "buy" || tx.type === "deposit" ? "text-success" : "text-destructive"
                  }`}>
                    {tx.type === "buy" || tx.type === "deposit" ? "+" : "-"}{formatPKR(tx.pkrValue)}
                  </p>
                  {tx.coin && (
                    <p className="text-xs text-muted-foreground mb-1">
                      {tx.amount} {tx.coin}
                    </p>
                  )}
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                    tx.status === "completed" ? "bg-success/10 text-success" :
                    tx.status === "pending" ? "bg-primary/10 text-primary" :
                    "bg-destructive/10 text-destructive"
                  }`}>
                    {tx.status}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      </main>
    </div>
  );
}
