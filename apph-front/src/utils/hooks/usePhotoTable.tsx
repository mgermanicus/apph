import * as React from 'react';
import { useEffect, useState } from 'react';
import { IFilterPayload } from '../types/Filter';
import { IPagination, ITable } from '../index';
import { useSelector } from 'react-redux';
import { PhotoDetails } from '../../static/components/PhotoDetails';
import PhotoService from '../../services/PhotoService';
import { ButtonGroup } from '@mui/material';
import { UploadImage } from '../../static/components/UploadImage';
import { MovePhoto } from '../../static/components/MovePhoto';
import { DeleteImage } from '../../static/components/DeleteImage';
import { Diaporama } from '../../static/components/Diaporama';
import { PhotoTable } from '../../static/components/PhotoTable';
import { DownloadZip } from '../../static/components/DownloadZip';
import { GridSortModel } from '@mui/x-data-grid';

export const usePhotoTable = (filterList?: IFilterPayload[]) => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [refresh, setRefresh] = useState(false);
  const [data, setData] = useState<ITable[]>([]);
  const [totalSize, setTotalSize] = useState<number>(0);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [pageSize, setPageSize] = useState<number>(5);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [sortModel, setSortModel] = useState<GridSortModel>([]);
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
      sortModel,
      filterList
    );
  }, [refresh, page, pageSize, sortModel, filterList ? filterList : null]);

  const handleError = (error: string) => {
    setErrorMessage(error);
    setLoading(false);
  };

  const handleSuccess = (pagination: IPagination) => {
    pagination.photoList.forEach(
      (row) =>
        (row.details = (
          <PhotoDetails
            details={{
              photoId: row.id,
              photoSrc: row.url,
              title: row.title,
              description: row.description,
              creationDate: row.creationDate,
              modificationDate: row.modificationDate,
              shootingDate: row.shootingDate,
              size: row.size,
              tags: row.tags,
              format: row.format
            }}
            updateData={() =>
              PhotoService.getData(
                pageSize,
                page + 1,
                handleSuccess,
                handleError,
                sortModel,
                filterList
              )
            }
            refresh={() => setRefresh((refresh) => !refresh)}
            clickType="button"
          />
        ))
    );
    setData(pagination.photoList);
    setTotalSize(pagination.totalSize);
    setLoading(false);
  };

  const handleSortModelChange = (model: GridSortModel) => {
    setSortModel(model);
    PhotoService.getData(
      pageSize,
      page + 1,
      handleSuccess,
      handleError,
      sortModel,
      filterList
    );
  };

  const photoTable = (
    <>
      <ButtonGroup variant="outlined">
        <UploadImage setRefresh={setRefresh} />
        <MovePhoto photoIds={selectedIds} />
        <DownloadZip ids={selectedIds} />
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
        handleSortModelChange={handleSortModelChange}
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
