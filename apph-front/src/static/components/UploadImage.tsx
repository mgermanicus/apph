import {
  Alert,
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Input,
  LinearProgress,
  Stack,
  TextField,
  Typography
} from '@mui/material';

import PhotoCamera from '@mui/icons-material/PhotoCamera';
import React from 'react';
import ImageService from '../../services/ImageService';
import { UploadStatus } from '../../utils/types/UploadImage';
import verifyFormat from '../../utils/verifyFormat';

function displayAlert(
  uploadStatus: UploadStatus,
  errorMessage = "Une erreur est survenue lors de l'upload"
) {
  switch (uploadStatus) {
    case 'success':
      return <Alert severity="success">Votre fichier a bien été uploadé</Alert>;
    case 'error':
      return <Alert severity="error">{errorMessage}</Alert>;
    default:
      return <></>;
  }
}

export default function UploadImage(): JSX.Element {
  const [title, setTitle] = React.useState('');
  const [uploadStatus, setUploadStatus] = React.useState<UploadStatus>('none');
  const [errorMessage, setErrorMessage] = React.useState<string>('');
  const fileInput = React.createRef<HTMLInputElement>();

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const files = fileInput.current?.files;
    if (files) {
      const file = files[0];
      setUploadStatus('uploading');
      try {
        verifyFormat(file);
        await ImageService.uploadImage(title, file);
        setUploadStatus('success');
      } catch (error) {
        if (error instanceof Error) setErrorMessage(error.message);
        setUploadStatus('error');
      }
    }
  }

  return (
    <Container component="main">
      <CssBaseline>
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <PhotoCamera />
          </Avatar>
          <Typography component="h1" variant="h5">
            Ajouter une photo
          </Typography>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <Stack
              direction="column"
              spacing={2}
              sx={{
                width: {
                  xs: 200,
                  sm: 300,
                  lg: 400,
                  xl: 500
                }
              }}
            >
              <TextField
                required
                fullWidth
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                id="title"
                label="Titre de la photo"
                name="title"
                autoComplete="title"
                autoFocus
              />
              <Input
                fullWidth
                inputRef={fileInput}
                inputProps={{
                  type: 'file',
                  accept: 'image/*'
                }}
                required
                id="fileInput"
              />
              {uploadStatus === 'uploading' && <LinearProgress />}
              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={uploadStatus === 'uploading'}
              >
                Ajouter
              </Button>
              {displayAlert(uploadStatus, errorMessage)}
            </Stack>
          </Box>
        </Box>
      </CssBaseline>
    </Container>
  );
}
