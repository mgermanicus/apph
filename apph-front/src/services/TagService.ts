import { getTokenHeader } from '../utils';
import Server from './Server';

export default class TagService {
  static getAllTags(
    handleSuccess: (tags: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'GET',
      headers: getTokenHeader()
    };
    return Server.request(`/tag/`, requestOptions, handleSuccess, handleError);
  }

  static getAllTagsCount(
    handleSuccess: (tags: { value: string; count: number }[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'GET',
      headers: getTokenHeader()
    };
    const successFunction = (tags: string) => {
      handleSuccess(JSON.parse(tags).taglist);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/tag/count`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
