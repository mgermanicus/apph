import { UploadImage } from '../components/UploadImage';
import * as React from 'react';
import { PhotoTable } from '../components/PhotoTable';

export const MyPhotoPage = (): JSX.Element => {
  return (
    <>
      <UploadImage />
      <PhotoTable />
    </>
  );
};
