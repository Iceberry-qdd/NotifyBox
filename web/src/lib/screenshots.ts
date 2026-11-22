import type { Language, Theme } from './siteContent'

export type Screen = 'home' | 'statistic' | 'log' | 'transfer'

export function screenshot(screen: Screen, language: Language, theme: Theme) {
  const prefix = screen === 'transfer' ? (language === 'zh' ? 'backup' : 'export') : screen
  return `${import.meta.env.BASE_URL}screenshots/Screenshot_${prefix}_${language}_${theme}.webp`
}
