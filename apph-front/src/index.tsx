import { App } from './App';
import React from 'react';
import reportWebVitals from './reportWebVitals';
import { createRoot } from 'react-dom/client';
import {
  Route,
  Routes,
  Navigate,
  BrowserRouter as Router
} from 'react-router-dom';
import { SignIn } from './static/components/SignIn';
import { SignUp } from './static/components/SignUp';

const container = document.getElementById('root');
// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
const root = createRoot(container!);

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

reportWebVitals();
