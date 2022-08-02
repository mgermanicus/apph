import Server from '../../services/Server';
import { FakeRequestResults } from './types/FakeRequestResults';

/**
 * Mocks successful Server.request
 * @param response success response
 * @returns mocked Server.request
 */
export function triggerRequestSuccess(response: string) {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined
    ) => {
      successFunction(response);
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
}

/**
 * Mocks Server.request failure
 * @param response error response
 * @returns mocked Server.request
 */
export function triggerRequestFailure(response: string) {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined,
      errorFunction: (error: string) => void
    ) => {
      errorFunction(response);
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
}

/**
 * Mocks Server.request by associating URLs with responses.
 * @param requestResults each URL is associated with a response. The response is an object containing either a body attribute or an error attribute.
 * @param defaultResponse if the URL provided to Server.request does not match any of those in requestResults, this is used to determine if success or error.
 * @returns mocked Server.request
 */
export const fakeRequest = (
  requestResults?: FakeRequestResults,
  defaultResponse: 'success' | 'error' = 'error'
) => {
  const spy = jest.fn(
    (
      URL: string,
      requestOptions: RequestInit,
      successFunction: (body: string) => void | undefined,
      errorFunction: (error: string) => void
    ) => {
      if (requestResults) {
        const result = requestResults[URL];
        if (result.error) {
          errorFunction(result.error);
        } else if (result.body) {
          successFunction(result.body);
        }
      } else if (defaultResponse === 'success') {
        successFunction('{}');
      } else {
        errorFunction('{"message":"erreur"}');
      }
      return Promise.resolve();
    }
  );
  Server.request = spy;
  return spy;
};
