import { configureStore } from '@reduxjs/toolkit';
import currentUserSlice from '../slices/userSlice';
import selectedPhotosSlice from '../slices/photoSlice';
import tagListSlice from '../slices/tagSlice';

export default configureStore({
  reducer: {
    currentUser: currentUserSlice,
    selectedPhotos: selectedPhotosSlice,
    tagList: tagListSlice
  }
});
