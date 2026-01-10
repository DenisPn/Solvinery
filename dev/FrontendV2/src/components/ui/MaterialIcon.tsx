import React from 'react'; // אפשר למחוק אם משתמשים ב-React 17+ (ראה סעיף 3)

interface MaterialIconProps {
  icon: string;
  className?: string; // ה-? אומר שזה שדה רשות
}

export default function MaterialIcon({ icon, className = "" }: MaterialIconProps) {
  return (
    <span className={`material-symbols-outlined select-none ${className}`}>
      {icon}
    </span>
  );
}