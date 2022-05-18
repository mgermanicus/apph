import { configureStore } from '@reduxjs/toolkit';
import currentUserSlice from '../slices/userSlice';
import selectedPhotosSlice from '../slices/photoSlice';

export default configureStore({
  reducer: {
    currentUser: currentUserSlice,
    selectedPhotos: selectedPhotosSlice
  }
});
