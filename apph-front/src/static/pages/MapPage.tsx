import { Box } from '@mui/material';
import { PhotosMap } from '../components/PhotosMap';
import { IMarker } from '../../utils/types/Location';
import { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';

export const MapPage = () => {
  const [markers, setMarkers] = useState<IMarker[]>();

  useEffect(() => {
    PhotoService.getMarkers(setMarkers, (error) => {
      console.log(error);
    });
  }, []);

  return (
    <Box component="div" sx={{ height: '91vh', overflow: 'hidden' }}>
      <PhotosMap markers={markers ?? []} />
    </Box>
  );
};
