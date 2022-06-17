export interface ILocation {
  address: string;
  position: IPosition;
}

export interface IPosition {
  lat: number;
  lng: number;
}

export interface IGeocodeResponse {
  items: IGeocodeResult[];
}

interface IGeocodeResult {
  address: { label: string };
  position: IPosition;
}
