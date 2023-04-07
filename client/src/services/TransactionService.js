import axios from 'axios';

const TRANSACTION_API_URL = "https://api.itsag1t1.com/api/v1/transcenda/transaction/";

class TransactionService {
    // retrieve all transactions for a user
    async getAllUserTransactions(userEmail) {
        const response = await axios.get(`${TRANSACTION_API_URL}?userEmail=${userEmail}`);
        return response.data;
    }

    //add a transaction, just to show the capability of the application
    async addTransaction(transaction) {
        console.log(transaction)
        const response = await axios.post(TRANSACTION_API_URL, transaction);
        return response.data;
    }

    //retrieve recent 50 transactions
    async getRecentTransactions() {
        const response = await axios.get(`${TRANSACTION_API_URL}`);
        return response.data;
    }
}
export default new TransactionService();
