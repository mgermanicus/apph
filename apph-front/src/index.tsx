import React from 'react';
import reportWebVitals from './reportWebVitals';
import { SignIn } from './static/components/SignIn';
import { createRoot } from 'react-dom/client';

const container = document.getElementById('root');
// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
const root = createRoot(container!);
root.render(
  <React.StrictMode>
    <SignIn />
  </React.StrictMode>
);
reportWebVitals();
