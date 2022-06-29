import i18n from 'i18next';
import moment from 'moment';

const parseDate = (date: string) => {
  const format = i18n.language == 'fr' ? 'DD/MM/yyyy' : 'MM/DD/yyyy';
  return moment(date).format(format);
};

export default parseDate;
