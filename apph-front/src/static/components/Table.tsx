import * as React from 'react';
import { DataGrid, GridColDef, GridValueGetterParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';
import TableService from '../../services/TableService';

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
  const [data, setData] = useState([]);
  useEffect(() => {
    TableService.getData(
      setData,
      () => {},
      () => {}
    );
  }, []);
  return (
    <div style={{ height: 400, width: '100%' }}>
      <DataGrid
        rows={data}
        columns={columns}
        pageSize={5}
        rowsPerPageOptions={[5]}
      />
    </div>
  );
}
