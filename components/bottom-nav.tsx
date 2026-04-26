"use client";

import { LayoutDashboard, ShoppingCart, TrendingDown, ArrowLeftRight, Wallet } from "lucide-react";

interface BottomNavProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
}

const navItems = [
  { id: "dashboard", label: "Home", icon: LayoutDashboard },
  { id: "buy", label: "Buy", icon: ShoppingCart },
  { id: "sell", label: "Sell", icon: TrendingDown },
  { id: "transfer", label: "Send", icon: ArrowLeftRight },
  { id: "wallet", label: "Wallet", icon: Wallet },
];

export function BottomNav({ activeTab, onTabChange }: BottomNavProps) {
  return (
    <nav className="fixed bottom-0 left-0 right-0 glass border-t border-border/50 safe-area-inset-bottom">
      <div className="max-w-lg mx-auto px-2">
        <div className="flex items-center justify-around py-2">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onTabChange(item.id)}
                className={`relative flex flex-col items-center gap-1 py-2 px-4 rounded-xl transition-all ${
                  isActive
                    ? "text-primary"
                    : "text-muted-foreground hover:text-foreground"
                }`}
              >
                {isActive && (
                  <div className="absolute inset-0 bg-primary/10 rounded-xl" />
                )}
                <Icon className={`relative w-5 h-5 ${isActive ? "scale-110" : ""} transition-transform`} />
                <span className={`relative text-xs font-medium ${isActive ? "font-semibold" : ""}`}>
                  {item.label}
                </span>
              </button>
            );
          })}
        </div>
      </div>
    </nav>
  );
}
