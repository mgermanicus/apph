import { MapPage } from '../../static/pages/MapPage';
import { render } from '@testing-library/react';
import { triggerRequestSuccess } from '../utils';

describe('Test MapPage', () => {
  it('should render', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"markerList":[{"id":17,"lat":48.85717,"lng":2.3414},{"id":18,"lat":48.85717,"lng":2.3414},{"id":19,"lat":48.85717,"lng":2.3414},{"id":20,"lat":43.64869,"lng":-79.38544},{"id":28,"lat":48.85717,"lng":2.3414},{"id":29,"lat":-8.67325,"lng":115.20338}]}'
    );
    render(<MapPage />);
  });
});
