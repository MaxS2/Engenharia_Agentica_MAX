import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

export function PokemonCardSkeleton() {
  return (
    <Card className="overflow-hidden">
      <Skeleton className="aspect-square rounded-none" />
      <CardContent className="space-y-3 pt-4">
        <Skeleton className="h-5 w-32" />
        <div className="flex gap-1.5">
          <Skeleton className="h-5 w-14 rounded-full" />
          <Skeleton className="h-5 w-14 rounded-full" />
        </div>
      </CardContent>
      <CardFooter>
        <Skeleton className="h-9 w-full" />
      </CardFooter>
    </Card>
  );
}
