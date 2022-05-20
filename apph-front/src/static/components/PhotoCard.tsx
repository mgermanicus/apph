import {
  Card,
  CardActionArea,
  CardContent,
  CardMedia,
  Typography
} from '@mui/material';
import * as React from 'react';

export const PhotoCard = ({
  src,
  title,
  openFunction
}: {
  src: string;
  title: string;
  openFunction: () => void;
}): JSX.Element => {
  return (
    <Card sx={{ maxWidth: 210 }}>
      <CardActionArea onClick={openFunction}>
        <CardMedia image={src} sx={{ height: 150, objectFit: 'scale-down' }} />
        <CardContent>
          <Typography noWrap>{title}</Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};
