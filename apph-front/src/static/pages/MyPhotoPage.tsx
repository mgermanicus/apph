import * as React from 'react';
import { useEffect, useState } from 'react';
import { PhotoTable } from '../components/PhotoTable';
import { IPagination } from '../../utils';
import PhotoService from '../../services/PhotoService';
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

  const getPhotos = (
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) => {
    PhotoService.getData(pageSize, page, handleSuccess, handleError);
  };

  return (
    <>
      <ButtonGroup variant="outlined" aria-label="outlined button group">
        <UploadImage />
        <Box sx={{ m: 1 }}>
          <DeleteImage ids={selectedIds} />
        </Box>
      </ButtonGroup>
      <PhotoTable getPhotos={getPhotos} />
    </>
  );
};
