import React, {useState} from "react";
import { HiOutlineTrash } from "react-icons/hi";
import { ExclamationCircleOutlined } from '@ant-design/icons';
import { Button, Modal, Space } from 'antd';
import CampaignService from "../../services/CampaignService";
import { ExclamationCircleFilled } from '@ant-design/icons';


const Campaign = ({ campaign }) => {
  const { campaignId,campaignName, campaignDescription, campaignStartDate, campaignEndDate,
    merchantName, campaignRewardRate, campaignRewardType, campaignRequiredAmountSpend, cardProgramName} = campaign;

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`;
  };

  const handleDelete = async (campaignId) => {
    try {
      const response = await CampaignService.deleteCampaignById(campaignId);
      console.log(response);
      window.location.reload();
    } catch (error) {
      console.log(error);
    }
  }


  const { confirm } = Modal;
  const showConfirm = () => {
    confirm({
      title: 'Do you want to delete this campaign?',
      icon: <ExclamationCircleFilled />,
      okButtonProps:{
        style: { background: '#ff4d4f', borderColor: '#ff4d4f' },
      },
      onOk() {

        handleDelete(campaignId);
        console.log('OK');
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  };

  let cardProgramFullName = '';

  if (cardProgramName === 'scis_shopping') {
    cardProgramFullName = 'SCIS Shopping';
  } else if (cardProgramName === 'scis_premiummiles') {
    cardProgramFullName = 'SCIS Premium Miles';
  } else if (cardProgramName === 'scis_platinummiles') {
    cardProgramFullName = 'SCIS Platinum Miles';
  } else if (cardProgramName === 'scis_freedom') {
    cardProgramFullName = 'SCIS Freedom';
  }

  return (


    <div className="w-full rounded p-4 mb-4 bg-white font-lexend relative">
    <button className="absolute top-0 right-0 p-2" onClick={showConfirm}>
      <HiOutlineTrash size={25} className="mt-3 mr-2"/>
    </button>
    <div className="flex items-center mb-2">
      <h2 className="text-lg font-medium">{campaignName}</h2>
      <div className="flex items-center ml-4">
        <span>
          {formatDate(campaignStartDate)} - {formatDate(campaignEndDate)}
        </span>
      </div>
    </div>
      <p>Card Program: {cardProgramFullName}</p>
    <p>Card Description: {campaignDescription}</p>
  </div>
  );
};

export default Campaign;
