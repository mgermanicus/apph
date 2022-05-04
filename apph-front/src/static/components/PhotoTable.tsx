import * as React from 'react';
import { useEffect, useState } from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { UploadImage } from './UploadImage';
import { Alert, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { ITable } from '../../utils/types/table';
import PhotoService from '../../services/PhotoService';
import { DownloadImage } from './DownloadImage';

const columns: GridColDef[] = [
  {
    field: 'title',
    headerName: 'Title',
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
    headerName: 'Creation date',
    type: 'date',
    flex: 2.2,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'shootingDate',
    headerName: 'Shooting date',
    type: 'date',
    flex: 2.2,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'size',
    headerName: 'Size',
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
    headerAlign: 'center'
  },
  {
    field: 'url',
    headerName: 'Url',
    flex: 1,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'action',
    headerName: 'Action',
    flex: 2,
    align: 'center',
    renderCell: (params) => DownloadImage(+params.id),
    headerAlign: 'center'
  }
];
export const DataTable = () => {
  const [data, setData] = useState<ITable[]>(new Array<ITable>());
  const [errorMessage, setErrorMessage] = useState('');
  useEffect(() => {
    PhotoService.getData(
      (tab) => {
        setData(tab);
      },
      (errorMessage: string) => {
        setErrorMessage(errorMessage);
      }
    );
  }, []);
  return (
    <div style={{ height: 400, width: '100%' }}>
      <UploadImage />
      <DataGrid
        rows={data}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5]}
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
