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
}
