export default function imageFileCheck(
  imageFile: File,
  handleError: (errorMessage: string) => void,
  maxFileSizeMb = 10
) {
  if (!/image\/.*/.test(imageFile.type)) {
    handleError("Le format du fichier n'est pas valide");
    return false;
  }
  if (imageFile.size > maxFileSizeMb * 1000000) {
    handleError(
      `La taille du fichier excède la limite maximale (${maxFileSizeMb} MB)`
    );
    return false;
  }
  return true;
}
