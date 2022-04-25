import { createEvent, fireEvent } from '@testing-library/react';

export function inputFile(file: File, input: HTMLInputElement) {
  fireEvent(
    input,
    createEvent('input', input, {
      target: { files: [file] }
    })
  );
}

export function bigImage(size: number) {
  const file = new File([''], 'big_image.png', { type: 'image/png' });
  Object.defineProperty(file, 'size', { value: size });
  return file;
}
