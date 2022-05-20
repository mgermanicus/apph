import { replaceSelectedPhotos } from '../../redux/slices/photoSlice';
import store from '../../redux/store/store';

describe('photo slice test', () => {
  describe('replaceSelectedPhotos', () => {
    it('Should initially array empty', () => {
      const state = store.getState().selectedPhotos;
      expect(state).toEqual([]);
    });

    it('Should set selectedPhotos', () => {
      const selectedPhotos = [
        {
          id: 42,
          size: 42,
          creationDate: new Date(),
          shootingDate: new Date(),
          description: 'string',
          title: 'string',
          url: 'string',
          tags: [{ name: 'tag' }],
          format: 'string'
        }
      ];
      const state = store.dispatch(
        replaceSelectedPhotos(JSON.stringify(selectedPhotos))
      );
      expect(state.payload).toEqual(JSON.stringify(selectedPhotos));
    });
  });
});
