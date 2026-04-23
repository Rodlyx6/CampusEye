/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      boxShadow: {
        soft: '0 12px 30px rgba(15,23,42,0.12)'
      },
      keyframes: {
        pulseRing: {
          '0%': { boxShadow: '0 0 0 0 rgba(239,68,68,0.35)' },
          '70%': { boxShadow: '0 0 0 14px rgba(239,68,68,0)' },
          '100%': { boxShadow: '0 0 0 0 rgba(239,68,68,0)' }
        }
      },
      animation: {
        pulseRing: 'pulseRing 1.6s infinite'
      }
    }
  },
  plugins: []
}
