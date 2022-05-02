import Cookies from 'universal-cookie';
import Server from './Server';
import { ITable } from '../utils/types/table';

const cookies = new Cookies();

class TableService {
  static getData(
    setData: (val: any) => void,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/photo/infos`;
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: userInfos?.token
      }
    };
    const successFunction = (val: string) => {
      let i = 1;
      const tab: Array<ITable> = JSON.parse(val);
      for (const elt of tab) {
        elt.id = i++;
      }
      setData(tab);
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}

export default TableService;
