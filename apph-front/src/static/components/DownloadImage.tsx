import { Button } from '@mui/material';
import { IPhoto } from '../../utils/types/Photo';
import PhotoService from '../../services/PhotoService';

export const DownloadImage = (): JSX.Element => {
  const handleSubmit = () => {
      PhotoService.downloadImage(
        1,
        (photo: IPhoto) => {
          const imageBase64 = `data:image/${photo.extension};base64,${photo.data}`;
          const a = document.createElement('a');
          const event = new MouseEvent('click');
          a.href = imageBase64;
          a.download = photo.title + '.' + photo.extension;
          a.dispatchEvent(event);
        },
        (errorMessage: any) => {
          console.log(errorMessage);
        }
      );
  };

  return (
    <Button variant="outlined" onClick={handleSubmit}>
      Télécharger
    </Button>
  );
};
