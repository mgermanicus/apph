import * as React from 'react';
import { useEffect, useState } from 'react';
import { PhotoTable } from '../components/PhotoTable';
import { UploadImage } from '../components/UploadImage';
import { ButtonGroup } from '@mui/material';
import { DeleteImage } from '../components/DeleteImage';
import { useSelector } from 'react-redux';
import { IPagination, ITable } from '../../utils';
import { Diaporama } from '../components/Diaporama';
import PhotoService from '../../services/PhotoService';
import { MovePhoto } from '../components/MovePhoto';

export const MyPhotoPage = (): JSX.Element => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const selected = useSelector(
    ({ selectedPhotos }: { selectedPhotos: ITable[] }) => selectedPhotos
  );
  const getPhotos = (
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) => {
    PhotoService.getData(pageSize, page, handleSuccess, handleError);
  };

  useEffect(() => {
    setSelectedIds(selected.map((photo: ITable) => +photo['id']));
  }, [selected]);

  return (
    <>
      <ButtonGroup
        variant="outlined"
        sx={{ m: 1, display: 'flex', justifyContent: 'end' }}
      >
        <UploadImage />
        <MovePhoto photoIds={selectedIds} />
        <DeleteImage ids={selectedIds} />
        <Diaporama data={selected} />
      </ButtonGroup>
      <PhotoTable getPhotos={getPhotos} />
    </>
  );
};
