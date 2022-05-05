import { fireEvent, screen } from '@testing-library/react';
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

export function spyRequestFailure(error: string) {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined,
      errorFunction: (error: string) => void
    ) => {
      errorFunction(error);
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
}

export function spyRequestSuccess() {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined
    ) => {
      successFunction('');
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
}

export function spyRequestSuccessBody(body: string) {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined
    ) => {
      successFunction(body);
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
}
