import { Menu, Image } from "antd";
import { useEffect, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { MdCampaign } from "react-icons/md";
import {AiFillFileAdd, AiFillStar, AiFillCreditCard, AiFillDollarCircle, AiFillPlusCircle} from "react-icons/ai";


function getItem(label, key, icon, children, type) {
  return {
    key,
    icon,
    children,
    label,
    type,
  };
}
const items = [
  getItem(
    "",
    "grp",
    null,
    [
      getItem("Transactions", "1" ,<AiFillDollarCircle /> ),
      getItem("File Upload", "2", <AiFillFileAdd />),
      getItem("Transaction Entry", "6", <AiFillPlusCircle />),
      getItem("Launch Campaign", "3", <MdCampaign />),
      getItem("Exclusion Management", "4", <AiFillStar />),
      getItem("Card Programs", "5", <AiFillCreditCard />)
    ],
    "group"
  ),
];

const SidebarBank = () => {
  
  const navigate = useNavigate();
  const onClick = (e) => {
    console.log("click ", e);
    if (e.key === "1") {
      navigate("/BankTransactions");
    } else if (e.key === "2") {
        navigate("/CSVUpload");
    } else if (e.key === "3") {
      navigate("/Campaigns");
    } else if (e.key === "4") {
      navigate("/Exclusions");
    } else if (e.key === "5") {
      navigate("/CardPrograms");
    } else if (e.key === "6") {
      navigate("/TransactionEntry");
    } 
  };

  return (
    <div className="SideMenu font-lexend">
      <Menu
        className="SideMenuVertical"
        mode="vertical"
        onClick={onClick}
        style={{
          width: 256,
          height: 2000,
        }}
        defaultSelectedKeys={["1"]}
        defaultOpenKeys={["sub1"]}
        items={items}
      />
    </div>
  );
};

export default SidebarBank;
