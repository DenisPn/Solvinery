import { useState } from 'react';
import { Link } from 'react-router-dom';

export default function AuthPage() {
  // ניהול המצב בין התחברות להרשמה
  const [authMode, setAuthMode] = useState<'login' | 'register'>('register');

  return (
    <div className="flex h-screen w-full overflow-hidden bg-[#f6f7f8] font-sans text-[#111618]">
      {/* הערה חשובה:
         כדי שהאייקונים יעבדו, עליך להוסיף את השורה הבאה לקובץ index.html בתיקיית public:
         <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" rel="stylesheet" />
      */}

      {/* צד שמאל - תמונה ושיווק (מוסתר במובייל) */}
      <div 
        className="hidden lg:flex w-6/12 flex-col justify-between bg-cover bg-center p-16 relative" 
        style={{ 
          backgroundImage: `linear-gradient(180deg, rgba(19, 164, 236, 0.2) 0%, rgba(16, 28, 34, 0.95) 100%), url('https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?q=80&w=2071&auto=format&fit=crop')` 
        }}
      >
        <div className="absolute inset-0 bg-black/10 backdrop-blur-[1px]"></div>
        
        {/* לוגו */}
        <div className="relative z-10 flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[#13a4ec] text-white shadow-lg backdrop-blur-sm bg-opacity-90">
            <span className="material-symbols-outlined text-3xl">calendar_month</span>
          </div>
          <h2 className="text-3xl font-black tracking-tight text-white drop-shadow-md">SchedulerPro</h2>
        </div>

        {/* טקסט שיווקי */}
        <div className="relative z-10 flex flex-col gap-8 max-w-xl">
          <h3 className="text-5xl font-extrabold leading-tight text-white drop-shadow-lg tracking-tight">
            Master your schedule with precision.
          </h3>
          <p className="text-xl font-medium leading-relaxed text-gray-100/90 max-w-lg">
            Join thousands of professionals who trust SchedulerPro to optimize workflows, allocate resources, and reclaim their time.
          </p>
          
          {/* הוכחה חברתית (Social Proof) */}
          <div className="flex gap-6 items-center text-white">
            <div className="flex -space-x-4">
              {[1, 2, 3].map((i) => (
                <div key={i} className="h-12 w-12 rounded-full border-2 border-white/20 bg-gray-300 bg-cover shadow-lg" style={{ backgroundImage: `url('https://i.pravatar.cc/150?img=${i + 10}')` }}></div>
              ))}
              <div className="flex h-12 w-12 items-center justify-center rounded-full border-2 border-white/20 bg-[#13a4ec]/90 text-sm font-bold text-white shadow-lg backdrop-blur-sm">+2k</div>
            </div>
            <div className="flex flex-col">
              <span className="text-lg font-bold">Join our community</span>
              <span className="text-sm text-white/70">Trusted by over 2000 teams</span>
            </div>
          </div>

          <div className="mt-8 flex gap-3">
            <div className="h-2 w-16 rounded-full bg-[#13a4ec] shadow-[0_0_15px_rgba(19,164,236,0.6)]"></div>
            <div className="h-2 w-3 rounded-full bg-white/30 backdrop-blur-sm"></div>
            <div className="h-2 w-3 rounded-full bg-white/30 backdrop-blur-sm"></div>
          </div>
        </div>

        {/* תפריט תחתון */}
        <div className="relative z-10 mt-auto pt-8 flex gap-6 text-sm text-white/60">
          <a className="hover:text-white transition-colors" href="#">Privacy Policy</a>
          <a className="hover:text-white transition-colors" href="#">Terms of Service</a>
          <span className="ml-auto">© 2024 SchedulerPro Inc.</span>
        </div>
      </div>

      {/* צד ימין - טפסים */}
      <div className="flex w-full lg:w-6/12 flex-col bg-white h-full relative overflow-y-auto">
        <div className="flex min-h-full flex-col justify-center px-6 py-12 md:px-12 lg:px-24">
          
          {/* כותרת מובייל */}
          <div className="mb-8 flex flex-col gap-2 lg:hidden text-center items-center">
            <div className="flex items-center gap-2 mb-2">
              <span className="material-symbols-outlined text-[#13a4ec] text-4xl">calendar_month</span>
              <p className="text-3xl font-black tracking-tight text-[#111618]">SchedulerPro</p>
            </div>
            <p className="text-base text-[#617c89]">Manage your time efficiently.</p>
          </div>

          {/* מתג בחירה (Toggle) */}
          <div className="mx-auto w-full max-w-md mb-8">
            <div className="flex w-full items-center justify-center rounded-xl bg-[#f6f7f8] p-1.5 border border-[#dbe2e6]">
              <button
                onClick={() => setAuthMode('login')}
                className={`flex-1 rounded-lg py-3 text-sm font-bold transition-all duration-200 ${
                  authMode === 'login' 
                    ? 'bg-white text-[#13a4ec] shadow-sm' 
                    : 'text-[#617c89] hover:text-[#111618]'
                }`}
              >
                Sign In
              </button>
              <button
                onClick={() => setAuthMode('register')}
                className={`flex-1 rounded-lg py-3 text-sm font-bold transition-all duration-200 ${
                  authMode === 'register' 
                    ? 'bg-white text-[#13a4ec] shadow-sm' 
                    : 'text-[#617c89] hover:text-[#111618]'
                }`}
              >
                Create Account
              </button>
            </div>
          </div>

          {/* תוכן הטפסים */}
          <div className="mx-auto w-full max-w-md flex-1">
            
            {/* טופס הרשמה */}
            {authMode === 'register' && (
              <div className="flex flex-col gap-6 animate-fade-in">
                <div className="flex flex-col gap-2 text-center lg:text-left">
                  <h1 className="text-3xl font-extrabold text-[#111618] tracking-tight">Get started free</h1>
                  <p className="text-base text-[#617c89]">Free forever. No credit card needed.</p>
                </div>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-[#111618]">Username or Email</span>
                  <div className="relative group">
                    <input className="w-full rounded-xl border border-[#dbe2e6] bg-white px-4 py-4 text-base text-[#111618] placeholder-[#617c89] focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] transition-colors outline-none" placeholder="e.g. user@company.com" type="email"/>
                    <span className="absolute right-4 top-1/2 -translate-y-1/2 text-[#617c89] group-focus-within:text-[#13a4ec] transition-colors">
                      <span className="material-symbols-outlined text-[20px]">person</span>
                    </span>
                  </div>
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-[#111618]">Password</span>
                  <div className="relative group">
                    <input className="w-full rounded-xl border border-[#dbe2e6] bg-white px-4 py-4 text-base text-[#111618] placeholder-[#617c89] focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] transition-colors outline-none" placeholder="Create a password" type="password"/>
                    <button className="absolute right-4 top-1/2 flex -translate-y-1/2 items-center text-[#617c89] hover:text-[#13a4ec] transition-colors" type="button">
                      <span className="material-symbols-outlined text-[20px]">visibility_off</span>
                    </button>
                  </div>
                  {/* מד חוזק סיסמה */}
                  <div className="flex gap-1.5 mt-2">
                    <div className="h-1.5 flex-1 rounded-full bg-[#dbe2e6]"></div>
                    <div className="h-1.5 flex-1 rounded-full bg-[#dbe2e6]"></div>
                    <div className="h-1.5 flex-1 rounded-full bg-[#dbe2e6]"></div>
                    <div className="h-1.5 flex-1 rounded-full bg-[#dbe2e6]"></div>
                  </div>
                  <span className="text-xs text-[#617c89] mt-1">Use 8 or more characters with a mix of letters, numbers & symbols</span>
                </label>

                <label className="flex items-start gap-3 mt-2 cursor-pointer group">
                  <div className="relative flex items-center pt-0.5">
                    <input className="peer h-5 w-5 cursor-pointer appearance-none rounded border border-[#dbe2e6] bg-white checked:bg-[#13a4ec] checked:border-[#13a4ec] focus:ring-0 transition-all" type="checkbox"/>
                    <span className="material-symbols-outlined pointer-events-none absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 text-base text-white opacity-0 peer-checked:opacity-100">check</span>
                  </div>
                  <span className="text-sm leading-tight text-[#617c89] group-hover:text-[#111618] transition-colors">
                    I agree to the <a className="font-medium text-[#13a4ec] hover:underline" href="#">Terms of Service</a> and <a className="font-medium text-[#13a4ec] hover:underline" href="#">Privacy Policy</a>
                  </span>
                </label>

                <button className="mt-4 flex w-full items-center justify-center rounded-xl bg-[#13a4ec] py-4 text-base font-bold text-white shadow-lg shadow-[#13a4ec]/20 transition-all hover:bg-[#0f8ecb] hover:shadow-xl active:scale-[0.98]">
                  Create Account
                </button>

                <div className="mt-6 text-center text-sm text-[#617c89]">
                  Already have an account? <span onClick={() => setAuthMode('login')} className="font-bold text-[#13a4ec] hover:underline cursor-pointer">Log in</span>
                </div>
              </div>
            )}

            {/* טופס התחברות */}
            {authMode === 'login' && (
              <div className="flex flex-col gap-6 animate-fade-in">
                <div className="flex flex-col gap-2 text-center lg:text-left">
                  <h1 className="text-3xl font-extrabold text-[#111618] tracking-tight">Welcome back</h1>
                  <p className="text-base text-[#617c89]">Please enter your details to sign in.</p>
                </div>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-[#111618]">Email</span>
                  <div className="relative group">
                    <input className="w-full rounded-xl border border-[#dbe2e6] bg-white px-4 py-4 text-base text-[#111618] placeholder-[#617c89] focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] transition-colors outline-none" placeholder="Enter your email" type="email"/>
                    <span className="absolute right-4 top-1/2 -translate-y-1/2 text-[#617c89] group-focus-within:text-[#13a4ec] transition-colors">
                      <span className="material-symbols-outlined text-[20px]">mail</span>
                    </span>
                  </div>
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-[#111618]">Password</span>
                  <div className="relative group">
                    <input className="w-full rounded-xl border border-[#dbe2e6] bg-white px-4 py-4 text-base text-[#111618] placeholder-[#617c89] focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec] transition-colors outline-none" placeholder="Enter your password" type="password"/>
                    <button className="absolute right-4 top-1/2 flex -translate-y-1/2 items-center text-[#617c89] hover:text-[#13a4ec] transition-colors" type="button">
                      <span className="material-symbols-outlined text-[20px]">visibility_off</span>
                    </button>
                  </div>
                </label>

                <div className="flex justify-between items-center">
                  <label className="flex items-center gap-2 cursor-pointer group">
                    <div className="relative flex items-center">
                      <input className="peer h-4 w-4 cursor-pointer appearance-none rounded border border-[#dbe2e6] bg-white checked:bg-[#13a4ec] checked:border-[#13a4ec] focus:ring-0 transition-all" type="checkbox"/>
                      <span className="material-symbols-outlined pointer-events-none absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 text-sm text-white opacity-0 peer-checked:opacity-100">check</span>
                    </div>
                    <span className="text-sm text-[#617c89] group-hover:text-[#111618] transition-colors">Remember me</span>
                  </label>
                  <a className="text-sm font-bold text-[#13a4ec] hover:underline" href="#">Forgot password?</a>
                </div>

                <Link to="/" className="mt-4 flex w-full items-center justify-center rounded-xl bg-[#13a4ec] py-4 text-base font-bold text-white shadow-lg shadow-[#13a4ec]/20 transition-all hover:bg-[#0f8ecb] hover:shadow-xl active:scale-[0.98]">
                  Sign In
                </Link>

                <div className="mt-6 text-center text-sm text-[#617c89]">
                  Don't have an account? <span onClick={() => setAuthMode('register')} className="font-bold text-[#13a4ec] hover:underline cursor-pointer">Create account</span>
                </div>
              </div>
            )}

            {/* הפרדה להתחברות עם גוגל/מייקרוסופט */}
            <div className="relative mt-8 flex items-center justify-center">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-[#dbe2e6]"></div>
              </div>
              <div className="relative bg-white px-4 text-xs font-medium uppercase text-[#617c89]">
                Or continue with
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mt-4">
              <button className="flex items-center justify-center gap-3 rounded-xl border border-[#dbe2e6] bg-white py-3 text-sm font-medium text-[#111618] transition-colors hover:bg-[#f6f7f8] hover:border-[#364147]/50">
                <img alt="Google" className="h-5 w-5" src="https://www.svgrepo.com/show/475656/google-color.svg"/>
                Google
              </button>
              <button className="flex items-center justify-center gap-3 rounded-xl border border-[#dbe2e6] bg-white py-3 text-sm font-medium text-[#111618] transition-colors hover:bg-[#f6f7f8] hover:border-[#364147]/50">
                <img alt="Microsoft" className="h-5 w-5" src="https://www.svgrepo.com/show/452263/microsoft.svg"/>
                Microsoft
              </button>
            </div>
            
          </div>

          <div className="mt-auto px-6 py-4 text-center text-xs text-[#617c89] opacity-60 lg:hidden">
            <a className="mx-2 hover:text-[#111618]" href="#">Help Center</a>
            <a className="mx-2 hover:text-[#111618]" href="#">Privacy</a>
            <a className="mx-2 hover:text-[#111618]" href="#">Terms</a>
          </div>
        </div>
      </div>
    </div>
  );
}