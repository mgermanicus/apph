import * as React from 'react';
import { DataGrid, GridColDef, GridValueGetterParams } from '@mui/x-data-grid';
import { useEffect, useState } from 'react';

const columns: GridColDef[] = [
  { field: 'title', headerName: 'Title', width: 70 },
  { field: 'description', headerName: 'Description', width: 130 ,sortable: false},
  {
    field: 'creationDate',
    headerName: 'Creation date',
    type: 'date',
    width: 130
  },
  {
    field: 'shootingDate',
    headerName: 'Shooting date',
    type: 'date',
    width: 130
  },
  { field: 'size', headerName: 'Size', type: 'number', width: 130 },
  { field: 'tags', headerName: 'Tags', width: 130 }
];



export default function DataTable() {
  const [data, setData] = useState([]);
  useEffect(() => {
    fetch('http://localhost:8080/photo/1')
      .then((response) => response.json())
      .then((val) => {
        setData(val);
      });
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
