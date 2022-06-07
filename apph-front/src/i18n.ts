import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import common_fr from './utils/translations/fr/common.json';
import common_en from './utils/translations/en/common.json';

i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    lng: 'fr',
    interpolation: {
      escapeValue: false // react already safes from xss
    },
    resources: {
      en: {
        translation: common_en
      },
      fr: {
        translation: common_fr
      }
    }
  });

export default i18n;
