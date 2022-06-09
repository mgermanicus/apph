import { UploadStatus } from '../../utils';
import { UploadListItem } from './UploadListItem';

export const UploadList = ({
  statuses,
  files
}: {
  statuses: UploadStatus[];
  files: File[] | undefined;
}) => {
  return (
    <>
      {files &&
        statuses.map(
          (status, i) =>
            !!files[i] && (
              <UploadListItem status={status} file={files[i]} key={i} />
            )
        )}
    </>
  );
};
