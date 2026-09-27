import { Progress } from '@/components/ui/progress';

interface StatBarProps {
  label: string;
  value: number;
  /** Valor maximo (padrao 150, suficiente para stats basicos de Pokemon). */
  max?: number;
}

export function StatBar({ label, value, max = 150 }: StatBarProps) {
  const pct = Math.min(100, Math.round((value / max) * 100));
  return (
    <div className="space-y-1">
      <div className="flex items-center justify-between text-sm">
        <span className="font-medium text-muted-foreground">{label}</span>
        <span className="font-semibold tabular-nums">{value}</span>
      </div>
      <Progress value={pct} aria-label={`${label}: ${value}`} />
    </div>
  );
}
