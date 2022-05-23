import { ITag } from './Tag';

export interface ITable {
  id: number;
  size: number;
  creationDate: Date;
  shootingDate: Date;
  description: string;
  title: string;
  url: string;
  tags: ITag[];
  format: string;
  details?: JSX.Element;
}

export interface IPhotoDetails {
  photoSrc: string;
  title: string;
  description: string;
  creationDate: Date;
  shootingDate: Date;
  size: number;
  tags: ITag[];
  format: string;
  clickType: string;
}
