import { Box } from '@mui/material';
import { PhotosMap } from '../components/PhotosMap';
import { IMarker } from '../../utils/types/Location';
import React, { useEffect, useState } from 'react';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from '../components/AlertSnackbar';

export const MapPage = () => {
  const [groupedMarkers, setGroupedMarkers] = useState<IMarker[][]>();
  const [error, setError] = useState('');
  const groupDuplicates = (markers: IMarker[]) => {
    const groupedMarkers: IMarker[][] = [];
    markers?.forEach((marker) => {
      if (groupedMarkers.some((group) => group.includes(marker))) {
        return;
      }
      const duplicates = markers.filter(
        (otherMarker) =>
          otherMarker.lat === marker.lat && otherMarker.lng === marker.lng
      );
      groupedMarkers.push(duplicates);
    });
    return groupedMarkers;
  };

  useEffect(() => {
    PhotoService.getMarkers(
      (markers) => setGroupedMarkers(groupDuplicates(markers)),
      setError
    );
  }, []);

  return (
    <Box component="div" sx={{ height: '91vh', overflow: 'hidden' }}>
      <PhotosMap markers={groupedMarkers ?? []} />
      <AlertSnackbar
        open={!!error}
        severity={'error'}
        message={error}
        onClose={() => setError('')}
      />
    </Box>
  );
};
