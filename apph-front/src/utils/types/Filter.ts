import { ITag } from './Tag';

export interface IFilter {
  id: number;
  field?: string;
  operator?: string;
  value?: string | Date | ITag[];
}
