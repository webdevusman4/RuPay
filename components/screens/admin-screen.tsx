"use client";

import { useState } from "react";
import { allUsers, pendingWithdrawals } from "@/lib/mock-data";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Check, X, Users, AlertCircle, Shield, TrendingUp } from "lucide-react";
import { Withdrawal } from "@/lib/types";

interface AdminScreenProps {
  onBack: () => void;
}

export function AdminScreen({ onBack }: AdminScreenProps) {
  const [withdrawals, setWithdrawals] = useState<Withdrawal[]>(pendingWithdrawals);

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
    });
  };

  const handleApprove = (id: string) => {
    setWithdrawals((prev) =>
      prev.map((w) => (w.id === id ? { ...w, status: "approved" as const } : w))
    );
  };

  const handleReject = (id: string) => {
    setWithdrawals((prev) =>
      prev.map((w) => (w.id === id ? { ...w, status: "rejected" as const } : w))
    );
  };

  const totalBalance = allUsers.reduce((sum, user) => sum + user.pkrBalance, 0);
  const pendingCount = withdrawals.filter((w) => w.status === "pending").length;

  return (
    <div className="min-h-screen bg-background">
      <header className="glass border-b border-border/50 px-6 py-4 sticky top-0 z-10">
        <div className="max-w-4xl mx-auto flex items-center gap-4">
          <button
            onClick={onBack}
            className="p-2.5 rounded-xl bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-bold text-foreground">Admin Panel</h1>
              <span className="px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-medium">
                <Shield className="w-3 h-3 inline mr-1" />
                Admin
              </span>
            </div>
            <p className="text-sm text-muted-foreground">Manage users and withdrawals</p>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-6 py-6 space-y-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-3 gap-4">
          <div className="bg-card rounded-2xl border border-border/50 p-4">
            <div className="flex items-center gap-2 mb-2">
              <Users className="w-4 h-4 text-primary" />
              <span className="text-xs text-muted-foreground">Total Users</span>
            </div>
            <p className="text-2xl font-bold text-foreground">{allUsers.length}</p>
          </div>
          <div className="bg-card rounded-2xl border border-border/50 p-4">
            <div className="flex items-center gap-2 mb-2">
              <TrendingUp className="w-4 h-4 text-success" />
              <span className="text-xs text-muted-foreground">Total Balance</span>
            </div>
            <p className="text-2xl font-bold text-foreground">{formatPKR(totalBalance)}</p>
          </div>
          <div className="bg-card rounded-2xl border border-border/50 p-4">
            <div className="flex items-center gap-2 mb-2">
              <AlertCircle className="w-4 h-4 text-primary" />
              <span className="text-xs text-muted-foreground">Pending</span>
            </div>
            <p className="text-2xl font-bold text-foreground">{pendingCount}</p>
          </div>
        </div>

        {/* Users Table */}
        <section>
          <div className="flex items-center gap-2 mb-4">
            <Users className="w-5 h-5 text-primary" />
            <h2 className="text-lg font-bold text-foreground">All Users</h2>
          </div>
          <div className="bg-card rounded-2xl border border-border/50 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border/50 bg-secondary/30">
                    <th className="text-left p-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Name</th>
                    <th className="text-left p-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Email</th>
                    <th className="text-right p-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Balance</th>
                    <th className="text-right p-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">Joined</th>
                  </tr>
                </thead>
                <tbody>
                  {allUsers.map((user, index) => (
                    <tr 
                      key={user.id} 
                      className={`hover:bg-secondary/30 transition-colors ${
                        index !== allUsers.length - 1 ? "border-b border-border/50" : ""
                      }`}
                    >
                      <td className="p-4">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center text-primary font-bold text-sm">
                            {user.name.charAt(0)}
                          </div>
                          <span className="font-medium text-foreground">{user.name}</span>
                        </div>
                      </td>
                      <td className="p-4 text-muted-foreground">{user.email}</td>
                      <td className="p-4 text-right font-bold text-foreground">{formatPKR(user.pkrBalance)}</td>
                      <td className="p-4 text-right text-muted-foreground">{formatDate(user.joinDate)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </section>

        {/* Pending Withdrawals */}
        <section>
          <div className="flex items-center gap-2 mb-4">
            <AlertCircle className="w-5 h-5 text-primary" />
            <h2 className="text-lg font-bold text-foreground">Pending Withdrawals</h2>
            {pendingCount > 0 && (
              <span className="px-2 py-0.5 rounded-full bg-destructive/10 text-destructive text-xs font-bold">
                {pendingCount}
              </span>
            )}
          </div>
          <div className="bg-card rounded-2xl border border-border/50 overflow-hidden">
            {pendingCount === 0 ? (
              <div className="p-12 text-center">
                <div className="w-16 h-16 rounded-2xl bg-success/10 flex items-center justify-center mx-auto mb-4">
                  <Check className="w-8 h-8 text-success" />
                </div>
                <p className="text-muted-foreground font-medium">All caught up!</p>
                <p className="text-sm text-muted-foreground mt-1">No pending withdrawals to review</p>
              </div>
            ) : (
              <div>
                {withdrawals.map((withdrawal, index) => (
                  <div 
                    key={withdrawal.id} 
                    className={`flex items-center justify-between p-4 ${
                      index !== withdrawals.length - 1 ? "border-b border-border/50" : ""
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary font-bold">
                        {withdrawal.userName.charAt(0)}
                      </div>
                      <div>
                        <p className="font-bold text-foreground">{withdrawal.userName}</p>
                        <p className="text-xs text-muted-foreground">{formatDate(withdrawal.date)}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <p className="text-lg font-bold text-foreground">{formatPKR(withdrawal.amount)}</p>
                      {withdrawal.status === "pending" ? (
                        <div className="flex gap-2">
                          <Button
                            onClick={() => handleApprove(withdrawal.id)}
                            size="sm"
                            className="h-9 bg-success text-success-foreground hover:bg-success/90 rounded-lg font-medium"
                          >
                            <Check className="w-4 h-4 mr-1" />
                            Approve
                          </Button>
                          <Button
                            onClick={() => handleReject(withdrawal.id)}
                            size="sm"
                            variant="outline"
                            className="h-9 border-destructive/50 text-destructive hover:bg-destructive hover:text-destructive-foreground rounded-lg font-medium"
                          >
                            <X className="w-4 h-4 mr-1" />
                            Reject
                          </Button>
                        </div>
                      ) : (
                        <span className={`px-3 py-1.5 rounded-lg text-sm font-bold capitalize ${
                          withdrawal.status === "approved"
                            ? "bg-success/10 text-success"
                            : "bg-destructive/10 text-destructive"
                        }`}>
                          {withdrawal.status}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>
      </main>
    </div>
  );
}
