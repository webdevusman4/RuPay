"use client";

import { useState } from "react";
import { X, ShieldCheck, Delete } from "lucide-react";
import { Button } from "@/components/ui/button";

interface PinPopupProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (pin: string) => void;
  title?: string;
}

export function PinPopup({ isOpen, onClose, onConfirm, title = "Enter PIN to Confirm" }: PinPopupProps) {
  const [pin, setPin] = useState<string[]>(["", "", "", ""]);
  
  if (!isOpen) return null;

  const handleNumberClick = (num: string) => {
    const newPin = [...pin];
    const emptyIndex = newPin.findIndex((p) => p === "");
    if (emptyIndex !== -1) {
      newPin[emptyIndex] = num;
      setPin(newPin);
    }
  };

  const handleBackspace = () => {
    const newPin = [...pin];
    const lastFilledIndex = newPin.map((p, i) => (p !== "" ? i : -1)).filter((i) => i !== -1).pop();
    if (lastFilledIndex !== undefined) {
      newPin[lastFilledIndex] = "";
      setPin(newPin);
    }
  };

  const handleConfirm = () => {
    if (pin.every((p) => p !== "")) {
      onConfirm(pin.join(""));
      setPin(["", "", "", ""]);
    }
  };

  const handleClose = () => {
    setPin(["", "", "", ""]);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
      <div className="absolute inset-0 bg-black/70 backdrop-blur-md" onClick={handleClose} />
      <div className="relative glass rounded-t-3xl sm:rounded-2xl p-8 w-full sm:max-w-sm shadow-2xl border border-border/50 animate-in slide-in-from-bottom-4 duration-300">
        <button
          onClick={handleClose}
          className="absolute top-4 right-4 p-2 rounded-full bg-secondary/50 text-muted-foreground hover:text-foreground hover:bg-secondary transition-all"
        >
          <X className="w-5 h-5" />
        </button>
        
        {/* Header */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center mb-4">
            <ShieldCheck className="w-7 h-7 text-primary" />
          </div>
          <h2 className="text-xl font-bold text-center text-foreground">{title}</h2>
          <p className="text-sm text-muted-foreground mt-1">Enter your 4-digit security PIN</p>
        </div>
        
        {/* PIN Dots */}
        <div className="flex justify-center gap-4 mb-8">
          {pin.map((digit, index) => (
            <div
              key={index}
              className={`w-14 h-14 rounded-xl border-2 flex items-center justify-center transition-all ${
                digit 
                  ? "border-primary bg-primary/10" 
                  : "border-border/50 bg-secondary/30"
              }`}
            >
              {digit ? (
                <div className="w-3 h-3 rounded-full bg-primary animate-in zoom-in duration-150" />
              ) : (
                <div className="w-3 h-3 rounded-full bg-muted-foreground/20" />
              )}
            </div>
          ))}
        </div>
        
        {/* Number Pad */}
        <div className="grid grid-cols-3 gap-3 mb-6">
          {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
            <button
              key={num}
              onClick={() => handleNumberClick(num.toString())}
              className="h-14 rounded-xl bg-secondary/50 text-foreground text-xl font-bold hover:bg-secondary active:scale-95 transition-all"
            >
              {num}
            </button>
          ))}
          <button
            onClick={handleBackspace}
            className="h-14 rounded-xl bg-secondary/50 text-muted-foreground hover:bg-destructive/10 hover:text-destructive active:scale-95 transition-all flex items-center justify-center"
          >
            <Delete className="w-5 h-5" />
          </button>
          <button
            onClick={() => handleNumberClick("0")}
            className="h-14 rounded-xl bg-secondary/50 text-foreground text-xl font-bold hover:bg-secondary active:scale-95 transition-all"
          >
            0
          </button>
          <div />
        </div>
        
        {/* Confirm Button */}
        <Button
          onClick={handleConfirm}
          disabled={!pin.every((p) => p !== "")}
          className="w-full h-13 bg-primary text-primary-foreground hover:bg-primary/90 font-bold rounded-xl text-base transition-all disabled:opacity-40"
        >
          Confirm Transaction
        </Button>
      </div>
    </div>
  );
}
