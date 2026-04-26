"use client";

import { useState } from "react";
import { cryptoAssets } from "@/lib/mock-data";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { PinPopup } from "@/components/pin-popup";
import { ChevronDown, Info, Wallet, ArrowDown, Banknote } from "lucide-react";

export function SellScreen() {
  const [selectedCoin, setSelectedCoin] = useState(cryptoAssets[0]);
  const [coinAmount, setCoinAmount] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);
  const [showPinPopup, setShowPinPopup] = useState(false);

  const pkrAmount = coinAmount ? parseFloat(coinAmount) * selectedCoin.priceInPKR : 0;

  const formatPKR = (value: number) => {
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const handleSell = () => {
    if (parseFloat(coinAmount) > 0) {
      setShowPinPopup(true);
    }
  };

  const handlePinConfirm = (pin: string) => {
    console.log("Sale confirmed with PIN:", pin);
    setShowPinPopup(false);
    setCoinAmount("");
  };

  const quickPercentages = [25, 50, 75, 100];

  return (
    <div className="min-h-screen bg-background pb-24">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-lg mx-auto">
          <h1 className="text-xl font-bold text-foreground">Sell Crypto</h1>
          <p className="text-sm text-muted-foreground">Convert cryptocurrency to PKR</p>
        </div>
      </header>

      <main className="max-w-lg mx-auto px-6 py-6">
        <div className="space-y-4">
          {/* Coin Selector */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">Select Coin</label>
            <div className="relative">
              <button
                onClick={() => setShowDropdown(!showDropdown)}
                className="w-full h-14 px-4 bg-secondary/50 hover:bg-secondary rounded-xl flex items-center justify-between text-foreground transition-colors"
              >
                <div className="flex items-center gap-3">
                  <span className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center text-primary font-bold">
                    {selectedCoin.icon}
                  </span>
                  <div className="text-left">
                    <span className="font-bold text-foreground block">{selectedCoin.symbol}</span>
                    <span className="text-xs text-muted-foreground">{selectedCoin.name}</span>
                  </div>
                </div>
                <ChevronDown className={`w-5 h-5 text-muted-foreground transition-transform ${showDropdown ? "rotate-180" : ""}`} />
              </button>
              
              {showDropdown && (
                <div className="absolute top-full left-0 right-0 mt-2 bg-card border border-border/50 rounded-xl shadow-xl z-20 overflow-hidden">
                  {cryptoAssets.map((coin) => (
                    <button
                      key={coin.id}
                      onClick={() => {
                        setSelectedCoin(coin);
                        setShowDropdown(false);
                      }}
                      className={`w-full px-4 py-3 flex items-center gap-3 hover:bg-secondary/50 transition-colors ${
                        selectedCoin.id === coin.id ? "bg-primary/10" : ""
                      }`}
                    >
                      <span className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center text-primary font-bold text-sm">
                        {coin.icon}
                      </span>
                      <span className="font-medium text-foreground">{coin.symbol}</span>
                      <span className="text-muted-foreground text-sm flex-1 text-left">{coin.name}</span>
                      <span className="text-xs text-muted-foreground">{coin.holdings?.toFixed(4)}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Holdings & Price Info */}
            <div className="grid grid-cols-2 gap-3 mt-4">
              <div className="flex items-center gap-2 p-3 bg-secondary/30 rounded-xl">
                <Wallet className="w-4 h-4 text-primary" />
                <div className="text-xs">
                  <span className="text-muted-foreground block">Holdings</span>
                  <span className="font-bold text-foreground">{selectedCoin.holdings?.toFixed(6)}</span>
                </div>
              </div>
              <div className="flex items-center gap-2 p-3 bg-primary/5 border border-primary/10 rounded-xl">
                <Info className="w-4 h-4 text-primary" />
                <div className="text-xs">
                  <span className="text-muted-foreground block">Price</span>
                  <span className="font-bold text-foreground">{formatPKR(selectedCoin.priceInPKR)}</span>
                </div>
              </div>
            </div>
          </div>

          {/* Amount Input */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">You Sell ({selectedCoin.symbol})</label>
            <Input
              type="number"
              placeholder="0"
              value={coinAmount}
              onChange={(e) => setCoinAmount(e.target.value)}
              className="h-16 text-3xl font-bold bg-secondary/50 border-border/50 rounded-xl text-foreground placeholder:text-muted-foreground text-center focus:border-primary/50 transition-all"
            />
            
            {/* Quick percentages */}
            <div className="flex gap-2 mt-4">
              {quickPercentages.map((pct) => (
                <button
                  key={pct}
                  onClick={() => setCoinAmount(((selectedCoin.holdings || 0) * pct / 100).toString())}
                  className={`flex-1 py-2 px-3 text-sm font-medium rounded-lg transition-colors ${
                    pct === 100 
                      ? "bg-destructive/10 text-destructive hover:bg-destructive/20" 
                      : "bg-secondary/50 hover:bg-secondary text-muted-foreground hover:text-foreground"
                  }`}
                >
                  {pct}%
                </button>
              ))}
            </div>
          </div>

          {/* Conversion */}
          <div className="flex justify-center">
            <div className="w-10 h-10 rounded-full bg-destructive/10 flex items-center justify-center">
              <ArrowDown className="w-5 h-5 text-destructive" />
            </div>
          </div>

          {/* You Receive */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">You Receive (PKR)</label>
            <div className="flex items-center justify-between h-16 px-4 bg-success/5 border border-success/10 rounded-xl">
              <span className="text-3xl font-bold text-success">
                {formatPKR(pkrAmount)}
              </span>
              <Banknote className="w-6 h-6 text-success" />
            </div>
          </div>

          {/* Sell Button */}
          <Button
            onClick={handleSell}
            disabled={!coinAmount || parseFloat(coinAmount) <= 0}
            className="w-full h-14 bg-destructive text-destructive-foreground hover:bg-destructive/90 font-bold rounded-xl text-lg transition-all disabled:opacity-50"
          >
            Sell {selectedCoin.symbol}
          </Button>
        </div>
      </main>

      <PinPopup
        isOpen={showPinPopup}
        onClose={() => setShowPinPopup(false)}
        onConfirm={handlePinConfirm}
        title="Confirm Sale"
      />
    </div>
  );
}
