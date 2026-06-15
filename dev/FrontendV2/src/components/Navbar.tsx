import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { userName, email, logout } = useAuth();

  const isActive = (path: string) => {
    return location.pathname === path
      ? 'text-primary font-bold'
      : 'text-text-main-light dark:text-gray-200 hover:text-primary dark:hover:text-primary font-medium';
  };

  return (
    <header className="sticky top-0 z-50 flex items-center justify-between whitespace-nowrap border-b border-solid border-border-light dark:border-border-dark bg-white dark:bg-surface-dark px-10 py-3 shadow-sm">

      {/* צד שמאל - לוגו */}
      <div className="flex items-center gap-4">
        <Link to="/" className="flex items-center gap-3">
          <div className="size-8 flex items-center justify-center bg-primary/10 rounded-lg text-primary">
            <span className="material-symbols-outlined text-2xl">calendar_month</span>
          </div>
          <h2 className="text-text-main-light dark:text-white text-lg font-bold leading-tight tracking-[-0.015em]">
            Solvinery
          </h2>
        </Link>
      </div>

      {/* צד ימין - ניווט ומשתמש */}
      <div className="flex items-center gap-8">

        {/* תפריט ניווט */}
        <nav className="hidden lg:flex items-center gap-6">
          <Link
            to="/myimages"
            className={`transition-colors text-sm leading-normal ${isActive('/myimages')}`}
          >
            My Problems
          </Link>

          <Link
            to="/new"
            className={`transition-colors text-sm leading-normal ${isActive('/new')}`}
          >
            New Problem
          </Link>
          
          <Link
            to="/community"
            className={`transition-colors text-sm leading-normal ${isActive('/community')}`}
          >
            Community Images
          </Link>

          <Link
            to="/"
            className={`transition-colors text-sm leading-normal ${isActive('/')}`}
          >
            Public Problems
          </Link>
        </nav>

        {/* אזור אישי */}
        <div className="flex items-center gap-4 relative">

          {/* כפתור התראות */}
          <button className="flex items-center justify-center size-10 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 text-text-main-light dark:text-white transition-colors">
            <span className="material-symbols-outlined">notifications</span>
          </button>

          {/* עיגול עם אות ראשונה + תפריט נפתח */}
          <div className="relative">
            <div
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="flex items-center justify-center size-10 rounded-full bg-[#13a4ec] text-white font-bold cursor-pointer hover:bg-[#0f8ecb] transition-all"
            >
              {userName ? userName[0].toUpperCase() : '?'}
            </div>

            {/* התפריט הנפתח */}
            {isMenuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white dark:bg-surface-dark rounded-xl shadow-lg border border-border-light dark:border-border-dark py-1 animate-fade-in z-50">
                <div className="px-4 py-2 border-b border-border-light dark:border-border-dark">
                  <p className="text-sm font-bold text-text-main-light dark:text-white">{userName}</p>
                </div>

                <Link
                  to="/auth"
                  onClick={() => { logout(); setIsMenuOpen(false); }}
                  className="block px-4 py-2 text-sm text-text-main-light dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-700 hover:text-primary"
                >
                  Log Out
                </Link>
              </div>
            )}
          </div>

        </div>
      </div>
    </header>
  );
}