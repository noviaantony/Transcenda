import './App.css';
import Landing from './screens/Landing';
import SidebarBank from "./components/navigation/SidebarBank";
import SidebarCustomer from "./components/navigation/SidebarCustomer";
import Navbar from './components/navigation/Navbar';
import PageContent from "./components/content/PageContent";
import Login from "./screens/Login";
import {Auth} from "aws-amplify";
import React, {useEffect, useState} from "react";

function App() {

  const [authGroup, setAuthGroup] = React.useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  const auth2 = true; // true - bank, false - customer

  useEffect(() => {
    checkAuthentication();
  }, []);

  const checkAuthentication = async () => {
    try {
      await Auth.currentAuthenticatedUser();
      setAuthenticated(true);
    } catch (err) {
      setAuthenticated(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    checkAuth();
  }, []);
  const checkAuth = async () => {

    if (await Auth.currentSession()) {
      const user = await Auth.currentAuthenticatedUser();
      const group = user.signInUserSession.accessToken.payload["cognito:groups"];
      console.log("Retrieved group:")
      console.log(group);
      if (group[0] === "transcenda_bank") {
        console.log("User is a bank");
        setAuthGroup(true);
      } else {
        console.log("User is a customer");
        setAuthGroup(false);
      }
    }

  }

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!authenticated) {
    return <Login />;
  }

  return (
    <div className="App font-lexend">

      {!authenticated ? (
          <Login/>
      ) : (
          <>
            <Navbar/>
            <div className="SideMenuAndPageContent">
              {authGroup ? <SidebarBank /> : <SidebarCustomer />}
              <PageContent />
            </div>
          </>
      )}
    </div>
  );
}

export default App;
