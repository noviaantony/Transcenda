import axios from 'axios';

const CAMPAIGN_API_URL = "https://api.itsag1t1.com/api/v1/transcenda/campaign/";

class CampaignService {

    async getAllCampaigns() {
        const response = await axios.get(CAMPAIGN_API_URL);
        return response.data;
    }

    async createCampaign(campaign) {

        const response = await axios.post(CAMPAIGN_API_URL, campaign);
        return response.data;
    }

    async deleteCampaignById(campaignId) {
        const response = await axios.delete(`${CAMPAIGN_API_URL}?campaignId=${campaignId}`);
        return response.data;
    }

}
export default new CampaignService();