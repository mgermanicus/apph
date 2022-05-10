import * as React from 'react';
import { useEffect, useState } from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { UploadImage } from './UploadImage';
import { Alert, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { ITable } from '../../utils/types/table';
import PhotoService from '../../services/PhotoService';
import PhotoDetails from './PhotoDetails';
import { DownloadImage } from './DownloadImage';

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
    headerName: 'Taille',
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
    flex: 3,
    align: 'center',
    headerAlign: 'center',
    renderCell: (params) => (
      <>
        {params.row.details} <DownloadImage id={+params.id} />
      </>
    )
  }
];
export const DataTable = () => {
  const [data, setData] = useState<ITable[]>(new Array<ITable>());
  const [errorMessage, setErrorMessage] = useState('');

  const getData = () => {
    PhotoService.getData(
      (tab) => {
        tab.forEach(
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
              />
            ))
        );
        setData(tab);
      },
      (errorMessage: string) => {
        setErrorMessage(errorMessage);
      }
    );
  };

  useEffect(() => {
    getData();
    const timer = setInterval(getData, 3000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div style={{ height: 400, width: '100%' }}>
      <UploadImage />
      <DataGrid
        rows={data}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5]}
        columnBuffer={8}
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
