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
  openFunction,
  cardStyle
}: {
  src: string;
  title: string;
  openFunction: () => void;
  cardStyle?: {
    cardMaxWidth: string;
    cardMediaHeight: string;
  };
}): JSX.Element => {
  return (
    <Card sx={{ maxWidth: cardStyle?.cardMaxWidth }}>
      <CardActionArea onClick={openFunction}>
        <CardMedia
          image={src}
          sx={{ height: cardStyle?.cardMediaHeight, objectFit: 'scale-down' }}
        />
        <CardContent>
          <Typography noWrap>{title}</Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};
