import { ITag } from './Tag';

export interface ITable {
  id: number;
  size: number;
  creationDate: Date;
  modificationDate: Date;
  shootingDate: Date;
  description: string;
  title: string;
  url: string;
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
  tags: ITag[];
  format: string;
  clickType: string;
  cardStyle?: {
    cardMaxWidth: string;
    cardMediaHeight: string;
  };
  updateData: () => void;
}
