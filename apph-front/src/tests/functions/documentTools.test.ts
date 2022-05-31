import { openFullScreenById } from '../../utils';

describe('Document Tools', () => {
  it('should open full screen', () => {
    const el = document.createElement('div');
    el.setAttribute('id', 'my-id');
    openFullScreenById('my-id');
    expect(document.fullscreenElement !== null);
  });

  it('should not open full screen', () => {
    openFullScreenById('my-id');
    expect(document.fullscreenElement === null);
  });
});
