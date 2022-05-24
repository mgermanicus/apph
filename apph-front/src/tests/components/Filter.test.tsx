import { render } from '@testing-library/react';
import { AdvancedResearchPage } from '../../static/pages/AdvancedResearchPage';
import { clickButton } from '../utils';

describe('Test du fonctionnement des filtres', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test if a filter is correctly added', () => {
    //GIVEN
    render(<AdvancedResearchPage />);
    //WHEN
    clickButton(/Ajouter un filtre/);
    //THEN

    //expect();
  });
});
