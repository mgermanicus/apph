import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
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

  useEffect(() => {
    setSelectedIds(selected.map((photo: ITable) => +photo['id']));
  }, [selected]);

  const getPhotos = (
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) => {
    PhotoService.getData(pageSize, page, handleSuccess, handleError);
  };

  const getPhotosRef = useRef(getPhotos);

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
      <PhotoTable onGetPhotos={getPhotosRef} />
    </>
  );
};
