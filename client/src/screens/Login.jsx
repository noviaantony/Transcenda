import React, { useRef, useState, useEffect, useContext } from "react";
import { HiMail, HiLockClosed } from "react-icons/hi";
import { ReactComponent as LoginSvg  } from "./login.svg";
import Logo from "./TranscendaLogo.png";
import { Navigate, Link } from "react-router-dom";
import { motion } from "framer-motion";
// import Navbar from "../components/landing/Navbar";
import { Auth } from "aws-amplify";

const Login = () => {
  const userRef = useRef();
  const errRef = useRef();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errMsg, setErrMsg] = useState("");
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await Auth.signIn(email, password);
      const user = await Auth.currentAuthenticatedUser();
      const group = user.signInUserSession.accessToken.payload["cognito:groups"];
      if (group[0] === "transcenda_bank") {
        window.location.href = '/BankTransactions';
      } else {
        window.location.href = '/Transactions';
      }
    } catch (err) {
      setErrMsg(err.message);
    }
  };

  useEffect(() => {
    userRef.current.focus();
  }, []);

  useEffect(() => {
    setErrMsg();
  }, [email, password]);

  return (
    <>
      {success ? (
        <>
          <br />
          <p>
          </p>
        </>
      ) : (
        <>
          {/*<Navbar />*/}
          <motion.div
            className="actions flex flex-col items-center min-h-screen py-2 font-default mt-20"
            animate={{ opacity: 1 }}
            initial={{ opacity: 0 }}
          >
       
            <img src={Logo} alt="My Image" />
            <main className="flex flex-col items-center w-full flex-1 px-20 text-center">
              <div className="bg-white rounded-2xl shadow-2xl flex w-2/3 max-w-4xl">
                <div className="w-3/5 p-5">
                  <div className="py-10">
                    <h2 className="text-2xl font-bold text-gray-700 mb-2">
                      Sign in to Transcenda
                    </h2>
                    <div className="border-2 w-10 border-gray-700 bg-gray-700 inline-block mb-2"></div>
                    <p
                      ref={errRef}
                      className={
                        errMsg
                          ? "text-red-800 text-sm mr-2 px-2.5 py-0.5 rounded dark:bg-red-200 dark:text-red-900 font-bold"
                          : "offscreen"
                      }
                      aria-live="assertive"
                    >
                      {errMsg}
                    </p>
                  </div>
                  <form> 
                    <div className="flex flex-col items-center">
                      {/* email section */}
                      <div className="bg-gray-100 w-64 p-2 flex items-center rounded mb-3">
                        <div className="bg-gray-100 w-64 p-2">
                          <HiMail className="text-grey-100 m-2" />
                        </div>
                        <input
                          type="email"
                          name="email"
                          placeholder="enter your email"
                          className="bg-gray-100 outline-none text-m flex-1"
                          ref={userRef}
                          onChange={(e) => setEmail(e.target.value)}
                          value={email}
                          required
                        />
                      </div>
                      {/* email section */}

                      {/* password section */}
                      <div className="bg-gray-100 w-64 p-2 flex items-center rounded mb-3">
                        <div className="bg-gray-100 w-64 p-2">
                          <HiLockClosed className="text-grey-100 m-2" />
                        </div>
                        <input
                          type="password"
                          name="password"
                          placeholder="enter your password"
                          className="bg-gray-100 outline-none text-m flex-1"
                          onChange={(e) => setPassword(e.target.value)}
                          value={password}
                          required
                        />
                      </div>
                      {/* password section */}

                      <button
                        // href=""
                        className="signIn px-7 py-3 w-64 justify-center rounded-md border border-transparent text-sm focus:outline-none transition duration-300 bg-[#66347F] hover:bg-[#FDEDE1] text-center marker:sm:w-auto font-bold text-white hover:text-[#66347F] hover:bg-white"
                        onClick={handleSubmit}
                      >
                        Sign In
                      </button>
                    </div>
                  </form>
                </div>
                <div className="w-2/5 bg-[#D1C2D8]  text-white rounded-tr-2xl rounded-br-2xl py-36 px-12">
                  <LoginSvg width="16rem" />
                </div>
              </div>
            </main>
          </motion.div>
        </>
      )}
    </>
  );
};


export default Login;

