import type { Language, Theme } from './siteContent'

export function initialLanguage(): Language {
  if (typeof window === 'undefined') return 'zh'
  const saved = localStorage.getItem('notifybox-language')
  if (saved === 'zh' || saved === 'en') return saved
  return navigator.language.toLowerCase().startsWith('zh') ? 'zh' : 'en'
}

export function initialTheme(): Theme {
  if (typeof window === 'undefined') return 'dark'
  const saved = localStorage.getItem('notifybox-theme')
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark'
}
