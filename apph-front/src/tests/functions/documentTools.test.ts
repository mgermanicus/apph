import { openFullScreenById } from '../../utils';

describe('Document Tools', () => {
  it('openFullScreenById Test', () => {
    const el = document.createElement('div');
    el.setAttribute('id', 'my-id');
    openFullScreenById('my-id');
    expect(document.fullscreenElement !== null);
  });
});
