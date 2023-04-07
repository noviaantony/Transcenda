import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { BrowserRouter } from "react-router-dom";
import awsConfig from "./awsConfig";
import {Amplify} from "aws-amplify";

const root = ReactDOM.createRoot(document.getElementById('root'));



Amplify.configure(awsConfig);

root.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);

