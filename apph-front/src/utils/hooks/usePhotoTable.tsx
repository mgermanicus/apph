import * as React from 'react';
import { useEffect, useState } from 'react';
import { IFilterPayload } from '../types/Filter';
import { IPagination, ITable } from '../index';
import { PhotoDetails } from '../../static/components/PhotoDetails';
import PhotoService from '../../services/PhotoService';
import { ButtonGroup } from '@mui/material';
import { UploadImage } from '../../static/components/UploadImage';
import { MovePhotoOrFolder } from '../../static/components/MovePhotoOrFolder';
import { DeleteImage } from '../../static/components/DeleteImage';
import { Diaporama } from '../../static/components/Diaporama';
import { PhotoTable } from '../../static/components/PhotoTable';
import { DownloadZip } from '../../static/components/DownloadZip';
import { GridSortModel } from '@mui/x-data-grid';
import { ModifyPhotos } from '../../static/components/ModifyPhotos';

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

  useEffect(() => {
    setLoading(true);
    PhotoService.getData(
      pageSize,
      page + 1,
      handleSuccess,
      handleError,
      sortModel.length > 0 ? sortModel[0] : undefined,
      filterList?.at(0)?.field ? filterList : undefined
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
                sortModel.length > 0 ? sortModel[0] : undefined,
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
  };

  const photoTable = (
    <>
      <ButtonGroup variant="outlined">
        <UploadImage setRefresh={setRefresh} />
        <MovePhotoOrFolder photoIds={selectedIds} />
        <DownloadZip ids={selectedIds} />
        <DeleteImage ids={selectedIds} setRefresh={setRefresh} />
        <Diaporama ids={selectedIds} />
        <ModifyPhotos ids={selectedIds} setRefresh={setRefresh} />
      </ButtonGroup>
      <PhotoTable
        data={data}
        page={page}
        pageSize={pageSize}
        setPage={setPage}
        setPageSize={setPageSize}
        setSelectedIds={setSelectedIds}
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
