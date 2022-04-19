import {
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

export default function UploadImage(): JSX.Element {
  const [title, setTitle] = React.useState('');
  const [isInProgress, setIsInProgress] = React.useState(false);
  const fileInput = React.createRef<HTMLInputElement>();

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const imageFiles = fileInput.current?.files;
    if (imageFiles) {
      setIsInProgress(true);
      ImageService.uploadImage(title, imageFiles[0])?.then(() => {
        // TODO error handling
        setIsInProgress(false);
      });
    } else {
      // TODO
      throw Error;
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
          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
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
                id="fileInput"
              />
              {isInProgress && <LinearProgress />}
              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={isInProgress}
              >
                Ajouter
              </Button>
            </Stack>
          </Box>
        </Box>
      </CssBaseline>
    </Container>
  );
}
