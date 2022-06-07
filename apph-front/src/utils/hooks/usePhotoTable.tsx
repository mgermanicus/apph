import { useEffect, useState } from 'react';
import { IFilterPayload } from '../types/Filter';
import { IPagination, ITable } from '../index';
import { useSelector } from 'react-redux';
import PhotoDetails from '../../static/components/PhotoDetails';
import PhotoService from '../../services/PhotoService';
import * as React from 'react';
import { ButtonGroup } from '@mui/material';
import { UploadImage } from '../../static/components/UploadImage';
import { MovePhoto } from '../../static/components/MovePhoto';
import { DeleteImage } from '../../static/components/DeleteImage';
import { Diaporama } from '../../static/components/Diaporama';
import { PhotoTable } from '../../static/components/PhotoTable';

export const usePhotoTable = (filterList?: IFilterPayload[]) => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [refresh, setRefresh] = useState(false);
  const [data, setData] = useState<ITable[]>([]);
  const [totalSize, setTotalSize] = useState<number>(0);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [pageSize, setPageSize] = useState<number>(5);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const selected = useSelector(
    ({ selectedPhotos }: { selectedPhotos: ITable[] }) => selectedPhotos
  );

  useEffect(() => {
    setSelectedIds((values) =>
      Array.from(
        new Set(values.concat(selected.map((photo: ITable) => +photo['id'])))
      )
    );
  }, [selected]);

  useEffect(() => {
    setLoading(true);
    PhotoService.getData(
      pageSize,
      page + 1,
      handleSuccess,
      handleError,
      filterList
    );
  }, [refresh, page, pageSize, filterList ? filterList : null]);

  const handleError = (error: string) => {
    setErrorMessage(error);
    setLoading(false);
  };

  const handleSuccess = (pagination: IPagination) => {
    pagination.photoList.forEach(
      (row) =>
        (row.details = (
          <PhotoDetails
            photoSrc={row.url}
            title={row.title}
            description={row.description}
            creationDate={row.creationDate}
            modificationDate={row.modificationDate}
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

  const photoTable = (
    <>
      <ButtonGroup variant="outlined">
        <UploadImage setRefresh={setRefresh} />
        <MovePhoto photoIds={selectedIds} />
        <DeleteImage ids={selectedIds} setRefresh={setRefresh} />
        <Diaporama data={selected} />
      </ButtonGroup>
      <PhotoTable
        data={data}
        selected={selectedIds}
        page={page}
        pageSize={pageSize}
        setPage={setPage}
        setPageSize={setPageSize}
        totalSize={totalSize}
        loading={loading}
      />
    </>
  );
  return {
    errorState: {
      getMessage: errorMessage,
      setMessage: setErrorMessage
    },
    photoTable
  };
};
