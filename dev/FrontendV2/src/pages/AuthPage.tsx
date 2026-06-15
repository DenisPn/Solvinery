import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth.service';
import { useAuth } from '../context/AuthContext';

export default function AuthPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  
  const [authMode, setAuthMode] = useState<'login' | 'register'>('register');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    userName: '',
    email: '',
    password: '',
    nickname: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const switchMode = (mode: 'login' | 'register') => {
    setAuthMode(mode);
    setError(null);
    setSuccessMessage(null);
  };

  const handleLogin = async () => {
    setError(null);
    setSuccessMessage(null);
    setIsLoading(true);
    try {
      const response = await authService.login({
        userName: formData.userName,
        password: formData.password
      });
      login(response.userId, formData.userName, formData.email);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Login failed due to an unknown error.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async () => {
    setError(null);
    setSuccessMessage(null);
    setIsLoading(true);
    try {
      await authService.register({
        userName: formData.userName,
        nickname: formData.nickname,
        email: formData.email,
        password: formData.password
      });
      setSuccessMessage('Account created successfully! Please sign in with your new credentials.');
      setAuthMode('login');
    } catch (err: any) {
      setError(err.message || 'Registration failed due to an unknown error.');
    } finally {
      setIsLoading(false);
    }
  };

  const toggleButtons = (
    <div className="flex w-full items-center justify-center rounded-xl bg-[#f6f7f8] p-1.5 border border-[#dbe2e6] mb-6">
      <button
        onClick={() => switchMode('login')}
        className={`flex-1 rounded-lg py-1 text-sm font-bold transition-all duration-200 ${
          authMode === 'login' ? 'bg-white text-[#13a4ec] shadow-sm' : 'text-[#617c89] hover:text-[#111618]'
        }`}
      >
        Sign In
      </button>
      <button
        onClick={() => switchMode('register')}
        className={`flex-1 rounded-lg py-1 text-sm font-bold transition-all duration-200 ${
          authMode === 'register' ? 'bg-white text-[#13a4ec] shadow-sm' : 'text-[#617c89] hover:text-[#111618]'
        }`}
      >
        Create Account
      </button>
    </div>
  );

  return (
    <div className="flex h-screen w-full overflow-hidden bg-[#f6f7f8] font-sans text-[#111618]">
      
      {/* Left Side (Image) */}
      <div 
        className="hidden lg:flex w-6/12 flex-col justify-between bg-cover bg-center p-16 relative" 
        style={{ 
          backgroundImage: `linear-gradient(180deg, rgba(19, 164, 236, 0.2) 0%, rgba(16, 28, 34, 0.95) 100%), url('https://images.unsplash.com/photo-1611095790444-1dfa35e37b52?q=80&w=2071&auto=format&fit=crop')` 
        }}
      >
        <div className="absolute inset-0 bg-black/10 backdrop-blur-[1px]"></div>
        <div className="relative z-10 flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[#13a4ec] text-white shadow-lg backdrop-blur-sm bg-opacity-90">
            <span className="material-symbols-outlined text-3xl">calendar_month</span>
          </div>
          <h2 className="text-3xl font-black tracking-tight text-white drop-shadow-md">Solvinery</h2>
        </div>
        <div className="relative z-10 flex flex-col gap-8 max-w-xl">
          <h3 className="text-5xl font-extrabold leading-tight text-white drop-shadow-lg tracking-tight">
            Master your schedule with precision.
          </h3>
          <div className="mt-8 flex gap-3">
            <div className="h-2 w-16 rounded-full bg-[#13a4ec] shadow-[0_0_15px_rgba(19,164,236,0.6)]"></div>
            <div className="h-2 w-3 rounded-full bg-white/30 backdrop-blur-sm"></div>
          </div>
        </div>
        <div className="relative z-10 mt-auto pt-8 flex gap-6 text-sm text-white/60">
          <span className="ml-auto">© 2026 Solvinery Inc.</span>
        </div>
      </div>

      {/* Right Side (Forms) */}
      <div className="flex w-full lg:w-6/12 flex-col bg-white h-full relative overflow-y-auto">
        <div className="flex min-h-full flex-col justify-center px-6 py-12 md:px-12 lg:px-24">
          
          <div className="mx-auto w-full max-w-xl flex-1">

            {/* Success Message */}
            {successMessage && (
              <div className="mb-6 p-4 rounded-lg bg-green-50 text-green-700 border border-green-200 flex items-start gap-3">
                <span className="material-symbols-outlined text-xl">check_circle</span>
                <span className="text-sm font-medium">{successMessage}</span>
              </div>
            )}

            {/* Error Message */}
            {error && (
              <div className="mb-6 p-4 rounded-lg bg-red-50 text-red-600 border border-red-200 flex items-start gap-3">
                <span className="material-symbols-outlined text-xl">error</span>
                <span className="text-sm font-medium">{error}</span>
              </div>
            )}

            {/* REGISTER FORM */}
            {authMode === 'register' && (
              <div className="flex flex-col gap-0 animate-fade-in">
                <div className="text-center lg:text-left mb-4">
                  <h1 className="text-4xl font-extrabold text-[#111618] mb-2">Get started free</h1>
                  <p className="text-lg text-[#617c89]">Free forever. No credit card needed.</p>
                </div>

                {toggleButtons}

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Username</span>
                  <input 
                    name="userName"
                    value={formData.userName}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="Username" 
                    type="text"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Nickname</span>
                  <input 
                    name="nickname"
                    value={formData.nickname}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="Display Name" 
                    type="text"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Email</span>
                  <input 
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="user@example.com" 
                    type="email"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Password</span>
                  <input 
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="Password" 
                    type="password"
                  />
                </label>

                <button 
                  onClick={handleRegister}
                  disabled={isLoading}
                  className="mt-6 w-full rounded-xl bg-[#13a4ec] py-1 text-lg font-bold text-white shadow-lg transition-all hover:bg-[#0f8ecb] active:scale-[0.98] disabled:opacity-70"
                >
                  {isLoading ? 'Creating Account...' : 'Create Account'}
                </button>
              </div>
            )}

            {/* LOGIN FORM */}
            {authMode === 'login' && (
              <div className="flex flex-col gap-6 animate-fade-in">
                <div className="text-center lg:text-left mb-4">
                  <h1 className="text-4xl font-extrabold text-[#111618] mb-2">Welcome back</h1>
                  <p className="text-lg text-[#617c89]">Please enter your details to sign in.</p>
                </div>

                {toggleButtons}

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Username</span>
                  <input 
                    name="userName"
                    value={formData.userName}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="Enter your username" 
                    type="text"
                  />
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-base font-medium text-[#111618]">Password</span>
                  <input 
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    className="w-full rounded-xl border border-[#dbe2e6] bg-white px-5 py-1 text-base outline-none focus:border-[#13a4ec] focus:ring-1 focus:ring-[#13a4ec]" 
                    placeholder="Enter your password" 
                    type="password"
                  />
                </label>

                <button 
                  onClick={handleLogin}
                  disabled={isLoading}
                  className="mt-6 w-full rounded-xl bg-[#13a4ec] py-1 text-lg font-bold text-white shadow-lg transition-all hover:bg-[#0f8ecb] active:scale-[0.98] disabled:opacity-70"
                >
                  {isLoading ? 'Signing In...' : 'Sign In'}
                </button>
              </div>
            )}
            
          </div>
        </div>
      </div>
    </div>
  );
}