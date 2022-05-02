import { Button } from '@mui/material';
import ImageService from '../../services/ImageService';
import { IPhoto } from '../../utils/types/Photo';

export const DownloadImage = (): JSX.Element => {
  const handleSubmit = () => {
    ImageService.downloadImage(
      1,
      (photo: IPhoto) => {
        const imageBase64 = `data:image/${photo.extension};base64,${photo.data}`;
        const a = document.createElement('a');
        const event = new MouseEvent('click');
        a.href = imageBase64;
        a.download = photo.name + '.' + photo.extension;
        a.dispatchEvent(event);
      },
      (errorMessage) => {
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
