import * as React from 'react';
import { useEffect, useState } from 'react';
import { IUserTable } from '../types';
import { UserTable } from '../../static/components/UserTable';
import UserService from '../../services/UserService';

export const useUserTable = () => {
  const [data, setData] = useState<IUserTable[]>([]);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [refresh, setRefresh] = useState<boolean>(false);

  useEffect(() => {
    setLoading(true);
    UserService.getUserList(handleSuccess, handleError);
  }, [refresh]);

  const handleError = (error: string) => {
    setErrorMessage(error);
    setLoading(false);
  };

  const handleSuccess = (list: IUserTable[]) => {
    setData(list);
    setLoading(false);
  };

  const userTable = (
    <UserTable data={data} loading={loading} setRefresh={setRefresh} />
  );

  return {
    errorState: {
      getMessage: errorMessage,
      setMessage: setErrorMessage
    },
    userTable
  };
};
