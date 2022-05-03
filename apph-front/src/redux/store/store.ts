import { configureStore } from '@reduxjs/toolkit';
import currentUserSlice from '../slices/userSlice';

export default configureStore({
  reducer: {
    currentUser: currentUserSlice
  }
});
