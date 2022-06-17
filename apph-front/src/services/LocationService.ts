import { IGeocodeResponse, ILocation } from '../utils/types/Location';
import Server from './Server';
import { useTranslation } from 'react-i18next';

const HERE_GEOCODE_API = 'https://geocode.search.hereapi.com/v1';
const HERE_API_KEY = process.env['REACT_APP_HERE_API_KEY'];

export class LocationService {
  static geocode(
    query: string,
    lang: string,
    handleSuccess: (locations: ILocation[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'GET'
    };
    const successFunction = (results: string) => {
      const response: IGeocodeResponse = JSON.parse(results);
      console.log(response);
      const locations = response.items.map((result): ILocation => {
        return { address: result.address.label, position: result.position };
      });
      console.log(locations);
      handleSuccess(locations);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      encodeURI(`/geocode?q=${query}&apiKey=${HERE_API_KEY}&lang=${lang}`),
      requestOptions,
      successFunction,
      errorFunction,
      HERE_GEOCODE_API
    );
  }
}
