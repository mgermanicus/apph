import { fireEvent, render, screen, within } from '@testing-library/react';
import AuthService from '../../services/AuthService';
import { ITag } from '../../utils';
import userEvent from '@testing-library/user-event';
import { ReactElement } from 'react';
import { wrapper } from './components/CustomWrapper';

export function renderWithWrapper(component: ReactElement) {
  return render(component, { wrapper });
}

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
  await userEvent.type(screen.getByTestId('location'), query);
  await userEvent.keyboard('{arrowdown}{enter}');
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

export const spyCookies = () => {
  const spyUpdateUserCookie = jest.fn();
  AuthService.updateUserCookie = spyUpdateUserCookie;
  return spyUpdateUserCookie;
};

export function deleteTags() {
  const deleteButtons = screen.getAllByTestId('CancelIcon');
  deleteButtons.forEach((button) => fireEvent.click(button));
}

export function fakeSearchRequestParams(
  target: string,
  page: number,
  pageSize: number
) {
  const requestOptions = {
    method: 'POST',
    body: JSON.stringify({
      target,
      page,
      pageSize
    })
  };
  return { URL: `/photo/search`, requestOptions };
}

export function fakeFuzzySearchRequestParams(target: string) {
  const requestOptions = {
    method: 'POST',
    body: JSON.stringify({
      target
    })
  };
  return { URL: `/photo/search/fuzzy`, requestOptions };
}
