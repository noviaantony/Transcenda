import {useEffect, useState} from "react";
import {Spin, Table} from "antd";
import {Auth} from "aws-amplify";
import TransactionService from "../../services/TransactionService";
import CardService from "../../services/CardService";


const columns = [
    {
        title: "Transaction Id",
        dataIndex: "transactionId",
        key: "transactionId",
        render: (text) => <a>{text}</a>,
    },
    {
        title: "Transaction Date",
        dataIndex: "transactionDate",
        key: "transactionDate",
    },
    {
        title: "Merchant Name",
        dataIndex: "merchantName",
        key: "merchantName",
    },
    {
        title: "Card Program Type",
        dataIndex: "cardProgramType",
        key: "cardProgramType",
    },
    {
        title: "Transaction Reward",
        dataIndex: "transactionRewardEarned",
        key: "transactionRewardEarned",
        render: (value) => parseFloat(value).toFixed(2),
    },
    {
        title: "Reward Balance (after transaction)",
        dataIndex: "transactionRewardBalance",
        key: "transactionRewardBalance",
        render: (value) => parseFloat(value).toFixed(2),
    },
    {
        title: "Transaction Amount",
        dataIndex: "transactionAmount",
        key: "transactionAmount",
        render: (value) => "$" + parseFloat(value).toFixed(2),
    },
    {
        title: "Transaction Currency",
        dataIndex: "transactionCurrency",
        key: "transactionCurrency",
    },
    {
        title: "Remarks",
        dataIndex: "remarks",
        key: "remarks",
    }
];


const TransactionsTable = () => {
    const [searchTerm, setSearchTerm] = useState("");

    const [transactions, setTransactions] = useState([]);
    const [pointBalance, setPointBalance] = useState(0);
    const [mileBalances, setMileBalances] = useState(0);
    const [cashbackBalances, setCashbackBalances] = useState(0);
    const [cards, setCards] = useState([]);
    const [loading, setLoading] = useState(true);

    const checkAuthGroup = async () => {
        const user = await Auth.currentAuthenticatedUser();
        return user.signInUserSession.accessToken.payload["cognito:groups"][0];
    };

    useEffect(() => {
        const fetchTransactions = async () => {
            setLoading(true);
            const group = await checkAuthGroup();
            const retrieveUserInformation = await Auth.currentUserInfo();

            if (group === "transcenda_customers") {
                console.log("Retrieving customer transactions")
                TransactionService.getAllUserTransactions(retrieveUserInformation.attributes.email).then((res) => {
                    setTransactions(res);
                });
                CardService.getUserCards(retrieveUserInformation.attributes.email).then((res) => {
                    setCards(res);
                });
                CardService.getUserRewardBalance(retrieveUserInformation.attributes.email).then((res) => {
                    setPointBalance(parseFloat(res[0].split("=")[1]));
                    setMileBalances(parseFloat(res[1].split("=")[1]));
                    setCashbackBalances(parseFloat(res[2].split("=")[1]));
                });
            } else {
                console.log("Retrieving recent transactions")
                TransactionService.getRecentTransactions().then((res) => {
                    setTransactions(res);
                });
            }
            setLoading(false);
        }

        fetchTransactions();
    }, []);


    useEffect(() => {
        // console.log(transactions)
        // console.log(pointBalance)
        // console.log(mileBalances)
        // console.log(cashbackBalances)
        // console.log(cards)
    }, [transactions, pointBalance, mileBalances, cashbackBalances, cards]);


    return (
        <>
            {loading ? (
                <Spin size="large"/>
            ) : (
                <>
                    <div className="flex my-2 mb-5 justify-center items-center">

                        {/* Balance Summary  */}
                        <div class="w-1/6 font-default mr-4 flex-grow">
                            <div
                                className="p-4 text-sm text-[#5e938780] bg-white rounded-lg flex items-stretch  drop-shadow-sm font-default cursor-pointer h-30 shadow-md">
                                <div className="m-auto text-xl text-center font-semibold text-gray-700 ">
                                    <h1 class="text-4xl text-center font-bold"> {Math.floor(pointBalance)} </h1>
                                    <h5 class="text-xs text-center"> Points Balance </h5>
                                </div>
                            </div>
                        </div>
                        {cards.filter(card => card.cardProgram.cardProgram === 'scis_premiummiles' || card.cardProgram.cardProgram === 'scis_platinummiles').map((card, i) => (
                            <div className="w-1/6 font-default mr-4 flex-grow" key={i}>
                                <div
                                    className="p-4 text-sm text-[#5e938780] bg-white rounded-lg flex items-stretch drop-shadow-sm font-default cursor-pointer h-30 shadow-md">
                                    <div className="m-auto text-xl text-center font-semibold text-gray-700">
                                        <h1 className="text-4xl text-center font-bold">{Math.floor(card.rewardEarned)}</h1>
                                        <h5 className="text-xs text-center">{card.cardProgram.cardProgram === 'scis_premiummiles' ? 'Premium Miles' : 'Platinum Miles'} Balance</h5>
                                    </div>
                                </div>
                            </div>
                        ))}

                        <div class="w-1/6 font-default mr-4 flex-grow ">
                            <div
                                className="p-4 text-sm text-[#5e938780] bg-white rounded-lg flex items-stretch  drop-shadow-sm font-default cursor-pointer h-30 shadow-md">
                                <div className="m-auto text-xl text-center font-semibold text-gray-700 ">
                                    <h1 class="text-4xl text-center font-bold"> ${cashbackBalances.toFixed(2)} </h1>
                                    <h5 class="text-xs text-center"> Cashback Balance </h5>
                                </div>
                            </div>
                        </div>
                    </div>

                    <form className="m-5 mx-50">
                        <label
                            htmlFor="default-search"
                            className="mb-2 text-sm font-medium text-gray-900 sr-only dark:text-gray-300 font-nunito"
                        >
                            Search
                        </label>
                        <div className="relative ">
                            <div className="flex items-center absolute inset-y-0 left-0 pl-3 pointer-events-none">
                                <svg
                                    aria-hidden="true"
                                    className="w-5 h-5 text-gray-500 dark:text-gray-400"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        stroke-width="2"
                                        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                                    ></path>
                                </svg>
                            </div>
                            <div className="flex flex-row">
                                <input
                                    type="search"
                                    id="default-search"
                                    className="block p-4 pl-10 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border"
                                    placeholder="Search by Merchant Name, Transaction Id"
                                    required=""
                                    onChange={(event) => {
                                        setSearchTerm(event.target.value);
                                    }}
                                />
                            </div>
                        </div>
                    </form>
                    <Table
                        className="font-nunito text-grey-700 shadow-md rounded-2xl "
                        columns={columns}
                        style={{backgroundColor: "#D1C2D8"}}
                        dataSource={transactions.filter((val) => {
                            return val;
                        })}
                        components={{
                            header: {
                                cell: (props) => (
                                    <th
                                        {...props}
                                        style={{
                                            backgroundColor: "#D1C2D8",
                                            fontWeight: "bold",
                                            font: "font-lexend",
                                        }}
                                    />
                                ),
                            },
                        }}
                    />
                </>)}
        </>
    );
};

export default TransactionsTable;
