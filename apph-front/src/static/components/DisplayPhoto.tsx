import { ITable } from '../../utils';
import {
  Alert,
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  Radio,
  RadioGroup
} from '@mui/material';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import * as React from 'react';
import PhotoDetails from './PhotoDetails';

const tinySize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 1,
  gridItemSM: 1.5,
  gridItemMD: 2
};

const mediumSize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 2,
  gridItemSM: 3,
  gridItemMD: 4
};

const bigSize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 4,
  gridItemSM: 6,
  gridItemMD: 8
};

export const DisplayPhoto = ({
  selectedFolder
}: {
  selectedFolder: string;
}): JSX.Element => {
  const [photoList, setPhotoList] = useState<ITable[]>([]);
  const [errorMessage, setErrorMessage] = useState('');
  const [selectedSize, setSelectedSize] = useState(tinySize);

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

  function handleChangeSize(event: React.ChangeEvent<HTMLInputElement>) {
    switch (event.target.value) {
      case 'tiny':
        setSelectedSize(tinySize);
        break;
      case 'medium':
        setSelectedSize(mediumSize);
        break;
      case 'big':
        setSelectedSize(bigSize);
        break;
      default:
        setErrorMessage('Erreur lors de la modification du format');
        break;
    }
  }

  if (errorMessage) {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {errorMessage}
      </Alert>
    );
  } else {
    return (
      <>
        <FormControl sx={{ m: 1 }}>
          <FormLabel id="photo-size-group-label">
            Format d'affichage :
          </FormLabel>
          <RadioGroup
            aria-labelledby="photo-size-group-label"
            defaultValue="tiny"
            name="photo-size-group"
            row
            onChange={handleChangeSize}
          >
            <FormControlLabel value="tiny" control={<Radio />} label="Petit" />
            <FormControlLabel
              value="medium"
              control={<Radio />}
              label="Moyen"
            />
            <FormControlLabel value="big" control={<Radio />} label="Grand" />
          </RadioGroup>
        </FormControl>
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
      </>
    );
  }
};
