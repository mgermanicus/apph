import * as React from 'react';
import { useEffect, useState } from 'react';
import { DataGrid, GridColDef, GridSelectionModel } from '@mui/x-data-grid';
import { Alert, Collapse, IconButton, Stack } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { IPagination, ITable, ITag } from '../../utils';
import PhotoDetails from './PhotoDetails';
import { DownloadImage } from './DownloadImage';
import { useDispatch } from 'react-redux';
import { replaceSelectedPhotos } from '../../redux/slices/photoSlice';

const columns: GridColDef[] = [
  {
    field: 'title',
    headerName: 'Titre',
    flex: 1,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'description',
    headerName: 'Description',
    flex: 1.7,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'creationDate',
    headerName: 'Date de création',
    type: 'date',
    flex: 2.2,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => params.row.creationDate?.toLocaleString()
  },
  {
    field: 'shootingDate',
    headerName: 'Date de prise de vue',
    type: 'date',
    flex: 2.2,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => params.row.shootingDate?.toLocaleString()
  },
  {
    field: 'size',
    headerName: 'Taille(Ko)',
    type: 'number',
    flex: 1,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'tags',
    headerName: 'Tags',
    flex: 1.5,
    align: 'center',
    headerAlign: 'center',
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
    headerName: 'Url',
    flex: 1,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'actions',
    headerName: 'Actions',
    flex: 2,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => (
      <Stack spacing={2} direction="row">
        {params.row.details} <DownloadImage id={+params.id} />
      </Stack>
    )
  }
];

interface photoTableProps {
  getPhotos: (
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) => void;
  refresh?: boolean;
}

export const PhotoTable = ({ getPhotos, refresh = false }: photoTableProps) => {
  const [data, setData] = useState<ITable[]>(new Array<ITable>());
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [pageSize, setPageSize] = useState<number>(5);
  const [page, setPage] = useState<number>(0);
  const [totalSize, setTotalSize] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [selectionModel, setSelectionModel] = useState<GridSelectionModel>([]);

  const dispatch = useDispatch();

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

  const handleError = (error: string) => {
    setErrorMessage(error);
    setLoading(false);
  };

  useEffect(() => {
    getPhotos(pageSize, page + 1, handleSuccess, handleError);
  }, [page, pageSize, refresh]);

  return (
    <div style={{ height: 115 + pageSize * 52, width: '100%' }}>
      <DataGrid
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
        onSelectionModelChange={(ids) => {
          setSelectionModel(ids);
          const selectedIDs = new Set(ids);
          const selectedRowData = data.filter((rows) =>
            selectedIDs.has(rows.id)
          );
          dispatch(replaceSelectedPhotos(JSON.stringify(selectedRowData)));
        }}
        selectionModel={selectionModel}
        checkboxSelection
        disableSelectionOnClick
      />
      <Collapse in={errorMessage !== ''}>
        <Alert
          action={
            <IconButton
              aria-label="close"
              color="inherit"
              size="small"
              onClick={() => {
                setErrorMessage('');
              }}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          }
          sx={{ mb: 2 }}
          severity="error"
        >
          {errorMessage}
        </Alert>
      </Collapse>
    </div>
  );
};
