import { ITag } from './Tag';

export interface IFilter {
  id: number;
  field?: string;
  operator?: string | null;
  value?: string | Date | ITag[];
}

export interface IFilterPayload {
  id?: number;
  field: string;
  operator: string | null;
  value: string | Date | ITag;
}
