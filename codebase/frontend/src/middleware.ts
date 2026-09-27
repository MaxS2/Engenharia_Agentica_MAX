import { NextResponse, type NextRequest } from 'next/server';
import { jwtVerify } from 'jose';

const AUTH_COOKIE = process.env.AUTH_COOKIE_NAME ?? 'pokedeck-session';
const PUBLIC_PATHS = ['/login'];

interface JwtClaims {
  admin?: boolean;
  username?: string;
}

async function verifyJwt(token: string): Promise<JwtClaims | null> {
  const secret = process.env.JWT_SECRET;
  if (!secret) return null;
  try {
    const { payload } = await jwtVerify(token, new TextEncoder().encode(secret));
    return {
      admin: payload.admin === true,
      username: typeof payload.username === 'string' ? payload.username : undefined,
    };
  } catch {
    return null;
  }
}

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const token = request.cookies.get(AUTH_COOKIE)?.value ?? null;
  const claims = token ? await verifyJwt(token) : null;
  const isPublic = PUBLIC_PATHS.some((path) => pathname === path || pathname.startsWith(`${path}/`));

  if (!claims && !isPublic) {
    const url = request.nextUrl.clone();
    url.pathname = '/login';
    url.searchParams.set('next', pathname);
    return NextResponse.redirect(url);
  }

  if (claims && pathname === '/login') {
    const url = request.nextUrl.clone();
    url.pathname = '/';
    url.searchParams.delete('next');
    return NextResponse.redirect(url);
  }

  if (pathname.startsWith('/admin') && !claims?.admin) {
    const url = request.nextUrl.clone();
    url.pathname = '/';
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/((?!_next/static|_next/image|favicon.ico|logo.png|iara.png).*)'],
};
