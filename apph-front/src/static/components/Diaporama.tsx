import React, { useEffect, useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { ITable, openFullScreenById } from '../../utils';
import { Box, Button, IconButton, Modal, Tooltip } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { Autoplay, EffectCoverflow, Navigation, Pagination } from 'swiper';
import { AlertSnackbar } from './AlertSnackbar';

import 'swiper/css';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import 'swiper/css/autoplay';
import 'swiper/css/effect-coverflow';
import { Slideshow } from '@mui/icons-material';

const diapoStyles = {
  displayContent: {
    display: 'contents'
  },
  swiper: {
    width: '80vw',
    height: '95vh',
    marginTop: '10px'
  },
  swiperSlide: {
    textAlign: 'center' as const,
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
    },
    color: '#fafafa'
  },
  swiperImgAutoPlay: {
    maxWidth: '100vw',
    maxHeight: '100vh'
  }
};

export const Diaporama = ({ data }: { data: ITable[] }) => {
  const [open, setOpen] = useState(false);
  const [autoPlay, setAutoPlay] = useState(false);
  const [alertOpen, setAlertOpen] = useState(false);

  const handleFullScreen = () => {
    if (!document.fullscreenElement) {
      setAutoPlay(false);
      setOpen(true);
    }
  };

  document.addEventListener('fullscreenchange', handleFullScreen);

  useEffect(() => {
    return () =>
      window.removeEventListener('fullscreenchange', handleFullScreen);
  }, [handleFullScreen]);

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title="Diaporama">
        <Button
          variant="outlined"
          onClick={() => {
            data.length ? setOpen(true) : setAlertOpen(true);
          }}
        >
          <Slideshow />
        </Button>
      </Tooltip>
      <>
        <Modal open={open} onClose={() => setOpen(false)}>
          <>
            <Box sx={diapoStyles.displayContent}>
              <IconButton
                onClick={() => setOpen(false)}
                sx={diapoStyles.button}
                aria-label="close"
                size="large"
              >
                <CloseIcon />
              </IconButton>
            </Box>
            <Swiper
              slidesPerView={'auto'}
              effect={'coverflow'}
              grabCursor={true}
              centeredSlides={true}
              coverflowEffect={{
                rotate: 50,
                stretch: 0,
                depth: 100,
                modifier: 1,
                slideShadows: true
              }}
              loop={true}
              pagination={{
                clickable: true
              }}
              navigation={true}
              modules={[Pagination, Navigation, EffectCoverflow]}
              style={diapoStyles.swiper}
            >
              {!!data?.length &&
                data.map((photo, index) => (
                  <SwiperSlide
                    style={diapoStyles.swiperSlide}
                    key={photo.id}
                    virtualIndex={index}
                  >
                    <Box component="div" sx={{ alignSelf: 'center' }}>
                      <img
                        style={diapoStyles.swiperImg}
                        src={photo.url}
                        alt={photo.title}
                        onClick={() => {
                          setOpen(false);
                          setAutoPlay(true);
                          openFullScreenById('full-screen');
                        }}
                      />
                    </Box>
                  </SwiperSlide>
                ))}
            </Swiper>
          </>
        </Modal>
        <Modal open={autoPlay} onClose={() => setAutoPlay(false)}>
          <>
            <Swiper
              slidesPerView={1}
              spaceBetween={30}
              loop={true}
              modules={[Autoplay]}
              autoplay={{ delay: 1500 }}
              style={diapoStyles.swiper}
              id="full-screen"
            >
              {!!data?.length &&
                data.map((photo, index) => (
                  <SwiperSlide
                    style={diapoStyles.swiperSlide}
                    key={photo.id}
                    virtualIndex={index}
                  >
                    <Box component="div" sx={{ alignSelf: 'center' }}>
                      <img
                        style={diapoStyles.swiperImgAutoPlay}
                        src={photo.url}
                        alt={photo.title}
                      />
                    </Box>
                  </SwiperSlide>
                ))}
            </Swiper>
          </>
        </Modal>
      </>
      <AlertSnackbar
        open={alertOpen}
        severity={'warning'}
        message={'Veuillez sélectionner les photos à afficher.'}
        onClose={setAlertOpen}
      />
    </Box>
  );
};
