import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';

export default function Navbar() {
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  // בדיקה האם הנתיב הנוכחי תואם לקישור לקביעת ה-Style
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
            to="/myimages" // ✅ תוקן: מפנה כעת לנתיב של MyImagesPage
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
            to="/" // ✅ תוקן: מפנה לנתיב הציבורי (אופציונלי)
            className={`transition-colors text-sm leading-normal ${isActive('/public-images')}`}
          >
            Public Problems
          </Link>

          <Link 
            to="/settings" 
            className={`transition-colors text-sm leading-normal ${isActive('/settings')}`}
          >
            Settings
          </Link>
        </nav>

        {/* אזור אישי - התראות ופרופיל */}
        <div className="flex items-center gap-4 relative">
          
          {/* כפתור התראות */}
          <button className="flex items-center justify-center size-10 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 text-text-main-light dark:text-white transition-colors">
            <span className="material-symbols-outlined">notifications</span>
          </button>

          {/* תמונת פרופיל + תפריט נפתח */}
          <div className="relative">
            <div 
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="bg-center bg-no-repeat aspect-square bg-cover rounded-full size-10 ring-2 ring-transparent hover:ring-primary cursor-pointer transition-all border border-gray-200 dark:border-gray-700" 
              style={{ backgroundImage: 'url("https://i.pravatar.cc/150?img=12")' }}
            ></div>

            {/* התפריט הנפתח (Dropdown) */}
            {isMenuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white dark:bg-surface-dark rounded-xl shadow-lg border border-border-light dark:border-border-dark py-1 animate-fade-in z-50">
                <div className="px-4 py-2 border-b border-border-light dark:border-border-dark">
                  <p className="text-sm font-bold text-text-main-light dark:text-white">John Doe</p>
                  <p className="text-xs text-text-secondary-light">user@example.com</p>
                </div>
                
                <Link 
                  to="/auth" 
                  onClick={() => setIsMenuOpen(false)}
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