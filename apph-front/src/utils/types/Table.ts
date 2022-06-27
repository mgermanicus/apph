import { ITag } from './Tag';
import { Dispatch, SetStateAction } from 'react';
import { AlertColor } from '@mui/material';
import { ILocation } from './Location';

export interface ITable {
  id: number;
  size: number;
  creationDate: Date;
  modificationDate: Date;
  shootingDate: Date;
  description: string;
  title: string;
  url: string;
  location: ILocation;
  tags: ITag[];
  format: string;
  details?: JSX.Element;
}

export interface ITableDetails {
  photoId: number;
  photoSrc: string;
  title: string;
  description: string;
  creationDate: Date;
  modificationDate: Date;
  shootingDate: Date;
  size: number;
  location: ILocation;
  tags: ITag[];
  format: string;
  fromFolders?: boolean;
  setRefresh?: Dispatch<SetStateAction<boolean>>;
  setSnackbarOpen?: Dispatch<SetStateAction<boolean>>;
  setSnackMessage?: Dispatch<SetStateAction<string>>;
  setSnackSeverity?: Dispatch<SetStateAction<AlertColor>>;
}

export interface IUserTable {
  id: string;
  firstname: string;
  lastname: string;
  email: string;
}
