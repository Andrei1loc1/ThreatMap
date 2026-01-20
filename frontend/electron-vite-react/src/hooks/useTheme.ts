import { useState, useEffect } from 'react';

const themes = {
  blue: {
    primary: '#22d3ee',
    secondary: '#0ea5e9',
  },
  purple: {
    primary: '#a855f7',
    secondary: '#9333ea',
  },
};

export const useTheme = () => {
  const [theme, setTheme] = useState('blue');

  useEffect(() => {
    const saved = localStorage.getItem('theme') || 'blue';
    setTheme(saved);
    applyTheme(saved);
  }, []);

  const applyTheme = (themeName: string) => {
    const t = themes[themeName as keyof typeof themes];
    if (t) {
      document.documentElement.style.setProperty('--primary-color', t.primary);
      document.documentElement.style.setProperty('--secondary-color', t.secondary);
    }
  };

  const changeTheme = (themeName: string) => {
    setTheme(themeName);
    localStorage.setItem('theme', themeName);
    applyTheme(themeName);
  };

  return { theme, changeTheme };
};