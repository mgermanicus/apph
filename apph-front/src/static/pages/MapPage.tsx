import { Box } from '@mui/material';
import { PhotosMap } from '../components/PhotosMap';

export const MapPage = () => {
  return (
    <Box>
      <PhotosMap locations={[]} />
    </Box>
  );
};
