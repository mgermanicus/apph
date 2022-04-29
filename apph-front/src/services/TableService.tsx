import Cookies from 'universal-cookie';
import Server from './Server';

const API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

class TableService {
  static getData(
    setData: (val: any) => void,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `${API_URL}/photo/infos`;
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: userInfos?.token
      }
    };
    const successFunction = (val: any) => {
      let i = 1;
      val = JSON.parse(val);
      for (let elt of val) {
        elt.id = i++;
      }
      setData(val);
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}

export default TableService;
