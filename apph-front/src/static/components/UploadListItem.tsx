import { StatusType, UploadStatus } from '../../utils';
import DoneIcon from '@mui/icons-material/Done';
import { Box, LinearProgress, Typography } from '@mui/material';

export const UploadListItem = (props: { file: File; status: UploadStatus }) => {
  const uploadBody = () => {
    switch (props.status.type) {
      case StatusType.Success:
        return <DoneIcon color={'success'} />;
      case StatusType.Error:
        return (
          <Typography color="error" variant="caption">
            {props.status.message}
          </Typography>
        );
      case StatusType.Uploading:
        return (
          <Box sx={{ width: '100%' }}>
            <LinearProgress />
          </Box>
        );
      default:
        return <></>;
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}
    >
      <Box sx={{ mr: 2 }}>
        <Typography color="text.secondary">{props.file.name}</Typography>
      </Box>
      {uploadBody()}
    </Box>
  );
};
