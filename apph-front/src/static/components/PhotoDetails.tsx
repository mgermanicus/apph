import { ButtonGroup, Chip, Modal, Stack, Tooltip } from '@mui/material';
import Button from '@mui/material/Button';
import * as React from 'react';
import { useState } from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { IMessage, ITableDetails, randomHSL } from '../../utils';
import { PhotoCard } from './PhotoCard';
import { Info } from '@mui/icons-material';
import { ReUploadPhoto } from './ReUploadPhoto';
import { EditPhotoDetails } from './EditPhotoDetails';
import PhotoService from '../../services/PhotoService';
import { ConfirmationDialog } from './ConfirmationDialog';
import { useTranslation } from 'react-i18next';
import parseDate from '../../utils/DateUtils';

const modalStyle = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 0.6,
  maxHeight: 0.7,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4,
  overflow: 'scroll'
};

const detailBoxStyle = { mt: 3, display: 'flex' };

const titleTypoStyle = { fontWeight: 'bold', pl: 15 };

const detailTypoStyle = { ml: 1 };

export const PhotoDetails = ({
  details,
  updateData,
  refresh,
  clickType,
  cardStyle
}: {
  details: ITableDetails;
  updateData: () => void;
  refresh: () => void;
  clickType: string;
  cardStyle?: {
    cardMaxWidth: string;
    cardMediaHeight: string;
  };
}) => {
  const [detailsOpen, setDetailsOpen] = useState<boolean>(false);
  const { t } = useTranslation();
  const [dialogOpen, setDialogOpen] = useState(false);

  const handleOpenDetails = () => {
    setDetailsOpen(true);
  };

  const handleCloseDetails = () => {
    setDetailsOpen(false);
  };
  const handleUpdate = async () => {
    await PhotoService.updatePhotoInfo(
      details.photoId,
      details.title,
      details.description,
      -1,
      (message: { message: string }) => {
        if (details.setSnackMessage && details.setSnackSeverity) {
          details.setSnackMessage(message.message);
          details.setSnackSeverity('success');
        }
      },
      (error: IMessage) => {
        if (details.setSnackMessage && details.setSnackSeverity) {
          details.setSnackMessage(error.message);
          details.setSnackSeverity('error');
        }
      }
    );
    setDetailsOpen(false);
    if (details.setSnackbarOpen) {
      details.setSnackbarOpen(true);
    }
    if (details.setRefresh) {
      details.setRefresh((refresh) => !refresh);
    }
  };

  return (
    <>
      {
        {
          button: (
            <Tooltip title={t('photo.details')}>
              <Button
                variant="outlined"
                onClick={handleOpenDetails}
                aria-label="photo-detail"
              >
                <Info />
              </Button>
            </Tooltip>
          ),
          card: (
            <PhotoCard
              src={details.photoSrc}
              title={details.title + details.format}
              openFunction={handleOpenDetails}
              cardStyle={cardStyle}
            />
          )
        }[clickType]
      }

      <Modal
        open={detailsOpen}
        onClose={handleCloseDetails}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={modalStyle}>
          <Typography
            id="modal-modal-title"
            component="h1"
            align="center"
            sx={{ fontSize: '2.5rem' }}
          >
            {details.title}
          </Typography>
          <Typography
            id="modal-modal-description"
            sx={{ mt: 2 }}
            align="center"
            component={'div'}
          >
            <Box
              component={'img'}
              sx={{ width: 0.7 }}
              src={`${details.photoSrc}?${global.Date.now()}`}
            />
            <ButtonGroup
              variant="outlined"
              sx={{ m: 1, display: 'flex', justifyContent: 'end' }}
            >
              <ReUploadPhoto
                photoId={details.photoId}
                updateData={updateData}
              />
            </ButtonGroup>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'flex-start'
              }}
            >
              {details.fromFolders && (
                <Button
                  name="Supprimer du dossier"
                  variant="outlined"
                  sx={{ alignSelf: 'center' }}
                  onClick={() => setDialogOpen(true)}
                >
                  {t('folder.delete')}
                </Button>
              )}
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.title')}:
                </Typography>
                <Typography sx={detailTypoStyle}>{details.title}</Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.description')}:
                </Typography>
                <Typography sx={detailTypoStyle}>
                  {details.description}
                </Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.creationDate')}:
                </Typography>
                <Typography sx={detailTypoStyle}>
                  {parseDate(details.creationDate?.toLocaleString())}
                </Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.lastModification')}:
                </Typography>
                <Typography sx={detailTypoStyle}>
                  {parseDate(details.modificationDate?.toLocaleString())}
                </Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.shootingDate')}:
                </Typography>
                <Typography sx={detailTypoStyle}>
                  {parseDate(details.shootingDate?.toLocaleString())}
                </Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Typography sx={titleTypoStyle}>
                  {t('photoTable.size')}:
                </Typography>
                <Typography sx={detailTypoStyle}>{details.size} Ko</Typography>
              </Box>
              <Box sx={detailBoxStyle}>
                <Stack direction="row" spacing={1}>
                  <Box sx={{ marginTop: 0.5 }}>
                    <Typography sx={titleTypoStyle}>
                      {t('photoTable.tags')}:
                    </Typography>
                  </Box>
                  {details.tags.map((tag, index) => (
                    <Chip
                      key={index}
                      label={tag.name}
                      sx={{
                        backgroundColor: randomHSL(tag.name)
                      }}
                    />
                  ))}
                </Stack>
              </Box>
              <Box sx={{ pl: 15, mt: 2 }}>
                <EditPhotoDetails
                  id={details.photoId}
                  title={details.title}
                  description={details.description}
                  shootingDate={details.shootingDate}
                  tags={details.tags}
                  onEdit={refresh}
                />
              </Box>
            </Box>
          </Typography>
          <ConfirmationDialog
            open={dialogOpen}
            onConfirm={handleUpdate}
            onCancel={() => {
              setDialogOpen(false);
            }}
            title={t('action.confirmDelete')}
            message={t('folder.confirmDeletePhoto')}
          />
        </Box>
      </Modal>
    </>
  );
};

export default PhotoDetails;
