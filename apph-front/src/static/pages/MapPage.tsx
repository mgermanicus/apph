import { Box } from '@mui/material';
import { PhotosMap } from '../components/PhotosMap';

export const MapPage = () => {
  return (
    <Box component="div" sx={{ height: '91vh', overflow: 'hidden' }}>
      <PhotosMap locations={[]} />
    </Box>
  );
};
