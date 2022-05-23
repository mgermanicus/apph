import { Box, Button, Modal, Tooltip, Typography } from '@mui/material';
import { IMessage, IPhoto, IMessage } from '../../utils';
import PhotoService from '../../services/PhotoService';
import React, { useState } from 'react';
import { Download } from '@mui/icons-material';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 0.2,
  maxHeight: 0.7,
  bgcolor: '#eceff1',
  border: '2px solid #eceff1',
  boxShadow: 24,
  color: '#d50000',
  p: 4
};

export const DownloadImage = ({ id }: { id: number }): JSX.Element => {
  const [errorMessage, setErrorMessage] = useState('');
  const handleClose = () => setErrorMessage('');
  const handleSubmit = () => {
    PhotoService.downloadImage(
      id,
      (photo: IPhoto) => {
        const imageBase64 = `data:image/${photo.format};base64,${photo.data}`;
        const a = document.createElement('a');
        const event = new MouseEvent('click');
        a.href = imageBase64;
        a.download = photo.title + photo.format;
        a.dispatchEvent(event);
      },
      (error: IMessage) => {
        setErrorMessage(error.message);
      }
    );
  };

  return (
    <>
      <Tooltip title="Télécharger">
        <Button
          variant="outlined"
          onClick={handleSubmit}
          id={`download-${id}`}
          aria-label="download-photo"
        >
          <Download />
        </Button>
      </Tooltip>
      <Modal
        open={errorMessage !== ''}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={modalStyle}>
          <Typography id="modal-modal-title" variant="h6" component="h2">
            Alert
          </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 2 }}>
            {errorMessage}
          </Typography>
        </Box>
      </Modal>
    </>
  );
};
