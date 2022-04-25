import { createEvent, fireEvent, screen } from '@testing-library/react';
import Server from '../../services/Server';

export function fillText(label: RegExp, value: string) {
  const textInput = screen.getByRole('textbox', { name: label });
  fireEvent.change(textInput, { target: { value: value } });
}

export function fillPassword(label: RegExp, value: string) {
  const passwordInput = screen.getByLabelText(label);
  fireEvent.change(passwordInput, { target: { value: value } });
}

export function clickButton(label: RegExp) {
  fireEvent.click(screen.getByRole('button', { name: label }));
}

export function triggerRequestSuccess(response: string) {
  Server.request = function (
    URL: string,
    requestOptions: RequestInit,
    successFunction: (body: string) => void | undefined
  ) {
    successFunction(response);
    return Promise.resolve();
  };
}

export function triggerRequestFailure(response: string) {
  Server.request = function (
    URL: string,
    requestOptions: RequestInit,
    successFunction: (body: string) => void | undefined,
    errorFunction: (error: string) => void
  ) {
    errorFunction(response);
    return Promise.resolve();
  };
}

export function spyRequest() {
  const spy = jest.fn();
  Server.request = spy;
  return spy;
}

export function inputFile(file: File, input: HTMLInputElement) {
  fireEvent(
    input,
    createEvent('input', input, {
      target: { files: [file] }
    })
  );
}

export function fakeFile(size: number, type: string) {
  const file = new File([''], 'big_image.png', { type });
  Object.defineProperty(file, 'size', { value: size });
  return file;
}
