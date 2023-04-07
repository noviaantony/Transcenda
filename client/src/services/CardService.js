import axios from 'axios';

const CARD_API_URL = "https://api.itsag1t1.com/api/v1/transcenda/card/";

class CardService {
    async getUserRewardBalance(userEmail) {
        const response = await axios.get(`${CARD_API_URL}rewardbalances?userEmail=${userEmail}`);
        return response.data;
    }
    async getUserCards(userEmail) {
        const response = await axios.get(`${CARD_API_URL}?userEmail=${userEmail}`);
        return response.data;
    }
}
export default new CardService();