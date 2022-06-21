import * as React from 'react';
import { UserTable } from '../../static/components/UserTable';
import { render, screen } from '@testing-library/react';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('UserTable Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Tests array display when data are send', async () => {
    //GIVEN
    const data = [
      {
        id: 'hl3@email.com',
        firstname: 'Gordon',
        lastname: 'Freeman',
        email: 'hl3@email.com'
      },
      {
        id: 'isaac@email.com',
        firstname: 'Isaac',
        lastname: 'Clarke',
        email: 'isaac@email.com'
      }
    ];
    //WHEN
    render(<UserTable data={data} loading={false} />);
    //THEN
    expect(screen.getByText(/Freeman/)).toBeInTheDocument();
    expect(screen.getByText(/Isaac/)).toBeInTheDocument();
  });
});
