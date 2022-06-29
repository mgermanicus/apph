import { getTokenHeader } from '../utils';
import Server from './Server';
import Cookies from 'universal-cookie';

const cookies = new Cookies();
export default class SettingService {
  static getSettings(
    handleSuccess: (setting: string) => void,
    handleError: () => void
  ) {
    const requestOptions = {
      method: 'GET',
      headers: getTokenHeader()
    };
    return Server.request(
      `/admin/getSettings`,
      requestOptions,
      handleSuccess,
      handleError
    );
  }

  static updateSettings(
    uploadSize: number,
    downloadSize: number,
    handleSuccess: (setting: string) => void,
    handleError: () => void
  ) {
    const URL = `/admin/updateSettings`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          uploadSize,
          downloadSize
        })
      };
    return Server.request(URL, requestOptions, handleSuccess, handleError);
  }
}
