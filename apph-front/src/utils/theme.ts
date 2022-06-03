import frFlag from './flag/fr.png';
import enFlag from './flag/en.png';

export const cardStyle = {
  cardStyle: {
    minWidth: 100,
    width: '30vw',
    display: 'inline-block',
    marginTop: 150
  }
};

export const flagStyles = {
  fr: {
    backgroundImage: `url(${frFlag})`,
    width: 53,
    height: 32,
    backgroundSize: '100%',
    backgroundRepeat: 'no-repeat',
    borderWidth: 0
  },
  en: {
    backgroundImage: `url(${enFlag})`,
    width: 53,
    height: 32,
    marginLeft: 20,
    backgroundSize: '100%',
    backgroundRepeat: 'no-repeat',
    borderWidth: 0
  }
};
