import React from "react";
import { Menu } from "antd";
import {useNavigate } from "react-router-dom";
import {AiFillCreditCard, AiFillDollarCircle} from "react-icons/ai";


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
        getItem("Card Programs", "2", <AiFillCreditCard />)
    ],
    "group"
  ),
];

const Sidebar = () => {
  const navigate = useNavigate();
  const onClick = (e) => {
    console.log("click ", e);
    if (e.key === "1") {
      navigate("/Transactions");
    } else if (e.key === "2") {
      navigate("/CardPrograms");
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

export default Sidebar;
