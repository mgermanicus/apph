import { ITable } from '../../utils';
import {
  Alert,
  Card,
  CardActionArea,
  CardContent,
  CardMedia,
  Grid,
  Typography
} from '@mui/material';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import * as React from 'react';

export const DisplayPhoto = ({
  selectedFolder
}: {
  selectedFolder: string;
}): JSX.Element => {
  const [photoList, setPhotoList] = useState<ITable[]>([]);
  const [errorMessage, setErrorMessage] = useState('');

  const getPhotos = async () => {
    await PhotoService.getFolderPhoto(
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
    getPhotos().catch(console.error);
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
            <Card sx={{ maxWidth: 200 }}>
              <CardActionArea>
                <CardMedia
                  image={
                    'https://media.discordapp.net/attachments/821089093730959382/898592598854348800/Bloody_Hell.jpg'
                  }
                  sx={{ height: 100, objectFit: 'scale-down' }}
                />
                <CardContent>
                  <Typography>{photo.title}</Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    );
  }
};
