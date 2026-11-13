import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // A API (CorsConfig) só libera a origem http://localhost:5173. Sem o
  // strictPort, se a 5173 estiver ocupada o Vite sobe na 5174, e a API recusa
  // essa origem. Com ele, o Vite para com erro e avisa que a porta está em uso.
  // O preview (npm run preview, que serve o build) usa a mesma porta pelo
  // mesmo motivo; o padrão dele seria a 4173.
  server: {
    port: 5173,
    strictPort: true,
  },
  preview: {
    port: 5173,
    strictPort: true,
  },
})
