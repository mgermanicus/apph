import { configureStore } from '@reduxjs/toolkit';
import currentUserSlice from '../slices/userSlice';
import tagListSlice from '../slices/tagSlice';
import selectedPhotosSlice from '../slices/photoSlice';

export default configureStore({
  reducer: {
    currentUser: currentUserSlice,
    tagList: tagListSlice,
    selectedPhotos: selectedPhotosSlice
  }
});
