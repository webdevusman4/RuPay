"use client";

import { useState } from "react";
import { cryptoAssets } from "@/lib/mock-data";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { PinPopup } from "@/components/pin-popup";
import { ChevronDown, Mail, Send, Wallet, User, ArrowRight } from "lucide-react";

export function TransferScreen() {
  const [selectedCoin, setSelectedCoin] = useState(cryptoAssets[0]);
  const [recipientEmail, setRecipientEmail] = useState("");
  const [amount, setAmount] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);
  const [showPinPopup, setShowPinPopup] = useState(false);

  const formatPKR = (value: number) => {
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  const handleTransfer = () => {
    if (recipientEmail && parseFloat(amount) > 0) {
      setShowPinPopup(true);
    }
  };

  const handlePinConfirm = (pin: string) => {
    console.log("Transfer confirmed with PIN:", pin);
    setShowPinPopup(false);
    setRecipientEmail("");
    setAmount("");
  };

  const transferValue = amount ? parseFloat(amount) * selectedCoin.priceInPKR : 0;

  return (
    <div className="min-h-screen bg-background pb-24">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-lg mx-auto">
          <h1 className="text-xl font-bold text-foreground">Send Crypto</h1>
          <p className="text-sm text-muted-foreground">Transfer to another RuPay user</p>
        </div>
      </header>

      <main className="max-w-lg mx-auto px-6 py-6">
        <div className="space-y-4">
          {/* Recipient */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">Recipient</label>
            <div className="relative group">
              <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground group-focus-within:text-primary transition-colors" />
              <Input
                type="email"
                placeholder="Enter recipient's email"
                value={recipientEmail}
                onChange={(e) => setRecipientEmail(e.target.value)}
                className="h-14 pl-12 bg-secondary/50 border-border/50 rounded-xl text-foreground placeholder:text-muted-foreground focus:border-primary/50 focus:bg-secondary transition-all"
              />
            </div>
            <p className="text-xs text-muted-foreground mt-2 flex items-center gap-1">
              <Mail className="w-3 h-3" />
              Transfer is instant between RuPay users
            </p>
          </div>

          {/* Coin Selector */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">Select Asset</label>
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

            {/* Holdings Info */}
            <div className="flex items-center gap-2 mt-4 p-3 bg-secondary/30 rounded-xl">
              <Wallet className="w-4 h-4 text-primary" />
              <span className="text-sm text-muted-foreground">Available:</span>
              <span className="text-sm font-bold text-foreground">
                {selectedCoin.holdings?.toFixed(6)} {selectedCoin.symbol}
              </span>
            </div>
          </div>

          {/* Amount Input */}
          <div className="bg-card rounded-2xl border border-border/50 p-5">
            <label className="text-sm font-medium text-muted-foreground mb-3 block">Amount to Send</label>
            <div className="relative">
              <Input
                type="number"
                placeholder="0"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="h-16 text-3xl font-bold bg-secondary/50 border-border/50 rounded-xl text-foreground placeholder:text-muted-foreground text-center pr-20 focus:border-primary/50 transition-all"
              />
              <span className="absolute right-4 top-1/2 -translate-y-1/2 text-lg font-bold text-primary">
                {selectedCoin.symbol}
              </span>
            </div>
            {amount && parseFloat(amount) > 0 && (
              <p className="text-center text-sm text-muted-foreground mt-3">
                Value: <span className="font-medium text-foreground">{formatPKR(transferValue)}</span>
              </p>
            )}
          </div>

          {/* Transfer Summary */}
          {recipientEmail && amount && parseFloat(amount) > 0 && (
            <div className="bg-primary/5 border border-primary/10 rounded-2xl p-4">
              <p className="text-xs text-muted-foreground mb-2">Transfer Summary</p>
              <div className="flex items-center gap-2">
                <span className="text-sm font-bold text-foreground truncate max-w-[120px]">You</span>
                <ArrowRight className="w-4 h-4 text-primary flex-shrink-0" />
                <span className="text-sm font-bold text-foreground truncate flex-1">{recipientEmail}</span>
              </div>
              <p className="text-lg font-bold text-primary mt-2">
                {amount} {selectedCoin.symbol}
              </p>
            </div>
          )}

          {/* Transfer Button */}
          <Button
            onClick={handleTransfer}
            disabled={!recipientEmail || !amount || parseFloat(amount) <= 0}
            className="w-full h-14 bg-primary text-primary-foreground hover:bg-primary/90 font-bold rounded-xl text-lg group transition-all glow-primary disabled:opacity-50 disabled:glow-none"
          >
            <Send className="w-5 h-5 mr-2 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
            Send Transfer
          </Button>
        </div>
      </main>

      <PinPopup
        isOpen={showPinPopup}
        onClose={() => setShowPinPopup(false)}
        onConfirm={handlePinConfirm}
        title="Confirm Transfer"
      />
    </div>
  );
}
