export const imageFileCheck = (
  imageFile: File,
  handleError: (errorMessage: string) => void,
  maxFileSizeMb = 10
) => {
  if (!/image\/.*/.test(imageFile.type)) {
    handleError('upload.error.wrongFormat');
    return false;
  }
  if (imageFile.size > maxFileSizeMb * 1000000) {
    handleError(`upload.error.overSize`);
    return false;
  }
  return true;
};
