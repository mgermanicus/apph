import React from 'react';
import { createRoot } from 'react-dom/client';
import reportWebVitals from './reportWebVitals';
import { App } from './App';

const el = document.getElementById('root');
if (el === null) throw new Error('Root container missing in index.html');

const root = createRoot(el);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

reportWebVitals();
