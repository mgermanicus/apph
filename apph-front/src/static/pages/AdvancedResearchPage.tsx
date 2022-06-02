import * as React from 'react';
import { useEffect, useState } from 'react';
import { PhotoTable } from '../components/PhotoTable';
import { IPagination, ITable } from '../../utils';
import PhotoService from '../../services/PhotoService';
import { UploadImage } from '../components/UploadImage';
import { ButtonGroup } from '@mui/material';
import { DeleteImage } from '../components/DeleteImage';
import { useSelector } from 'react-redux';
import { FilterSelector } from '../components/FilterSelector';
import { IFilter, IFilterPayload } from '../../utils/types/Filter';
import { AlertSnackbar } from '../components/AlertSnackbar';
import PhotoDetails from '../components/PhotoDetails';
import { MovePhoto } from '../components/MovePhoto';
import { Diaporama } from '../components/Diaporama';

export const AdvancedResearchPage = (): JSX.Element => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [refresh, setRefresh] = useState(false);
  const [data, setData] = useState<ITable[]>([]);
  const [totalSize, setTotalSize] = useState<number>(0);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [pageSize, setPageSize] = useState<number>(5);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [filterList, setFilterList] = useState<IFilterPayload[]>();

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
    PhotoService.getData(
      pageSize,
      page + 1,
      handleSuccess,
      handleError,
      filterList
    );
  }, [refresh, page, pageSize, filterList]);

  const getFilterPayload = (filterList: IFilter[]): void => {
    if (
      filterList.length === 0 ||
      filterList.some((filterState) =>
        Object.values(filterState).some(
          (value) =>
            value === undefined ||
            value === '' ||
            (Array.isArray(value) && value.length === 0)
        )
      )
    ) {
      handleError('Au moins un filtre possède un champ vide.');
    } else {
      const filterPayload = filterList as IFilterPayload[];
      setFilterList(deserializeArray(filterPayload));
    }
  };

  const deserializeArray = (
    filterPayload: IFilterPayload[]
  ): IFilterPayload[] => {
    filterPayload.forEach((filter) => {
      delete filter.id;
      if (Array.isArray(filter.value)) {
        const oldTagsArray = filter.value;
        filter.value = filter.value[0].name;
        for (let i = 1; i < oldTagsArray.length; i++) {
          const newFilter = {
            field: filter.field,
            operator: null,
            value: oldTagsArray[i].name
          };
          filterPayload.push(newFilter);
        }
      }
    });
    return filterPayload;
  };

  const handleFilterPhoto = (filterList: IFilter[]) => {
    const filters: IFilter[] = JSON.parse(JSON.stringify(filterList));
    getFilterPayload(filters);
  };

  return (
    <>
      <FilterSelector onFilterPhoto={handleFilterPhoto} onError={handleError} />
      {filterList?.length && (
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
            refresh={refresh}
            page={page}
            pageSize={pageSize}
            setPage={setPage}
            setPageSize={setPageSize}
            totalSize={totalSize}
            loading={loading}
          />
        </>
      )}
      <AlertSnackbar
        open={!!errorMessage}
        severity={'warning'}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </>
  );
};
