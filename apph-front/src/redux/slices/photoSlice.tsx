import { createSlice } from '@reduxjs/toolkit';
import { ITable } from '../../utils';

export const selectedPhotosSlice = createSlice({
  name: 'selectedPhotos',
  initialState: [] as ITable[],
  reducers: {
    replaceSelectedPhotos: (state, action) => {
      const photos: ITable[] = JSON.parse(action.payload);
      return [
        ...state.filter((photo: ITable) => photos.includes(photo)),
        ...photos
      ];
    }
  }
});

export const { replaceSelectedPhotos } = selectedPhotosSlice.actions;

export default selectedPhotosSlice.reducer;
