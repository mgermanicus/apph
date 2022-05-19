import { createSlice } from '@reduxjs/toolkit';
import { IPhoto } from '../../utils';

export const selectedPhotosSlice = createSlice({
  name: 'selectedPhotos',
  initialState: [] as IPhoto[],
  reducers: {
    replaceSelectedPhotos: (state, action) => {
      const photos: IPhoto[] = JSON.parse(action.payload);
      return [
        ...state.filter((photo: IPhoto) => photos.includes(photo)),
        ...photos
      ];
    }
  }
});

export const { replaceSelectedPhotos } = selectedPhotosSlice.actions;

export default selectedPhotosSlice.reducer;
