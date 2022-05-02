import { createSlice, Draft, PayloadAction } from '@reduxjs/toolkit';
import { IUser } from '../../utils';

export const currentUserSlice = createSlice({
  name: 'currentUser',
  initialState: {
    firstname: '',
    lastname: '',
    login: ''
  } as IUser,
  reducers: {
    changeCurrentUser: (state: Draft<IUser>, action: PayloadAction<IUser>) => {
      state.firstname = action.payload.firstname;
      state.lastname = action.payload.lastname;
      state.login = action.payload.login;
    }
  }
});

export const { changeCurrentUser } = currentUserSlice.actions;

export default currentUserSlice.reducer;
