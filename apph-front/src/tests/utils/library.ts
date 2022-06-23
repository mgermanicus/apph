import { fireEvent, screen, within } from '@testing-library/react';
import Server from '../../services/Server';
import { FakeRequestResults } from './types/FakeRequestResults';
import AuthService from '../../services/AuthService';
import { ITag } from '../../utils';
import userEvent from '@testing-library/user-event';
import { isValidElement } from 'react';

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

export function clickLoadingButton(label: RegExp) {
  fireEvent.click(screen.getByRole('LoadingButton', { name: label }));
}

export function fillTags(tags: ITag[]) {
  const autocomplete = within(screen.getByTestId('tags')).getByRole('combobox');
  tags.forEach((tag) => {
    fireEvent.change(autocomplete, {
      target: { value: tag.name }
    });
    fireEvent.keyDown(autocomplete, { key: 'ArrowDown' });
    fireEvent.keyDown(autocomplete, { key: 'Enter' });
  });
  return;
}

export async function fillLocation(query: string) {
  const autocomplete = within(screen.getByTestId('location')).getByRole(
    'combobox'
  );
  const user = userEvent.setup();
  await user.click(screen.getByTestId('location'));
  await user.keyboard(query);
  await setTimeout(() => {
    fireEvent.keyDown(autocomplete, { key: 'ArrowDown' });
    fireEvent.keyDown(autocomplete, { key: 'Enter' });
  }, 1000);
}

export function fillDate(date: Date) {
  const dateInput = screen.getByLabelText(/Date/);
  fireEvent.change(dateInput, { target: { value: date } });
}

export function selectOptionInListBox(input: HTMLElement, optionName: RegExp) {
  fireEvent.mouseDown(input);
  const fieldListBox = within(screen.getByRole('listbox'));
  fireEvent.click(fieldListBox.getByRole('option', { name: optionName }));
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

export const fakeRequest = (requestResults: FakeRequestResults) => {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined,
      errorFunction: (error: string) => void
    ) => {
      const result = Object.entries(requestResults).filter(([key, value]) =>
          URL.includes(key)
      )[0][1];
      if (result?.error) {
        errorFunction(result.error);
      } else if (result?.body) {
        successFunction(result.body);
      }
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
};

export const spyCookies = () => {
  const spyUpdateUserCookie = jest.fn();
  AuthService.updateUserCookie = spyUpdateUserCookie;
  return spyUpdateUserCookie;
};

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
