/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        "primary": "#13a4ec",
        "primary-dark": "#0f8ecb",
        "background-light": "#f6f7f8",
        "background-dark": "#101c22",
        "card-light": "#ffffff",
        "card-dark": "#1a262d",
        "input-bg-light": "#ffffff", 
        "input-bg-dark": "#243036",
        "text-main-light": "#111618",
        "text-main-dark": "#ffffff",
        "text-secondary-light": "#617c89",
        "text-secondary-dark": "#9ca3af",
        "border-light": "#dbe2e6",
        "border-dark": "#364147",
      },
      fontFamily: {
        "display": ["Inter", "sans-serif"]
      },
    },
  },
  plugins: [],
}