import * as React from 'react';
import { useEffect, useState } from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { UploadImage } from './UploadImage';
import { Alert, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { ITable } from '../../utils/types/table';
import PhotoService from '../../services/PhotoService';
import PhotoDetails from './PhotoDetails';

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
    headerAlign: 'center',
    renderCell: (params) => params.row.creationDate.toLocaleString()
  },
  {
    field: 'shootingDate',
    headerName: 'Shooting date',
    type: 'date',
    flex: 2.2,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => params.row.shootingDate.toLocaleString()
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
    headerAlign: 'center',
    renderCell: (params) =>
      params.row.tags.map((tag: string, index: number) => {
        if (index !== params.row.tags.length - 1) {
          return tag + ', ';
        }
        return tag;
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
    flex: 1,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => params.row.details
  }
];
export default function DataTable() {
  const [data, setData] = useState<ITable[]>(new Array<ITable>());
  const [errorMessage, setErrorMessage] = useState('');
  useEffect(() => {
    PhotoService.getData(
      (tab) => {
        tab.forEach(
          (row) =>
            (row.details = (
              <PhotoDetails
                photoSrc={
                  'https://i.pinimg.com/originals/a2/39/b5/a239b5b33d145fcab7e48544b81019da.jpg'
                }
                title={row.title}
                description={row.description}
                creationDate={row.creationDate}
                shootingDate={row.shootingDate}
                size={row.size}
                tags={row.tags}
              />
            ))
        );
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
}
