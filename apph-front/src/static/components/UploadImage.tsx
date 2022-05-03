import {
  Alert,
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Dialog,
  Input,
  LinearProgress,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import ImageService from '../../services/ImageService';
import { UploadStatus } from '../../utils';
import { createRef, FormEvent, useState } from 'react';
import PhotoCamera from '@mui/icons-material/PhotoCamera';

const displayAlert = (
  uploadStatus: UploadStatus,
  errorMessage = "Une erreur est survenue lors de l'upload"
) => {
  switch (uploadStatus) {
    case 'success':
      return <Alert severity="success">Votre fichier a bien été uploadé</Alert>;
    case 'error':
      return <Alert severity="error">{errorMessage}</Alert>;
    default:
      return <></>;
  }
};

export const UploadImage = (): JSX.Element => {
  const [title, setTitle] = useState('');
  const [uploadStatus, setUploadStatus] = useState<UploadStatus>('none');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const fileInput = createRef<HTMLInputElement>();
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const files = fileInput.current?.files;
    if (files) {
      const file = files[0];
      setUploadStatus('uploading');
      ImageService.uploadImage(
        title,
        file,
        () => {
          setUploadStatus('success');
        },
        (errorMessage) => {
          setUploadStatus('error');
          setErrorMessage(errorMessage);
        }
      );
    }
  };

  return (
    <Box sx={{ mt: 10 }}>
      <Button variant="outlined" onClick={handleClickOpen}>
        Upload
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <Container component="main">
          <CssBaseline>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                mb: 3
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
                      accept: 'image/*',
                      'data-testid': 'file-input'
                    }}
                    required
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
      </Dialog>
    </Box>
  );
};
