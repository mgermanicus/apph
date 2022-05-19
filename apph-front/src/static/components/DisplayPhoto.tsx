import { ITable } from '../../utils';
import { Alert, Grid } from '@mui/material';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import * as React from 'react';
import PhotoDetails from './PhotoDetails';

export const DisplayPhoto = ({
  selectedFolder
}: {
  selectedFolder: string;
}): JSX.Element => {
  const [photoList, setPhotoList] = useState<ITable[]>([]);
  const [errorMessage, setErrorMessage] = useState('');

  const getPhotos = async () => {
    await PhotoService.getFolderPhotos(
      selectedFolder,
      (list: ITable[]) => {
        setPhotoList(list);
      },
      (error: string) => {
        setErrorMessage(error);
      }
    );
  };

  useEffect(() => {
    getPhotos();
  }, [selectedFolder]);

  if (errorMessage) {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {errorMessage}
      </Alert>
    );
  } else {
    return (
      <Grid
        container
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 8, sm: 12, md: 16 }}
        padding={5}
      >
        {photoList.map((photo) => (
          <Grid item xs={2} sm={4} md={4} key={'key' + photo.id}>
            <PhotoDetails
              photoSrc={photo.url}
              title={photo.title}
              description={photo.description}
              creationDate={photo.creationDate}
              shootingDate={photo.shootingDate}
              size={photo.size}
              tags={photo.tags}
              format={photo.format}
              clickType="card"
            />
          </Grid>
        ))}
      </Grid>
    );
  }
};
