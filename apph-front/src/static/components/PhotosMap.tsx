import React from 'react';
import { Map, Marker } from 'pigeon-maps';
import { Box } from '@mui/material';
import { IPosition } from '../../utils/types/Location';

// https://pigeon-maps.js.org/docs/marker
export const PhotosMap = ({ locations }: { locations: IPosition[] }) => (
  <Box component="div">
    <Map
      height={723}
      defaultCenter={
        locations.length
          ? [locations[0].lat, locations[0].lng]
          : [48.866667, 2.333333]
      }
      defaultZoom={3}
    >
      {locations?.map((location) => (
        <Marker width={50} anchor={[location.lat, location.lng]} />
      ))}
    </Map>
  </Box>
);
