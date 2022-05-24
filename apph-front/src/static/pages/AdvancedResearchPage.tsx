import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { PhotoTable } from '../components/PhotoTable';
import { IPagination } from '../../utils';
import PhotoService from '../../services/PhotoService';
import { UploadImage } from '../components/UploadImage';
import { Box, ButtonGroup } from '@mui/material';
import { DeleteImage } from '../components/DeleteImage';
import { useSelector } from 'react-redux';
import { IPhoto } from '../../utils';
import { FilterSelector } from '../components/FilterSelector';
import { IFilter, IFilterPayload } from '../../utils/types/Filter';
import { AlertSnackbar } from '../components/AlertSnackbar';

export const AdvancedResearchPage = (): JSX.Element => {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [filterList, setFilterList] = useState<IFilterPayload[]>();
  const [isAlert, setIsAlert] = useState<boolean>(false);
  const [alertMessage, setAlertMessage] = useState<string>('');

  const selected = useSelector(
    ({ selectedPhotos }: { selectedPhotos: IPhoto[] }) => selectedPhotos
  );

  useEffect(() => {
    setSelectedIds(selected.map((photo: IPhoto) => +photo['id']));
  }, [selected]);

  useEffect(() => {
    getPhotosRef.current = getPhotos;
  }, [filterList]);

  const openAlert = (message: string): void => {
    setAlertMessage(message);
    setIsAlert(true);
  };

  const getFilterPayload = (filterList: IFilter[]): void => {
    if (
      filterList.length === 0 ||
      filterList.some((filterState) =>
        Object.values(filterState).some(
          (value) => value === undefined || value === ''
        )
      )
    ) {
      openAlert('Au moins un filtre possède un champ vide.');
    } else {
      const filterPayload = filterList as IFilterPayload[];
      filterPayload.forEach((filter) => delete filter.id);
      setFilterList(filterPayload);
    }
  };

  const getPhotos = (
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) => {
    PhotoService.getData(
      pageSize,
      page,
      handleSuccess,
      handleError,
      filterList
    );
  };

  const getPhotosRef =
    useRef<
      (
        pageSize: number,
        page: number,
        handleSuccess: (pagination: IPagination) => void,
        handleError: (errorMessage: string) => void
      ) => void
    >(getPhotos);

  const handleFilterPhoto = (filterList: IFilter[]) => {
    const filters: IFilter[] = JSON.parse(JSON.stringify(filterList));
    getFilterPayload(filters);
  };

  return (
    <>
      <FilterSelector onFilterPhoto={handleFilterPhoto} openAlert={openAlert} />
      {filterList?.length !== 0 && (
        <>
          <ButtonGroup variant="outlined" aria-label="outlined button group">
            <UploadImage />
            <Box sx={{ m: 1 }}>
              <DeleteImage ids={selectedIds} />
            </Box>
          </ButtonGroup>
          <PhotoTable onGetPhotos={getPhotosRef} />
        </>
      )}
      <AlertSnackbar
        open={isAlert}
        severity={'error'}
        message={alertMessage}
        onClose={setIsAlert}
      />
    </>
  );
};
