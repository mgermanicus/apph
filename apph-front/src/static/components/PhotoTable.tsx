import * as React from 'react';
import { Dispatch, useEffect, useState } from 'react';
import {
  DataGrid,
  GridColDef,
  GridSelectionModel,
  GridSortModel
} from '@mui/x-data-grid';
import { Stack } from '@mui/material';
import { ITable, ITag } from '../../utils';
import { DownloadImage } from './DownloadImage';
import { useTranslation } from 'react-i18next';
import parseDate from '../../utils/DateUtils';

interface photoTableProps {
  data: ITable[];
  loading: boolean;
  totalSize: number;
  clearData: boolean;
  page: number;
  setPage: Dispatch<React.SetStateAction<number>>;
  pageSize: number;
  setPageSize: Dispatch<React.SetStateAction<number>>;
  setSelectedIds: Dispatch<React.SetStateAction<number[]>>;
  handleSortModelChange: (model: GridSortModel) => void;
}

export const PhotoTable = ({
  data,
  loading = false,
  clearData = false,
  totalSize = 0,
  page = 0,
  setPage,
  pageSize = 5,
  setPageSize,
  setSelectedIds,
  handleSortModelChange
}: photoTableProps) => {
  const [selectionModel, setSelectionModel] = useState<GridSelectionModel>([]);
  const { t } = useTranslation();

  const columns: GridColDef[] = [
    {
      field: 'title',
      headerName: t('photoTable.title'),
      flex: 1,
      align: 'center',
      headerAlign: 'center'
    },
    {
      field: 'description',
      headerName: t('photoTable.description'),
      flex: 1.7,
      align: 'center',
      headerAlign: 'center'
    },
    {
      field: 'creationDate',
      headerName: t('photoTable.creationDate'),
      type: 'date',
      flex: 2.2,
      align: 'center',
      headerAlign: 'center',
      renderCell: (params) => parseDate(params.row.creationDate)
    },
    {
      field: 'shootingDate',
      headerName: t('photoTable.shootingDate'),
      type: 'date',
      flex: 2.2,
      align: 'center',
      headerAlign: 'center',
      renderCell: (params) => parseDate(params.row.shootingDate)
    },
    {
      field: 'size',
      headerName: t('photoTable.size') + '(' + t('photoTable.kb') + ')',
      type: 'number',
      flex: 1,
      align: 'center',
      headerAlign: 'center'
    },
    {
      field: 'tags',
      headerName: t('photoTable.tags'),
      flex: 1.5,
      align: 'center',
      headerAlign: 'center',
      sortable: false,
      renderCell: (params) =>
        params.row.tags.map((tag: ITag, index: number) => {
          if (index !== params.row.tags.length - 1) {
            return tag.name + ', ';
          }
          return tag.name;
        })
    },
    {
      field: 'url',
      headerName: t('photoTable.url'),
      flex: 1,
      align: 'center',
      headerAlign: 'center',
      sortable: false
    },
    {
      field: 'actions',
      headerName: t('photoTable.actions'),
      flex: 2,
      align: 'center',
      headerAlign: 'center',
      sortable: false,
      renderCell: (params) => (
        <Stack spacing={2} direction="row">
          {params.row.details} <DownloadImage id={+params.id} />
        </Stack>
      )
    }
  ];

  useEffect(() => {
    setSelectionModel([]);
    setSelectedIds([]);
  }, [clearData]);

  return (
    <div style={{ height: 115 + pageSize * 52, width: '100%' }}>
      <DataGrid
        keepNonExistentRowsSelected
        pagination
        paginationMode="server"
        page={page}
        loading={loading}
        rows={data}
        rowCount={totalSize}
        columns={columns}
        pageSize={pageSize}
        rowsPerPageOptions={[5, 10, 20]}
        onPageChange={(pageIndex) => setPage(pageIndex)}
        onPageSizeChange={(size) => {
          const newPage = Math.trunc(pageSize / size) * page;
          setPageSize(size);
          setPage(newPage);
        }}
        columnBuffer={9}
        onSelectionModelChange={(ids: GridSelectionModel) => {
          setSelectionModel(ids);
          setSelectedIds(ids as number[]);
        }}
        selectionModel={selectionModel}
        checkboxSelection
        disableSelectionOnClick
        sortingMode="server"
        onSortModelChange={handleSortModelChange}
        disableColumnMenu
      />
    </div>
  );
};
