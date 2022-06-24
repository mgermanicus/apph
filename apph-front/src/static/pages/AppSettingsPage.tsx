import SettingService from '../../services/SettingService';
import { FormEvent, useEffect, useState } from 'react';
import { cardStyle, ISetting } from '../../utils';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  InputAdornment,
  Stack,
  TextField
} from '@mui/material';
import { ConfirmationDialog } from '../components/ConfirmationDialog';
import { useTranslation } from 'react-i18next';

export const AppSettingsPage = () => {
  const { t } = useTranslation();
  const [uploadSize, setUploadSize] = useState<number>(0);
  const [downloadSize, setDownloadSize] = useState<number>(0);
  const [message, setMessage] = useState<string>('');
  const [errorOccured, setErrorOccured] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    (async () => {
      await SettingService.getSettings(
        (data) => {
          const settings: ISetting = JSON.parse(data);
          setUploadSize(settings.uploadSize);
          setDownloadSize(settings.downloadSize);
        },
        () => {
          setErrorOccured(true);
          setMessage('admin.setting.error.errorGet');
        }
      );
    })();
  }, []);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (uploadSize > 0 && downloadSize > 0) {
      setDialogOpen(true);
    } else {
      setMessage('admin.setting.error.valueNotValid');
    }
  };

  const updateParameters = async () =>
    SettingService.updateSettings(
      uploadSize,
      downloadSize,
      () => setMessage('admin.setting.success.modifySuccess'),
      () => {
        setErrorOccured(true);
        setMessage('admin.setting.error.modifyFailed');
      }
    );

  return (
    <Card sx={cardStyle}>
      <CardHeader title={t('admin.setting.edit')} />
      <CardContent>
        <Box component="form" onSubmit={handleSubmit}>
          <Stack spacing={2}>
            <TextField
              required
              label={t('admin.setting.uploadSize')}
              value={uploadSize}
              InputProps={{
                endAdornment: <InputAdornment position="end">MB</InputAdornment>
              }}
              onChange={(e) =>
                setUploadSize(
                  isNaN(parseInt(e.target.value)) ? 0 : parseInt(e.target.value)
                )
              }
            />
            <TextField
              required
              label={t('admin.setting.downloadSize')}
              value={downloadSize}
              InputProps={{
                endAdornment: <InputAdornment position="end">MB</InputAdornment>
              }}
              onChange={(e) =>
                setDownloadSize(
                  isNaN(parseInt(e.target.value)) ? 0 : parseInt(e.target.value)
                )
              }
            />
            <Button type="submit">{t('action.confirm')}</Button>

            {!!message && (
              <Alert severity={errorOccured ? 'error' : 'success'} id="alert">
                {t(message)}
              </Alert>
            )}
          </Stack>
        </Box>
        <ConfirmationDialog
          open={dialogOpen}
          onConfirm={async () => {
            setDialogOpen(false);
            await updateParameters();
          }}
          onCancel={() => {
            setDialogOpen(false);
          }}
          message={t('admin.setting.warningUpdate')}
        />
      </CardContent>
    </Card>
  );
};
