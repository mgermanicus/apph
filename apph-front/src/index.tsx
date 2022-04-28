import React from 'react';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { createRoot } from 'react-dom/client';
import App from './App';
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

function RouterSwitch() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/signIn" element={<SignIn />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

root.render(
  <React.StrictMode>
    <Router>
      <RouterSwitch />
    </Router>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
