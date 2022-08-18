import { IUserTable } from '../../utils';
import * as React from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useTranslation } from 'react-i18next';
import { DeleteUser } from './DeleteUser';

interface userTableProps {
  data: IUserTable[];
  setRefresh: React.Dispatch<React.SetStateAction<boolean>>;
  loading: boolean;
}

export const UserTable = ({
  data,
  setRefresh,
  loading = false
}: userTableProps) => {
  const { t } = useTranslation();
  const columns: GridColDef[] = [
    {
      field: 'firstname',
      headerName: t('userTable.firstname'),
      flex: 1,
      align: 'center',
      headerAlign: 'center',
      sortable: false
    },
    {
      field: 'lastname',
      headerName: t('userTable.lastname'),
      flex: 1,
      align: 'center',
      headerAlign: 'center',
      sortable: false
    },
    {
      field: 'email',
      headerName: t('userTable.email'),
      flex: 1,
      align: 'center',
      headerAlign: 'center',
      sortable: false
    },
    {
      field: 'actions',
      headerName: t('userTable.actions'),
      flex: 1,
      align: 'center',
      headerAlign: 'center',
      sortable: false,
      renderCell: (params) => (
        <DeleteUser userId={params.row.id} setRefresh={setRefresh} />
      )
    }
  ];

  return (
    <div style={{ height: '90vh', width: '100%' }}>
      <DataGrid
        loading={loading}
        rows={data}
        columns={columns}
        columnBuffer={3}
        rowsPerPageOptions={[100]}
        disableSelectionOnClick
        disableColumnMenu
      />
    </div>
  );
};
