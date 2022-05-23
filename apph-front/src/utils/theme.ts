import { makeStyles } from '@mui/styles';

export const makeCardStyles = makeStyles({
  cardStyle: {
    minWidth: 100,
    width: '30vw',
    display: 'inline-block',
    marginTop: 150
  }
});

export const makeAppBarStyles = makeStyles({
  iconButton: {
    mr: 2
  }
});

export const makeDiapoStyles = makeStyles({
  displayContent: {
    display: 'contents'
  },
  swiper: {
    width: '80vw',
    height: '95vh',
    marginTop: '10px'
  },
  swiperSlide: {
    textAlign: 'center',
    fontSize: '18px',
    background: 'transparent',
    display: 'flex',
    justifyContent: 'center'
  },
  swiperImg: {
    maxWidth: '90vw',
    maxHeight: '90vh'
  },
  button: {
    float: 'right',
    '&:hover': {
      color: '#0032ff'
    }
  },
  swiperImgAutoPlay: {
    maxWidth: '100vw',
    maxHeight: '100vh'
  }
});
