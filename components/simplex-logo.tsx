"use client";

export function SimplexLogo({ className = "", size = "default" }: { className?: string; size?: "default" | "large" }) {
  const isLarge = size === "large";
  
  return (
    <div className={`flex items-center gap-3 ${className}`}>
      <div className={`relative ${isLarge ? "w-14 h-14" : "w-11 h-11"}`}>
        {/* Outer glow */}
        <div className="absolute inset-0 bg-primary/20 rounded-xl blur-lg" />
        {/* Main shape */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary via-primary to-yellow-400 rounded-xl" />
        {/* Inner cutout */}
        <div className="absolute inset-[3px] bg-background rounded-lg" />
        {/* Letter */}
        <div className="absolute inset-0 flex items-center justify-center">
          <span className={`text-gradient font-black ${isLarge ? "text-2xl" : "text-xl"}`}>R</span>
        </div>
      </div>
      <div className="flex flex-col">
        <span className={`font-bold tracking-tight ${isLarge ? "text-3xl" : "text-2xl"}`}>
          <span className="text-foreground">Ru</span>
          <span className="text-gradient">Pay</span>
        </span>
        {isLarge && (
          <span className="text-xs text-muted-foreground tracking-widest uppercase">Crypto Exchange</span>
        )}
      </div>
    </div>
  );
}
