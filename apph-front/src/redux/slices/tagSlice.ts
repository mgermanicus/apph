import { createSlice, Draft, PayloadAction } from '@reduxjs/toolkit';
import { ITag } from '../../utils';

export const tagListSlice = createSlice({
  name: 'tagList',
  initialState: [] as ITag[],
  reducers: {
    setTagList: (state: Draft<ITag[]>, action: PayloadAction<ITag[]>) =>
      (state = action.payload),
    addTag: (state: Draft<ITag[]>, action: PayloadAction<ITag>) => {
      state.push(action.payload);
    }
  }
});

export const { setTagList, addTag } = tagListSlice.actions;

export default tagListSlice.reducer;
