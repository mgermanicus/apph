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
    <Card sx={{ maxWidth: 200 }}>
      <CardActionArea onClick={openFunction}>
        <CardMedia image={src} sx={{ height: 100, objectFit: 'scale-down' }} />
        <CardContent>
          <Typography>{title}</Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};
