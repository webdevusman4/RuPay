"use client";

import { useState } from "react";
import { LoginScreen } from "@/components/screens/login-screen";
import { RegisterScreen } from "@/components/screens/register-screen";
import { DashboardScreen } from "@/components/screens/dashboard-screen";
import { BuyScreen } from "@/components/screens/buy-screen";
import { SellScreen } from "@/components/screens/sell-screen";
import { TransferScreen } from "@/components/screens/transfer-screen";
import { WalletScreen } from "@/components/screens/wallet-screen";
import { HistoryScreen } from "@/components/screens/history-screen";
import { AdminScreen } from "@/components/screens/admin-screen";
import { BottomNav } from "@/components/bottom-nav";

type Screen = "login" | "register" | "dashboard" | "buy" | "sell" | "transfer" | "wallet" | "history" | "admin";

export default function Home() {
  const [currentScreen, setCurrentScreen] = useState<Screen>("login");
  const [activeTab, setActiveTab] = useState("dashboard");

  const handleLogin = () => {
    setCurrentScreen("dashboard");
    setActiveTab("dashboard");
  };

  const handleRegister = () => {
    setCurrentScreen("dashboard");
    setActiveTab("dashboard");
  };

  const handleLogout = () => {
    setCurrentScreen("login");
  };

  const handleTabChange = (tab: string) => {
    setActiveTab(tab);
    setCurrentScreen(tab as Screen);
  };

  // Auth screens (no bottom nav)
  if (currentScreen === "login") {
    return (
      <LoginScreen
        onLogin={handleLogin}
        onRegister={() => setCurrentScreen("register")}
      />
    );
  }

  if (currentScreen === "register") {
    return (
      <RegisterScreen
        onRegister={handleRegister}
        onLogin={() => setCurrentScreen("login")}
      />
    );
  }

  // History screen (no bottom nav)
  if (currentScreen === "history") {
    return (
      <HistoryScreen
        onBack={() => {
          setCurrentScreen("dashboard");
          setActiveTab("dashboard");
        }}
      />
    );
  }

  // Admin screen (no bottom nav)
  if (currentScreen === "admin") {
    return (
      <AdminScreen
        onBack={() => {
          setCurrentScreen("dashboard");
          setActiveTab("dashboard");
        }}
      />
    );
  }

  // Main app screens with bottom nav
  return (
    <>
      {currentScreen === "dashboard" && (
        <DashboardScreen
          onLogout={handleLogout}
          onViewHistory={() => setCurrentScreen("history")}
          onAdmin={() => setCurrentScreen("admin")}
        />
      )}
      {currentScreen === "buy" && <BuyScreen />}
      {currentScreen === "sell" && <SellScreen />}
      {currentScreen === "transfer" && <TransferScreen />}
      {currentScreen === "wallet" && <WalletScreen />}
      
      <BottomNav activeTab={activeTab} onTabChange={handleTabChange} />
    </>
  );
}
