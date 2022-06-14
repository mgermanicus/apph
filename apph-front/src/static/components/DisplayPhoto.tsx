import { ITable } from '../../utils';
import {
  Alert,
  AlertColor,
  Box,
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  Radio,
  RadioGroup
} from '@mui/material';
import * as React from 'react';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import { PhotoDetails } from './PhotoDetails';
import { AlertSnackbar } from './AlertSnackbar';
import { MoveFolders } from './MoveFolders';
import { useTranslation } from 'react-i18next';
import { MovePhotoOrFolder } from './MovePhotoOrFolder';

const tinySize = {
  gridContainerSpacing: { xs: 1, md: 2 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 2.5,
  gridItemXS: 1,
  gridItemSM: 1.5,
  gridItemMD: 2,
  cardStyle: {
    cardMaxWidth: '30vw',
    cardMediaHeight: '10vh'
  }
};

const mediumSize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 2,
  gridItemSM: 3,
  gridItemMD: 4,
  cardStyle: {
    cardMaxWidth: '30vw',
    cardMediaHeight: '20vh'
  }
};

const bigSize = {
  gridContainerSpacing: { xs: 2, md: 3 },
  gridContainerColumns: { xs: 8, sm: 12, md: 16 },
  gridContainerPadding: 5,
  gridItemXS: 4,
  gridItemSM: 6,
  gridItemMD: 8,
  cardStyle: {
    cardMaxWidth: '30vw',
    cardMediaHeight: '30vh'
  }
};

export const DisplayPhoto = ({
  selectedFolder,
  rootFolder
}: {
  selectedFolder: string;
  rootFolder: string | undefined;
}): JSX.Element => {
  const [photoList, setPhotoList] = useState<ITable[]>([]);
  const [errorMessage, setErrorMessage] = useState('');
  const { t } = useTranslation();
  const [selectedSize, setSelectedSize] = useState(tinySize);
  const [refresh, setRefresh] = useState(false);
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('info');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const getPhotos = () => {
    PhotoService.getFolderPhotos(
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
  }, [selectedFolder, refresh]);

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
        setErrorMessage('photo.error.failChangeFormat');
        break;
    }
  }

  if (errorMessage) {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {t(errorMessage)}
      </Alert>
    );
  } else {
    return (
      <>
        <Box component="div" sx={{ display: 'flex', justifyContent: 'center' }}>
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
              <FormControlLabel
                value="tiny"
                control={<Radio />}
                label="Petit"
              />
              <FormControlLabel
                value="medium"
                control={<Radio />}
                label="Moyen"
              />
              <FormControlLabel value="big" control={<Radio />} label="Grand" />
            </RadioGroup>
          </FormControl>
          <MovePhotoOrFolder
            folderToBeMoved={selectedFolder}
            folderId={rootFolder}
          />
        </Box>
        <FormControl sx={{ m: 1 }}>
          <FormLabel id="photo-size-group-label">
            {t('photo.displayFormat')} :
          </FormLabel>
          <RadioGroup
            aria-labelledby="photo-size-group-label"
            defaultValue="tiny"
            name="photo-size-group"
            row
            onChange={handleChangeSize}
          >
            <FormControlLabel
              value="tiny"
              control={<Radio />}
              label={t('size.small')}
            />
            <FormControlLabel
              value="medium"
              control={<Radio />}
              label={t('size.medium')}
            />
            <FormControlLabel
              value="big"
              control={<Radio />}
              label={t('size.large')}
            />
          </RadioGroup>
        </FormControl>
        <Grid
          container
          spacing={selectedSize.gridContainerSpacing}
          columns={selectedSize.gridContainerColumns}
          sx={{ px: selectedSize.gridContainerPadding }}
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
                details={{
                  photoId: photo.id,
                  photoSrc: photo.url,
                  title: photo.title,
                  description: photo.description,
                  creationDate: photo.creationDate,
                  modificationDate: photo.modificationDate,
                  shootingDate: photo.shootingDate,
                  size: photo.size,
                  tags: photo.tags,
                  format: photo.format,
                  fromFolders: true,
                  setRefresh: setRefresh,
                  setSnackbarOpen: setSnackbarOpen,
                  setSnackMessage: setSnackMessage,
                  setSnackSeverity: setSnackSeverity
                }}
                updateData={getPhotos}
                refresh={getPhotos}
                clickType="card"
                cardStyle={selectedSize.cardStyle}
              />
            </Grid>
          ))}
        </Grid>
        <AlertSnackbar
          open={snackbarOpen}
          severity={snackSeverity}
          message={t(snackMessage)}
          onClose={setSnackbarOpen}
        />
      </>
    );
  }
};
