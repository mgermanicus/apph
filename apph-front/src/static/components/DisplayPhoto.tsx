import { ITable } from '../../utils';
import { Alert, Grid } from '@mui/material';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import * as React from 'react';
import PhotoDetails from './PhotoDetails';

const mediumSize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 2,
  gridItemSM: 4,
  gridItemMD: 4
};

export const DisplayPhoto = ({
  selectedFolder
}: {
  selectedFolder: string;
}): JSX.Element => {
  const [photoList, setPhotoList] = useState<ITable[]>([]);
  const [errorMessage, setErrorMessage] = useState('');
  const [selectedSize, setSelectedSize] = useState(mediumSize);

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
        spacing={selectedSize.gridContainerSpacing}
        columns={selectedSize.gridContainerColumns}
        padding={selectedSize.gridContainerPadding}
      >
        {photoList.map((photo) => (
          <Grid
            item
            xs={selectedSize.gridItemXS}
            sm={selectedSize.gridItemSM}
            md={selectedSize.gridItemMD}
            key={'key' + photo.id}
          >
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
