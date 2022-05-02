import * as React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import TableService from '../../services/TableService';
import { UploadImage } from './UploadImage';
import { Alert, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { ITable } from '../../utils/types/table';

const columns: GridColDef[] = [
  {
    field: 'title',
    headerName: 'Title',
    width: 70,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'description',
    headerName: 'Description',
    width: 130,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'creationDate',
    headerName: 'Creation date',
    type: 'date',
    width: 250,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'shootingDate',
    headerName: 'Shooting date',
    type: 'date',
    width: 250,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'size',
    headerName: 'Size',
    type: 'number',
    width: 130,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'tags',
    headerName: 'Tags',
    width: 130,
    align: 'center',
    headerAlign: 'center'
  },
  {
    field: 'url',
    headerName: 'Url',
    width: 130,
    align: 'center',
    headerAlign: 'center'
  }
];
export default function DataTable() {
  const [data, setData] = useState<ITable[]>(new Array<ITable>());
  const [errorMessage, setErrorMessage] = useState('');
  useEffect(() => {
    TableService.getData(
      setData,
      () => {
        console.log('Affichage du tableau');
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
}
