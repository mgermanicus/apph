import { styled } from '@mui/material/styles';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import ButtonBase from '@mui/material/ButtonBase';
import { ITable, randomHSL } from '../../utils';
import Box from '@mui/material/Box';
import * as React from 'react';
import { useTranslation } from 'react-i18next';
import { Chip, Stack } from '@mui/material';
import parseDate from '../../utils/DateUtils';

const Img = styled('img')({
  margin: 'auto',
  display: 'block',
  maxWidth: '100%',
  maxHeight: '100%'
});

const titleTypoStyle = { fontWeight: 'bold' };

const detailBoxStyle = { mt: 1, display: 'flex' };

const detailTypoStyle = { ml: 0 };

const photoStyle = {
  width: 'calc(.25 * (100vw - 28px))',
  height: '100%',
  minWidth: '100px',
  maxWidth: '250px',
  maxHeight: '218px'
};

export const PhotoComplexGrid = ({ photo }: { photo: ITable }): JSX.Element => {
  const { t } = useTranslation();

  return (
    <Paper
      sx={{
        p: 2,
        flexGrow: 1,
        backgroundColor: (theme) =>
          theme.palette.mode === 'dark' ? '#1A2027' : '#fff'
      }}
    >
      <Grid container spacing={2}>
        <Grid item>
          <ButtonBase sx={photoStyle}>
            <Img alt={photo.title} src={`${photo.url}?${global.Date.now()}`} />
          </ButtonBase>
        </Grid>
        <Grid item xs={12} sm container>
          <Grid item xs container direction="column" spacing={2}>
            <Grid item xs={6}>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.title')}:
                </Typography>
                <Typography sx={detailTypoStyle}>{photo.title}</Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.description')}:
                </Typography>
                <Typography sx={detailTypoStyle}>
                  {photo.description}
                </Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Stack direction="row" spacing={1}>
                  <Box sx={{ marginTop: 0.5 }}>
                    <Typography sx={titleTypoStyle}>
                      {t('photoTable.tags')}:
                    </Typography>
                  </Box>
                  {photo.tags.map((tag, index) => (
                    <Chip
                      key={index}
                      label={tag.name}
                      sx={{
                        backgroundColor: randomHSL(tag.name)
                      }}
                    />
                  ))}
                </Stack>
              </Box>
            </Grid>
          </Grid>
          <Grid item xs={6}>
            <Box sx={detailBoxStyle}>
              <Typography sx={titleTypoStyle}>
                {t('photoTable.size')}:
              </Typography>
              <Typography sx={detailTypoStyle}>{photo.size} Ko</Typography>
            </Box>
            <Box sx={detailBoxStyle}>
              <Typography sx={titleTypoStyle}>
                {t('photoTable.creationDate')}:
              </Typography>
              <Typography sx={detailTypoStyle}>
                {parseDate(photo.creationDate?.toLocaleString())}
              </Typography>
            </Box>
            <Box sx={detailBoxStyle}>
              <Typography sx={titleTypoStyle}>
                {t('photoTable.lastModification')}:
              </Typography>
              <Typography sx={detailTypoStyle}>
                {parseDate(photo.modificationDate?.toLocaleString())}
              </Typography>
            </Box>
            <Box sx={detailBoxStyle}>
              <Typography sx={titleTypoStyle}>
                {t('photoTable.shootingDate')}:
              </Typography>
              <Typography sx={detailTypoStyle}>
                {parseDate(photo.shootingDate?.toLocaleString())}
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Grid>
    </Paper>
  );
};
