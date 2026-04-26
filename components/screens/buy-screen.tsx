"use client";

import { useState } from "react";
import { cryptoAssets } from "@/lib/mock-data";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { PinPopup } from "@/components/pin-popup";
import { ChevronDown, Info, ArrowDown, Sparkles } from "lucide-react";

export function BuyScreen() {
  const [selectedCoin, setSelectedCoin] = useState(cryptoAssets[0]);
  const [pkrAmount, setPkrAmount] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);
  const [showPinPopup, setShowPinPopup] = useState(false);

  const coinAmount = pkrAmount ? parseFloat(pkrAmount) / selectedCoin.priceInPKR : 0;

  const formatPKR = (value: number) => {
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const handleBuy = () => {
    if (parseFloat(pkrAmount) > 0) {
      setShowPinPopup(true);
    }
  };

  const handlePinConfirm = (pin: string) => {
    console.log("Purchase confirmed with PIN:", pin);
    setShowPinPopup(false);
    setPkrAmount("");
  };

  const quickAmounts = [1000, 5000, 10000, 50000];

  return (
    <div className="min-h-screen bg-background pb-24">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-lg mx-auto">
          <h1 className="text-xl font-bold text-foreground">Buy Crypto</h1>
          <p className="text-sm text-muted-foreground">Purchase cryptocurrency with PKR</p>
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
                      <span className="text-sm text-muted-foreground">{formatPKR(coin.priceInPKR)}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Current Price */}
            <div className="flex items-center gap-2 mt-4 p-3 bg-primary/5 border border-primary/10 rounded-xl">
              <Info className="w-4 h-4 text-primary" />
              <span className="text-sm text-muted-foreground">Current Price:</span>
              <span className="text-sm font-bold text-foreground ml-auto">{formatPKR(selectedCoin.priceInPKR)}</span>
            </div>
          </div>

          {/* Amount Input */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">You Pay (PKR)</label>
            <Input
              type="number"
              placeholder="0"
              value={pkrAmount}
              onChange={(e) => setPkrAmount(e.target.value)}
              className="h-16 text-3xl font-bold bg-secondary/50 border-border/50 rounded-xl text-foreground placeholder:text-muted-foreground text-center focus:border-primary/50 transition-all"
            />
            
            {/* Quick amounts */}
            <div className="flex gap-2 mt-4">
              {quickAmounts.map((amount) => (
                <button
                  key={amount}
                  onClick={() => setPkrAmount(amount.toString())}
                  className="flex-1 py-2 px-3 text-sm font-medium bg-secondary/50 hover:bg-secondary text-muted-foreground hover:text-foreground rounded-lg transition-colors"
                >
                  {amount >= 1000 ? `${amount / 1000}K` : amount}
                </button>
              ))}
            </div>
          </div>

          {/* Conversion */}
          <div className="flex justify-center">
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
              <ArrowDown className="w-5 h-5 text-primary" />
            </div>
          </div>

          {/* You Receive */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">You Receive</label>
            <div className="flex items-center justify-between h-16 px-4 bg-secondary/50 rounded-xl">
              <span className="text-3xl font-bold text-foreground">
                {coinAmount.toFixed(8)}
              </span>
              <span className="text-lg font-bold text-primary">{selectedCoin.symbol}</span>
            </div>
          </div>

          {/* Buy Button */}
          <Button
            onClick={handleBuy}
            disabled={!pkrAmount || parseFloat(pkrAmount) <= 0}
            className="w-full h-14 bg-primary text-primary-foreground hover:bg-primary/90 font-bold rounded-xl text-lg group transition-all glow-primary disabled:opacity-50 disabled:glow-none"
          >
            <Sparkles className="w-5 h-5 mr-2" />
            Buy {selectedCoin.symbol}
          </Button>
        </div>
      </main>

      <PinPopup
        isOpen={showPinPopup}
        onClose={() => setShowPinPopup(false)}
        onConfirm={handlePinConfirm}
        title="Confirm Purchase"
      />
    </div>
  );
}
