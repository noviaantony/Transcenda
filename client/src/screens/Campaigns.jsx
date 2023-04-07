import React, {useEffect, useState} from "react";
import {Typography, Space, Select} from "antd";
import { AiFillPlusCircle } from "react-icons/ai";
import Campaign from "../components/campaigns/Campaign";
import {Auth} from "aws-amplify";
import TransactionService from "../services/TransactionService";
import CampaignService from "../services/CampaignService";


const Campaigns = () => {

  const [campaigns, setCampaigns] = useState([]);
  const [date, setDate] = useState('');

  // retrieve campaigns from backend
  useEffect(() => {
    CampaignService.getAllCampaigns().then((response) => {
      setCampaigns(response);
    });
  }, []);

  useEffect(() => {
    console.log(campaigns);
  }, [campaigns]);


  const [isAddingCampaign, setIsAddingCampaign] = useState(false);
  const [newCampaign, setNewCampaign] = useState({
    campaignName: "",
    campaignDescription: "",
    campaignStartDate: "",
    campaignEndDate: "",
    merchantName: "",
    campaignRewardRate: "",
    campaignRewardType: "",
    campaignRequiredAmountSpend: "",
    cardProgramName: "",
  });

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNewCampaign((prevCampaign) => ({ ...prevCampaign, [name]: value }));
  };
  function handleStartDateChange(event) {
    const selectedDate = new Date(event.target.value);
    console.log(selectedDate.toISOString().substring(0, 10));
    setNewCampaign((prevCampaign) => ({ ...prevCampaign, campaignStartDate: selectedDate.toISOString().substring(0, 10) }));
  }
  function handleEndDateChange(event) {
    const selectedDate = new Date(event.target.value);
    console.log(selectedDate.toISOString().substring(0, 10));

    setNewCampaign((prevCampaign) => ({ ...prevCampaign, campaignEndDate: selectedDate.toISOString().substring(0, 10) }));
  }

  const handleAddCampaign = () => {
    console.log(newCampaign);
    CampaignService.createCampaign(newCampaign).then((response) => {
        console.log(response);
    });

    setCampaigns((prevCampaigns) => [
      ...prevCampaigns,
      { ...newCampaign, id: campaigns.length + 1 },
    ]);
    setNewCampaign({ campaignName: "", campaignDescription: "", campaignStartDate: "", campaignEndDate: "",
      merchantName: "", campaignRewardRate: "", campaignRewardType: "", campaignRequiredAmountSpend: "", cardProgramName: ""});
    setIsAddingCampaign(false);
  };




  return (
    <>
      <Space direction="vertical table" className="font-lexend">
        <div className="font-nunito flex content-start">
          <Typography.Title
            // level={4}
            className="font-lexend font-bold font-6xl mt-5 mr-4"
          >
            Campaign Management
          </Typography.Title>
        </div>
      </Space>
      <div className="container mx-auto px-4 py-8 w-6/6 font-lexend">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-4xl font-semibold">Current Campaigns</h1>
          <button
            className="flex items-center font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 text-white bg-[#66347F]  hover:bg-[#D1C2D8] hover:text-[#66347F] focus:outline-none ml-96"
            onClick={() => setIsAddingCampaign(true)}
          >
            Add Campaign
            <AiFillPlusCircle className="w-6 h-6 ml-1" />
          </button>
        </div>
        {campaigns && campaigns.map((campaign) => (
          <Campaign key={campaign.id} campaign={campaign} />
        ))}
        {isAddingCampaign && (
          <div className="mt-4">
            <h2 className="text-lg font-medium mb-2">Add New Campaign</h2>
            <form onSubmit={(event) => event.preventDefault()}>
              <div className="mb-4">
                <label
                  htmlFor="name"
                  className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Name
                </label>
                <input
                  type="text"
                  id="campaignName"
                  name="campaignName"
                  value={newCampaign.campaignName}
                  onChange={handleInputChange}
                  className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="campaignDescription"
                  className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Description
                </label>
                <input
                  type="text"
                  id="campaignDescription"
                  name="campaignDescription"
                  value={newCampaign.campaignDescription}
                  onChange={handleInputChange}
                  className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="campaignStartDate"
                  className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Start Date
                </label>
                <input
                  type="date"
                  id="campaignStartDate"
                  name="campaignStartDate"
                  value={newCampaign.campaignStartDate}
                  onChange={handleStartDateChange}
                  className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                  htmlFor="endDate"
                  className="block text-gray-700 font-medium mb-2"
                >
                  Campaign End Date
                </label>
                <input
                  type="date"
                  id="campaignEndDate"
                  name="campaignEndDate"
                  value={newCampaign.campaignEndDate}
                  onChange={handleEndDateChange}
                  className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                    htmlFor="merchantName"
                    className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Merchant Name
                </label>
                <input
                    type="text"
                    id="merchantName"
                    name="merchantName"
                    value={newCampaign. merchantName}
                    onChange={handleInputChange}
                    className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                    htmlFor="campaignRewardRate"
                    className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Reward Rate (e.g. 5% Cashback or 1.0 points/miles per dollar)
                </label>
                <input
                    type="number"
                    id="campaignRewardRate"
                    name="campaignRewardRate"
                    value={newCampaign.campaignRewardRate}
                    onChange={handleInputChange}
                    className="border border-gray-400 p-2 w-full"
                />
              </div>

              <div className="mb-4">
                <label
                    htmlFor="campaignRequiredAmountSpend"
                    className="block text-gray-700 font-medium mb-2"
                >
                  Campaign required amount spend to be eligible for reward
                </label>
                <input
                    type="number"
                    id="campaignRequiredAmountSpend"
                    name="campaignRequiredAmountSpend"
                    value={newCampaign.campaignRequiredAmountSpend}
                    onChange={handleInputChange}
                    className="border border-gray-400 p-2 w-full"
                />
              </div>
              <div className="mb-4">
                <label
                    htmlFor="cardProgramName"
                    className="block text-gray-700 font-medium mb-2"
                >
                  Campaign Card Program Name
                </label>
                <Select
                    id="cardProgramName"
                    name="cardProgramName"
                    value={newCampaign.cardProgramName}
                    onChange={(value) =>{
                      let rewardType = '';
                      switch (value) {
                        case 'scis_shopping':
                          rewardType = 'CASHBACK';
                          break;
                        case 'scis_premiummiles':
                          rewardType = 'MILES';
                          break;
                        case 'scis_platinummiles':
                          rewardType = 'MILES';
                          break;
                        case 'scis_freedom':
                          rewardType = 'POINTS';
                          break;
                        default:
                          rewardType = '';
                          break;
                      }
                      setNewCampaign((prevCampaign) => ({
                        ...prevCampaign,
                        cardProgramName: value,
                        campaignRewardType: rewardType,
                      }));
                    }}
                    options={[
                      {
                        value: 'scis_shopping',
                        label: 'SCIS Shopping Card',
                      },
                      {
                        value: 'scis_premiummiles',
                        label: 'SCIS PremiumMiles',
                      },
                      {
                        value: 'scis_platinummiles',
                        label: 'SCIS PlatinumMiles',
                      },
                      {
                        value: 'scis_freedom',
                        label: 'SCIS Freedom'
                      },
                    ]}
                    className="border border-gray-400 p-2 w-full"
                >
                </Select>
              </div>
              <div className="flex justify-end">
                <button
                  type="button"
                  className="rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 bg-white text-[#66347F]  hover:text-white hover:bg-[#66347F] focus:outline-none"
                  onClick={() => setIsAddingCampaign(false)}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 text-white bg-[#66347F]  hover:bg-[#D1C2D8] hover:text-[#66347F] focus:outline-none"
                  onClick={handleAddCampaign}
                >
                  Add Campaign
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </>
  );

};

export default Campaigns;