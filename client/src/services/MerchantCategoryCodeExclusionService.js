import axios from 'axios';

const MCC_EXCLUSION_API_URL = "https://api.itsag1t1.com/api/v1/transcenda/merchant-category-code-exclusion";

class MerchantCategoryCodeExclusionService {
    async getAllMerchantCategoryCodeExclusions() {
        const response = await axios.get(MCC_EXCLUSION_API_URL);
        return response.data;
    }
    async addNewMerchantCategoryCodeExclusion(merchantCategoryCodeExclusion) {
        const response = await axios.post(MCC_EXCLUSION_API_URL, merchantCategoryCodeExclusion);
        return response.data;
    }

    async deleteMerchantCategoryCodeExclusionById(merchantCategoryCodeExclusionId) {
        const response = await axios.delete(`${MCC_EXCLUSION_API_URL}?merchantCategoryCode=${merchantCategoryCodeExclusionId}`);
        return response.data;
    }
}
export default new MerchantCategoryCodeExclusionService();