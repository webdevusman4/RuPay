"use client";

import { CryptoAsset } from "@/lib/types";
import { TrendingUp, TrendingDown } from "lucide-react";

interface CryptoCardProps {
  asset: CryptoAsset;
}

export function CryptoCard({ asset }: CryptoCardProps) {
  const isPositive = asset.change24h >= 0;
  
  const formatPKR = (value: number) => {
    if (value >= 1000000) {
      return `PKR ${(value / 1000000).toFixed(2)}M`;
    }
    return new Intl.NumberFormat("en-PK", {
      style: "currency",
      currency: "PKR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="group bg-card rounded-xl p-4 border border-border/50 hover:border-primary/30 transition-all card-hover cursor-pointer">
      <div className="flex items-center gap-3 mb-3">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center text-primary font-bold text-lg group-hover:scale-110 transition-transform">
          {asset.icon}
        </div>
        <div className="flex-1 min-w-0">
          <h3 className="font-bold text-foreground">{asset.symbol}</h3>
          <p className="text-xs text-muted-foreground truncate">{asset.name}</p>
        </div>
      </div>
      <div className="flex items-end justify-between">
        <p className="text-sm font-semibold text-foreground">{formatPKR(asset.priceInPKR)}</p>
        <div className={`flex items-center gap-1 text-xs font-medium px-2 py-1 rounded-full ${
          isPositive 
            ? "bg-success/10 text-success" 
            : "bg-destructive/10 text-destructive"
        }`}>
          {isPositive ? <TrendingUp className="w-3 h-3" /> : <TrendingDown className="w-3 h-3" />}
          <span>{isPositive ? "+" : ""}{asset.change24h.toFixed(2)}%</span>
        </div>
      </div>
    </div>
  );
}
