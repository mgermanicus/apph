import * as React from 'react';
import {
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Input,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { PhotoCamera } from '@mui/icons-material';

export default function UploadImage(): JSX.Element {
  function handleSubmit() {
    // TODO
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
                id="title"
                label="Titre de la photo"
                name="title"
                autoComplete="title"
                autoFocus
              />
              <Input
                fullWidth
                inputProps={{ type: 'file', accept: 'image/*' }}
                id="fileInput"
              />
              <Button type="submit" fullWidth variant="contained">
                Ajouter
              </Button>
            </Stack>
          </Box>
        </Box>
      </CssBaseline>
    </Container>
  );
}
