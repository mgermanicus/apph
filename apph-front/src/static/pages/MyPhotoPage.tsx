import * as React from 'react';
import { useEffect, useState } from 'react';
import { PhotoTable } from '../components/PhotoTable';
import { UploadImage } from '../components/UploadImage';
import { Box, ButtonGroup } from '@mui/material';
import { DeleteImage } from '../components/DeleteImage';
import { useSelector } from 'react-redux';
import { IPhoto } from '../../utils';

export const MyPhotoPage = (): JSX.Element => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  const selected = useSelector(
    ({ selectedPhotos }: { selectedPhotos: IPhoto[] }) => selectedPhotos
  );

  useEffect(() => {
    setSelectedIds(selected.map((photo: IPhoto) => +photo['id']));
  }, [selected]);

  return (
    <>
      <ButtonGroup variant="outlined" aria-label="outlined button group">
        <UploadImage />
        <Box sx={{ m: 1 }}>
          <DeleteImage ids={selectedIds} />
        </Box>
      </ButtonGroup>
      <PhotoTable />
    </>
  );
};
