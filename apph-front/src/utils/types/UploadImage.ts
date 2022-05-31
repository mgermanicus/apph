export enum StatusType {
  None,
  Success,
  Error,
  Uploading
}

export type UploadStatus = { type: StatusType; message?: string };
