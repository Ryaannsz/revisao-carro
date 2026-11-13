/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        // Tinta: verde-grafite usado no menu, títulos e texto
        ink: { DEFAULT: '#22302B', 2: '#2E3F39', 3: '#40534C', soft: '#56665F', faint: '#8A968F' },
        // Fundo "concreto" e superfícies
        paper: { DEFAULT: '#F2F3EF', 2: '#E7E9E3' },
        line: { DEFAULT: '#D9DDD5', strong: '#BFC5BB' },
        // Amarelo de sinalização (ação principal e item ativo)
        signal: { DEFAULT: '#F5B800', deep: '#D49E00', soft: '#FFF4CC' },
        // Situações
        ok: { DEFAULT: '#2E7D4F', soft: '#E2F0E7' },
        due: { DEFAULT: '#B85C10', soft: '#FBEADB' },
        over: { DEFAULT: '#B42318', soft: '#FBE3E0' },
      },
      fontFamily: {
        sans: ['Barlow', 'system-ui', '-apple-system', 'Segoe UI', 'Roboto', 'sans-serif'],
        cond: ['"Barlow Condensed"', 'Barlow', 'system-ui', 'sans-serif'],
      },
      keyframes: {
        'sheet-up': { '0%': { transform: 'translateY(100%)' }, '100%': { transform: 'translateY(0)' } },
        'pop-in': { '0%': { opacity: '0', transform: 'translateY(6px) scale(.98)' }, '100%': { opacity: '1', transform: 'none' } },
        'fade': { '0%': { opacity: '0' }, '100%': { opacity: '1' } },
        'roll': { '0%': { transform: 'translateY(-60%)', opacity: '0' }, '100%': { transform: 'none', opacity: '1' } },
      },
      animation: {
        'sheet-up': 'sheet-up .22s cubic-bezier(.2,.8,.2,1)',
        'pop-in': 'pop-in .18s ease-out',
        'fade': 'fade .18s ease-out',
        'roll': 'roll .45s cubic-bezier(.2,.8,.2,1) both',
      },
    },
  },
  plugins: [],
}
