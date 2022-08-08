import React from 'react';
import { Map, Marker, Overlay } from 'pigeon-maps';
import { Box } from '@mui/material';
import { IMarker } from '../../utils/types/Location';

// https://pigeon-maps.js.org/docs/marker
export const PhotosMap = ({ markers }: { markers: IMarker[][] }) => (
  <Box component="div" sx={{ height: '100%' }}>
    <Map
      defaultCenter={
        markers.length
          ? [markers[0][0].lat, markers[0][0].lng]
          : [48.866667, 2.333333]
      }
      defaultZoom={3}
    >
      {markers?.map((group) => (
        <Marker
          key={group[0].id}
          width={50}
          anchor={[group[0].lat, group[0].lng]}
        />
      ))}
      {markers?.map((group) => (
        <Overlay anchor={[group[0].lat, group[0].lng]} offset={[4.0, 41.5]}>
          {group.length}
        </Overlay>
      ))}
    </Map>
  </Box>
);
