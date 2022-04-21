export default function verifyFormat(imageFile: File) {
  if (!/image\/.*/.test(imageFile.type))
    throw Error("Le format du fichier n'est pas valide");
}
