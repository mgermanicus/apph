import React from 'react';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { SignIn } from './static/components/SignIn';
import { SignUp } from './static/components/SignUp';
import { createRoot } from 'react-dom/client';

const container = document.getElementById('root');
// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
const root = createRoot(container!);
root.render(
  <React.StrictMode>
    <SignUp />
    <SignIn />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
