import { Button, DialogContentText } from '@mui/material';
import { IPhoto } from '../../utils/types/Photo';
import PhotoService from '../../services/PhotoService';
import React, { useState } from 'react';

export const DownloadImage = ({ id }: { id: number }): JSX.Element => {
  const [errorMessage, setErrorMessage] = useState('');
  const handleSubmit = () => {
    PhotoService.downloadImage(
      id,
      (photo: IPhoto) => {
        const imageBase64 = `data:image/${photo.extension};base64,${photo.data}`;
        const a = document.createElement('a');
        const event = new MouseEvent('click');
        a.href = imageBase64;
        a.download = photo.title + '.' + photo.extension;
        a.dispatchEvent(event);
      },
      (errorMessage) => {
        setErrorMessage(errorMessage);
      }
    );
  };

  return (
    <Button variant="outlined" onClick={handleSubmit}>
      Télécharger
      <DialogContentText
        sx={{ color: 'red', fontSize: 'small' }}
        hidden={!errorMessage}
      >
        {errorMessage}
      </DialogContentText>
    </Button>
  );
};
