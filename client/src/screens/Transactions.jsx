import React from "react";
import {Space, Typography} from "antd";
import './../App.css';
import TransactionsTable from "../components/transactions/TransactionsTable";
import TransactionService from "../services/TransactionService";
import {Auth} from "aws-amplify";


const Transactions = () => {

    return (
        <Space direction="vertical table font-lexend">
            <Typography.Title
                // level={4}
                className="font-lexend font-bold font-6x mt-5"
            >
                Transactions
            </Typography.Title>
            <TransactionsTable/>        
        </Space>
    );
};

export default Transactions;