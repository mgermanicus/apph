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
import PhotoDetails from '../components/PhotoDetails';
import { AlertSnackbar } from '../components/AlertSnackbar';

export const MyPhotoPage = (): JSX.Element => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [refresh, setRefresh] = useState(false);
  const [data, setData] = useState<ITable[]>([]);
  const [totalSize, setTotalSize] = useState<number>(0);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [pageSize, setPageSize] = useState<number>(5);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);

  const handleError = (error: string) => {
    setErrorMessage(error);
    setLoading(false);
  };

  const selected = useSelector(
    ({ selectedPhotos }: { selectedPhotos: ITable[] }) => selectedPhotos
  );

  const handleSuccess = (pagination: IPagination) => {
    pagination.photoList.forEach(
      (row) =>
        (row.details = (
          <PhotoDetails
            photoSrc={row.url}
            title={row.title}
            description={row.description}
            creationDate={row.creationDate}
            shootingDate={row.shootingDate}
            size={row.size}
            tags={row.tags}
            format={row.format}
            clickType="button"
          />
        ))
    );
    setData(pagination.photoList);
    setTotalSize(pagination.totalSize);
    setLoading(false);
  };

  useEffect(() => {
    setSelectedIds((values) =>
      Array.from(
        new Set(values.concat(selected.map((photo: ITable) => +photo['id'])))
      )
    );
  }, [selected]);

  useEffect(() => {
    setLoading(true);
    PhotoService.getData(pageSize, page + 1, handleSuccess, handleError);
  }, [refresh, page, pageSize]);

  return (
    <>
      <ButtonGroup
        variant="outlined"
        sx={{ m: 1, display: 'flex', justifyContent: 'end' }}
      >
        <UploadImage setRefresh={setRefresh} />
        <MovePhoto photoIds={selectedIds} />
        <DeleteImage ids={selectedIds} setRefresh={setRefresh} />
        <Diaporama data={selected} />
      </ButtonGroup>
      <PhotoTable
        data={data}
        selected={selectedIds}
        refresh={refresh}
        page={page}
        pageSize={pageSize}
        setPage={setPage}
        setPageSize={setPageSize}
        totalSize={totalSize}
        loading={loading}
      />
      <AlertSnackbar
        open={!!errorMessage}
        severity={'warning'}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </>
  );
};
