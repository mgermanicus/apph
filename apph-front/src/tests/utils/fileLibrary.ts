import { createEvent, fireEvent } from '@testing-library/react';
import { ITag } from '../../utils';

export function inputFile(file: File, input: HTMLInputElement) {
  fireEvent(
    input,
    createEvent('input', input, {
      target: { files: [file] }
    })
  );
}

export function fakeFile(size: number, type: string) {
  const file = new File([''], 'big_image.png', { type });
  Object.defineProperty(file, 'size', { value: size });
  return file;
}

export function fakeUploadRequestParams(
  file: File,
  title: string,
  description: string,
  shootingDate: Date,
  selectedTags: ITag[]
) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tags', JSON.stringify(selectedTags));
  formData.append('title', title);
  formData.append('description', description);
  formData.append(
    'shootingDate',
    JSON.stringify(shootingDate.toLocaleString())
  );
  const requestOptions = {
    method: 'POST',
    body: formData
  };
  return { URL: `/photo/upload`, requestOptions };
}

export function fakeDownloadRequestParams(id: number) {
  const requestOptions = {
    method: 'POST',
    body: JSON.stringify({
      id
    })
  };
  return { URL: `/photo/download`, requestOptions };
}

export function fakeDeleteRequestParams(ids: number[]) {
  const requestOptions = {
    method: 'DELETE',
    body: JSON.stringify({
      ids
    })
  };
  return { URL: `/photo/delete`, requestOptions };
}
