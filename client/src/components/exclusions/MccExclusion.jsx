import React from "react";
import MerchantCategoryCodeExclusionService from "../../services/MerchantCategoryCodeExclusionService";
import CampaignService from "../../services/CampaignService";
import {Modal} from "antd";
import {ExclamationCircleFilled} from "@ant-design/icons";
import {HiOutlineTrash} from "react-icons/hi";

const MccExclusion = ({ mccExclusion }) => {
  const { merchantCategoryCode, mccDescription,mccMerchantName} = mccExclusion;

    //delete mcc exclusion
    const handleDelete  = async (merchantCategoryCode) => {
        try {
          const response = await MerchantCategoryCodeExclusionService.deleteMerchantCategoryCodeExclusionById(merchantCategoryCode);
          console.log(response);
          window.location.reload();
        } catch (error) {
          console.log(error);
        }
    }

    const { confirm } = Modal;
    const showConfirm = () => {
        confirm({
            title: 'Do you want to delete this exclusion?',
            icon: <ExclamationCircleFilled />,
            okButtonProps:{
                style: { background: '#ff4d4f', borderColor: '#ff4d4f' },
            },
            onOk() {
                handleDelete(merchantCategoryCode);
                console.log('OK');
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    };
  return (

    <div className="w-full rounded p-4 mb-4 bg-white font-lexend relative">
        <button className="absolute top-0 right-0 p-2" onClick={showConfirm}>
            <HiOutlineTrash size={25} className="mt-3 mr-2"/>
        </button>
      <div className="flex items-center mb-2">

        <h2 className="text-lg font-medium"> MCC: {merchantCategoryCode}</h2>
      </div>
        <p>{mccMerchantName}</p>
      <p>{mccDescription}</p>

    </div>
  );
};

export default MccExclusion;
