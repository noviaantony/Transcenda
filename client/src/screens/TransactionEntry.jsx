import React, {createContext, useState} from "react";
import {Input, Modal, Select, Space, Typography} from "antd";
import TransactionService from "../services/TransactionService";

const ReachableContext = createContext(null);
const UnreachableContext = createContext(null);

const config = {
  title: 'Would you like to upload a transaction record?',
};


const { TextArea } = Input;

const TransactionEntry = () => {

  const [modal, contextHolder] = Modal.useModal();

  const onChange = (value) => {
    console.log(`selected ${value}`);
  };
  const onSearch = (value) => {
    console.log('search:', value);
  };

  function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${year}-${month}-${day}`;
  }

  const handleCardProgramChange = (value) => {
    setNewTransaction((prevTransaction) => ({...prevTransaction, cardProgramType: value}));
  };

  const handleCurrencyChange = (value) => {
    setNewTransaction((prevTransaction) => ({...prevTransaction, transactionCurrency: value}));
  };

  const [newTransaction, setNewTransaction] = useState({
    "cardNumber": "",
    "transactionDate": "",
    "cardProgramType": "",
    "transactionAmount": "",
    "transactionCurrency": "",
    "merchantName": "",
    "merchantCategoryCode": ""
  });

  const handleInputChange = (event) => {
    const {name, value} = event.target;
    if (name === "transactionDate") {
      setNewTransaction((prevTransaction) => ({...prevTransaction, [name]: formatDate(value)}));
    } else {
      setNewTransaction((prevTransaction) => ({...prevTransaction, [name]: value}));
    }
  };

  const handleAddTransaction = () => {
    const date = new Date(newTransaction.transactionDate);
    newTransaction.transactionDate = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear().toString()}`;
    console.log(newTransaction);
    TransactionService.addTransaction(newTransaction).then((response) => {
      console.log(response);
    });

    setNewTransaction({"cardNumber": "",
      "transactionDate": "",
      "cardProgramType": "",
      "transactionAmount": "",
      "transactionCurrency": "",
      "merchantName": "",
      "merchantCategoryCode": ""});
  }


  return (
      <>

      <ReachableContext.Provider value="Light">
        <Space direction="vertical table" className="font-lexend">
          <div className="font-nunito flex content-start">
            <Typography.Title
                className="font-lexend font-bold font-6xl mt-5 mr-4"
            >
              Manual Transaction Entry
            </Typography.Title>
          </div>

          <div>
            <h1 className = "text-xl">Card Number: </h1>
            <Input
                id="cardNumber"
                name="cardNumber"
                placeholder="Enter Card Number"
                type = "text"
                onChange={handleInputChange}
                className = "text-md mb-5 font-lexend "
                value={newTransaction.cardNumber}
                allowClear
            />
          </div>
          <div>
            <h1 className = "text-xl">Transaction Date: </h1>
            <Input
                id="transactionDate"
                name="transactionDate"
                placeholder="Enter Transaction Date"
                type = "date"
                onChange={handleInputChange}
                value={newTransaction.transactionDate}
                className = "text-md mb-5 font-lexend "
                 />
          </div>
           <div>
            <h1 className = "text-xl">Merchant Category Code (MCC): </h1>
            <Input
                id="merchantCategoryCode"
                name="merchantCategoryCode"
                placeholder="Enter Merchant Category Code: "
                type = "text"
                onChange={handleInputChange}
                value={newTransaction.merchantCategoryCode}
                className = "text-md mb-5 font-lexend "
                allowClear />
          </div>
    
          <div>
            <h1 className = "text-xl">Merchant Name: </h1>
            <Input
                id="merchantName"
                name="merchantName"
                placeholder="Enter Merchant Name"
                type = "text"
                onChange={handleInputChange}
                value={newTransaction.merchantName}
                className = "text-md mb-5 font-lexend "
                allowClear />
          </div>
            <div>
            <h1 className = "text-xl">Card Program: </h1>
            <Select
              className = "text-md mb-5 font-lexend " 
              showSearch
              placeholder="Select Card Program"
              optionFilterProp="children"
              onChange={handleCardProgramChange}
              value={newTransaction.cardProgramType}
              onSearch={onSearch}
              style={{
                width: 300,
              }}
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={[
                {
                  value: 'scis_premiummiles',
                  label: 'SCIS PremiumMiles',
                },
                {
                  value: 'scis_freedom',
                  label: 'SCIS Freedom',
                },
                {
                  value: 'scis_platinummiles',
                  label: 'SCIS PlatinumMiles',
                },
                {
                  value: 'scis_shopping',
                  label: 'SCIS Shopping Card',
                },
              ]}
            />
          </div>
          <div>
            <h1 className = "text-xl">Transaction Amount: </h1>
            <Input
                id="transactionAmount"
                name="transactionAmount"
                placeholder="Enter Transaction Amount"
                type = "number"
                onChange={handleInputChange}
                value={newTransaction.transactionAmount}
                className = "text-md mb-5 font-lexend "
                allowClear prefix = "$" />
          </div>
          <div>
            <h1 className = "text-xl w-1">Currency: </h1>
          <Select
            // className = "text-md mb-10 font-lexend w-max"
            // showSearch
            placeholder="Select a Currency"
            // optionFilterProp="children"
            onChange={handleCurrencyChange}
            // onSearch={onSearch}
            value={newTransaction.transactionCurrency}
            // filterOption={(input, option) =>
            //   (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
            // }
            className="w-full bg-white max-h-60 overflow-auto sm:max-h-80 mb-5"
            options={[
              {
                value: 'SGD',
                label: 'SGD',
              },
              {
                value: 'USD',
                label: 'USD',
              },
            ]}
          />
          </div>
          <button
              type="button"
              className="text-white bg-[#66347F] hover:bg-[#D1C2D8] hover:text-[#66347F] font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2"
              onClick={() => {
                handleAddTransaction();
          }}

          >
            Upload Transaction Record
          </button>
        </Space>
          {contextHolder}
          <UnreachableContext.Provider value="Bamboo" />
        </ReachableContext.Provider>
      </>
  );
};

export default TransactionEntry;

