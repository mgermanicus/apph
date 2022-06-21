import { configureStore } from '@reduxjs/toolkit';
import currentUserSlice from '../slices/userSlice';
import tagListSlice from '../slices/tagSlice';

export default configureStore({
  reducer: {
    currentUser: currentUserSlice,
    tagList: tagListSlice
  }
});
