import React from 'react';
import { Map, Marker } from 'pigeon-maps';
import { Box } from '@mui/material';
import { IMarker } from '../../utils/types/Location';

// https://pigeon-maps.js.org/docs/marker
export const PhotosMap = ({ markers }: { markers: IMarker[] }) => (
  <Box component="div" sx={{ height: '100%' }}>
    <Map
      defaultCenter={
        markers.length
          ? [markers[0].lat, markers[0].lng]
          : [48.866667, 2.333333]
      }
      defaultZoom={3}
    >
      {markers?.map((marker) => (
        <Marker key={marker.id} width={50} anchor={[marker.lat, marker.lng]} />
      ))}
    </Map>
  </Box>
);
