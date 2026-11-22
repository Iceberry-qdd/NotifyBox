import { svelte } from '@sveltejs/vite-plugin-svelte'
import { defineConfig } from 'vite'

const repoName = process.env.GITHUB_REPOSITORY?.split('/')[1]

const base = process.env.GITHUB_ACTIONS === 'true' && repoName ? `/${repoName}/` : '/'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
  base
})
