"use client";

import { useState } from "react";
import { cryptoAssets, currentUser } from "@/lib/mock-data";
import { Button } from "@/components/ui/button";
import { PinPopup } from "@/components/pin-popup";
import { ArrowDownToLine, ArrowUpFromLine, Wallet, TrendingUp, Coins } from "lucide-react";

export function WalletScreen() {
  const [showPinPopup, setShowPinPopup] = useState(false);
  const [pinAction, setPinAction] = useState<"deposit" | "withdraw">("deposit");

  const formatPKR = (value: number) => {
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const handleAction = (action: "deposit" | "withdraw") => {
    setPinAction(action);
    setShowPinPopup(true);
  };

  const handlePinConfirm = (pin: string) => {
    console.log(`${pinAction} confirmed with PIN:`, pin);
    setShowPinPopup(false);
  };

  const totalCryptoValue = cryptoAssets.reduce((sum, asset) => {
    return sum + (asset.holdings || 0) * asset.priceInPKR;
  }, 0);

  const totalPortfolio = currentUser.pkrBalance + totalCryptoValue;

  return (
    <div className="min-h-screen bg-background pb-24">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-lg mx-auto">
          <h1 className="text-xl font-bold text-foreground">Wallet</h1>
          <p className="text-sm text-muted-foreground">Manage your portfolio</p>
        </div>
      </header>

      <main className="max-w-lg mx-auto px-6 py-6 space-y-4">
        {/* Total Portfolio */}
        <div className="relative overflow-hidden bg-gradient-to-br from-primary/20 via-primary/10 to-transparent rounded-2xl border border-primary/20 p-6 glow-primary">
          <div className="absolute top-0 right-0 w-40 h-40 bg-primary/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />
          <div className="relative">
            <div className="flex items-center gap-2 mb-2">
              <Coins className="w-5 h-5 text-primary" />
              <p className="text-sm text-muted-foreground">Total Portfolio</p>
            </div>
            <p className="text-4xl font-bold text-foreground mb-4">{formatPKR(totalPortfolio)}</p>
            <div className="flex items-center gap-2 text-sm">
              <span className="flex items-center gap-1 px-2 py-1 rounded-full bg-success/10 text-success">
                <TrendingUp className="w-3 h-3" />
                +15.8%
              </span>
              <span className="text-muted-foreground">all time</span>
            </div>
          </div>
        </div>

        {/* Balance Cards */}
        <div className="grid grid-cols-2 gap-3">
          {/* PKR Balance Card */}
          <div className="bg-card rounded-2xl border border-border/50 p-4">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
                <Wallet className="w-4 h-4 text-primary" />
              </div>
              <span className="text-xs text-muted-foreground">PKR Balance</span>
            </div>
            <p className="text-xl font-bold text-foreground">{formatPKR(currentUser.pkrBalance)}</p>
          </div>

          {/* Crypto Value Card */}
          <div className="bg-card rounded-2xl border border-border/50 p-4">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 rounded-lg bg-success/10 flex items-center justify-center">
                <TrendingUp className="w-4 h-4 text-success" />
              </div>
              <span className="text-xs text-muted-foreground">Crypto Value</span>
            </div>
            <p className="text-xl font-bold text-foreground">{formatPKR(totalCryptoValue)}</p>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-3">
          <Button
            onClick={() => handleAction("deposit")}
            className="flex-1 h-13 bg-success text-success-foreground hover:bg-success/90 font-bold rounded-xl glow-success"
          >
            <ArrowDownToLine className="w-5 h-5 mr-2" />
            Deposit
          </Button>
          <Button
            onClick={() => handleAction("withdraw")}
            variant="outline"
            className="flex-1 h-13 border-border/50 text-foreground hover:bg-secondary font-bold rounded-xl"
          >
            <ArrowUpFromLine className="w-5 h-5 mr-2" />
            Withdraw
          </Button>
        </div>

        {/* Crypto Holdings */}
        <div>
          <h2 className="text-lg font-bold text-foreground mb-4">Your Holdings</h2>
          <div className="bg-card rounded-2xl border border-border/50 overflow-hidden">
            {cryptoAssets.map((asset, index) => {
              const value = (asset.holdings || 0) * asset.priceInPKR;
              const percentage = totalCryptoValue > 0 ? (value / totalCryptoValue) * 100 : 0;
              return (
                <div 
                  key={asset.id} 
                  className={`flex items-center justify-between p-4 hover:bg-secondary/30 transition-colors ${
                    index !== cryptoAssets.length - 1 ? "border-b border-border/50" : ""
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center text-primary font-bold">
                      {asset.icon}
                    </div>
                    <div>
                      <p className="font-bold text-foreground">{asset.symbol}</p>
                      <p className="text-xs text-muted-foreground">{asset.name}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-foreground">{asset.holdings?.toFixed(6)}</p>
                    <div className="flex items-center gap-2 justify-end">
                      <p className="text-xs text-muted-foreground">{formatPKR(value)}</p>
                      <span className="text-xs px-1.5 py-0.5 rounded bg-secondary text-muted-foreground">
                        {percentage.toFixed(1)}%
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </main>

      <PinPopup
        isOpen={showPinPopup}
        onClose={() => setShowPinPopup(false)}
        onConfirm={handlePinConfirm}
        title={pinAction === "deposit" ? "Confirm Deposit" : "Confirm Withdrawal"}
      />
    </div>
  );
}
