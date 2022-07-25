import * as React from 'react';
import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import PhotoService from '../../services/PhotoService';
import { IMessage, ITable } from '../../utils';
import {
  AlertColor,
  Backdrop,
  Box,
  Button,
  ButtonGroup,
  Grid,
  Pagination,
  Stack,
  Typography
} from '@mui/material';
import { PhotoComplexGrid } from '../components/PhotoComplexGrid';
import CircularProgress from '@mui/material/CircularProgress';
import { useTranslation } from 'react-i18next';
import { AlertSnackbar } from '../components/AlertSnackbar';

export const GlobalSearchPage = ({
  pageSize = 5
}: {
  pageSize?: number;
}): JSX.Element => {
  const { t } = useTranslation();
  const location = useLocation();
  const [data, setData] = useState<ITable[]>([]);
  const [total, setTotal] = React.useState<number>(0);
  const [page, setPage] = React.useState<number>(1);
  const [loading, setLoading] = React.useState<boolean>(false);
  const [message, setMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();
  const [tagFacets, setTagFacets] = useState<Record<string, number>>({});

  const traitError = (error: IMessage) => {
    setMessage(error.message);
    setSnackbarOpen(true);
    setSeverity('error');
  };

  useEffect(() => {
    setLoading(true);
    PhotoService.search(
      location?.search.replace('%2', '/'),
      page,
      pageSize,
      (photoList, totalHits, tagFacets) => {
        setData(photoList);
        setTotal(totalHits);
        setTagFacets(tagFacets);
      },
      (error: IMessage) => traitError(error)
    ).finally(() => {
      setLoading(false);
    });
  }, [location, page]);

  const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
    window.scrollTo(0, 0);
  };

  const getButtonsFromFacets = (facets: Record<string, number>) => (
    <ButtonGroup
      variant="text"
      aria-label="text button group"
      orientation="vertical"
      sx={{ alignItems: 'baseline' }}
    >
      {Object.entries(facets).map(([key, value]) => (
        <Button
          key={key}
          sx={{
            justifyContent: 'left',
            textAlign: 'left',
            maxWidth: 'calc(.25 * (100vw - 28px))',
            overflow: 'hidden'
          }}
          onClick={() => console.log(key)}
        >
          {key} ({value})
        </Button>
      ))}
    </ButtonGroup>
  );

  if (total == 0) {
    return (
      <Grid
        container
        direction="row"
        justifyContent="center"
        alignItems="center"
        sx={{ height: '90vh' }}
      >
        <Typography variant="h6" gutterBottom component="div">
          {t('photo.error.notFound')}
        </Typography>
        <AlertSnackbar
          open={snackbarOpen}
          severity={severity}
          message={t(message)}
          onClose={setSnackbarOpen}
        />
      </Grid>
    );
  }
  return (
    <Box>
      <Grid container spacing={1} justifyContent="flex-start">
        <Grid
          item
          sx={{
            width: 'calc(.25 * (100vw - 28px))',
            minWidth: '249px',
            maxWidth: '374px',
            float: 'left'
          }}
        >
          <Typography variant="h5" gutterBottom component="div">
            {t('photo.enhanceSearch')}
          </Typography>
          <Typography variant="h6" gutterBottom component="div" align="left">
            {t('photo.tagSuggestions')}
          </Typography>
          {getButtonsFromFacets(tagFacets)}
        </Grid>
        <Grid item xs>
          <Stack spacing={2}>
            {data.map((photo) => (
              <React.Fragment key={photo.id}>
                <PhotoComplexGrid photo={photo} />
              </React.Fragment>
            ))}
          </Stack>
          <Stack spacing={2}>
            <Pagination
              count={Math.ceil(total / pageSize)}
              showFirstButton
              showLastButton
              size="large"
              sx={{ margin: 'auto', paddingTop: 1, paddingBottom: 1 }}
              page={page}
              onChange={handleChange}
            />
          </Stack>
          <Backdrop
            sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}
          >
            <CircularProgress color="inherit" />
          </Backdrop>
        </Grid>
      </Grid>
    </Box>
  );
};
