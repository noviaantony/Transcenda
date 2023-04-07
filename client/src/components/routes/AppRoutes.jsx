import React, {useEffect} from "react";
import {Route, Routes} from "react-router-dom";
import CSVUpload from '../../screens/CSVUpload'
import Campaigns from "../../screens/Campaigns";
import Exclusions from "../../screens/Exclusions";
import CardPrograms from "../../screens/CardPrograms";
import Transactions from "../../screens/Transactions";
import TransactionEntry from "../../screens/TransactionEntry";
import PageNotFound from "../../screens/PageNotFound";
import {Auth} from "aws-amplify";

const AppRoutes = () => {

    const [auth, setAuth] = React.useState(false);

    useEffect(() => {
        checkAuth();
    }, []);
    const checkAuth = async () => {

        if (await Auth.currentSession()) {
            const user = await Auth.currentAuthenticatedUser();
            const group = user.signInUserSession.accessToken.payload["cognito:groups"];
            if (group[0] === "transcenda_bank") {
                setAuth(true);
            } else {
                setAuth(false);
            }
        }

    }

    return (
        <Routes>
            {auth ? (
                <>
                    <Route path="/BankTransactions" element={<Transactions/>}/>
                    <Route path="/CSVUpload" element={<CSVUpload/>}/>
                    <Route path="/Campaigns" element={<Campaigns/>}/>
                    <Route path="/Exclusions" element={<Exclusions/>}/>
                    <Route path="/CardPrograms" element={<CardPrograms/>}/>
                    <Route path="/TransactionEntry" element={<TransactionEntry/>}/>
                    <Route path="/" element={<Transactions/>}/>
                    <Route path="*" element={<PageNotFound/>}/>
                </>
            ) : (
                <>
                    <Route path="/Transactions" element={<Transactions/>}/>
                    <Route path="/CardPrograms" element={<CardPrograms/>}/>
                    <Route path="/" element={<Transactions/>}/>
                    <Route path="*" element={<PageNotFound/>}/>
                </>
            )}
        </Routes>
    );
};

export default AppRoutes;
