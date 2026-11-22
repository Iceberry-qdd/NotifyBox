import type { Language } from './siteContent'

export function tokens(text: string, language: Language) {
  return language === 'zh' ? [...text] : text.split(' ')
}
