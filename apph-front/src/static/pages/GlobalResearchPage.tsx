import * as React from 'react';
import { useEffect, useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import PhotoService from '../../services/PhotoService';
import { IMessage, ITable } from '../../utils';
import {
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

export const GlobalResearchPage = (): JSX.Element => {
  const location = useLocation();
  const params = useParams();
  const [data, setData] = useState<ITable[]>([]);
  const pageSize = 5;
  const [total, setTotal] = React.useState<number>(0);
  const [page, setPage] = React.useState<number>(1);
  const [loading, setLoading] = React.useState<boolean>(false);

  useEffect(() => {
    setLoading(true);
    PhotoService.search(
      params.target,
      page,
      pageSize,
      (photoList, totalHits) => {
        setData(photoList);
        setTotal(totalHits);
        console.log(photoList);
      },
      (error: IMessage) => console.log(error)
    ).finally(() => {
      setLoading(false);
    });
  }, [location, page]);

  const handleChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
    window.scrollTo(0, 0);
  };

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
          <Typography variant="h6" gutterBottom component="div">
            TODO facets
          </Typography>
          <ButtonGroup
            variant="text"
            aria-label="text button group"
            orientation="vertical"
          >
            <Button>Paris</Button>
            <Button>Toulouse</Button>
          </ButtonGroup>
        </Grid>
        <Grid item xs>
          <Stack spacing={2}>
            {data.map((photo) => (
              <PhotoComplexGrid photo={photo} />
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
