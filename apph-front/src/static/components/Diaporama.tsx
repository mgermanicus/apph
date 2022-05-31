import React, { useEffect, useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { ITable, makeDiapoStyles, openFullScreenById } from '../../utils';
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

export const Diaporama = ({ data }: { data: ITable[] }) => {
  const [open, setOpen] = useState(false);
  const [autoPlay, setAutoPlay] = useState(false);
  const [alertOpen, setAlertOpen] = useState(false);

  const classes = makeDiapoStyles();

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
            <Box className={classes.displayContent}>
              <IconButton
                onClick={() => setOpen(false)}
                sx={{ color: '#fafafa' }}
                className={classes.button}
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
              className={classes.swiper}
            >
              {!!data?.length &&
                data.map((photo, index) => (
                  <SwiperSlide
                    className={classes.swiperSlide}
                    key={photo.id}
                    virtualIndex={index}
                  >
                    <Box component="div" sx={{ alignSelf: 'center' }}>
                      <img
                        className={classes.swiperImg}
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
              className={classes.swiper}
              id="full-screen"
            >
              {!!data?.length &&
                data.map((photo, index) => (
                  <SwiperSlide
                    className={classes.swiperSlide}
                    key={photo.id}
                    virtualIndex={index}
                  >
                    <Box component="div" sx={{ alignSelf: 'center' }}>
                      <img
                        className={classes.swiperImgAutoPlay}
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
