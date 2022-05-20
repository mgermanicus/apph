import * as React from 'react';
import { DrawerMenuItem } from '../../static/components/DrawerMenuItem';
import { wrapper } from '../utils/components/CustomWrapper';
import { render } from '@testing-library/react';

describe('DrawerMenuItem test', () => {
  it('Render', () => {
    //WHEN
    const tree = render(
      <DrawerMenuItem title="test" url={'./'} icon={<></>} />,
      { wrapper }
    );
    //THEN
    expect(tree).toMatchSnapshot();
  });
});
