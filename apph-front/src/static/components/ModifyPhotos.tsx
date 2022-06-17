import { Box, Button, Tooltip } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import React from 'react';

export const ModifyPhotos = () => {
  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={'Modifier'}>
        <Button
          variant="outlined"
          onClick={() => console.log('click !')}
          aria-label="upload-photo"
        >
          <EditIcon />
        </Button>
      </Tooltip>
    </Box>
  );
};
