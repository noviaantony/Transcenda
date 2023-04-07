import React from "react";
import { Typography, Space, Table } from "antd";


const columns = [
  {
    title: "Card Program",
    dataIndex: "cardProgram",
    key: "cardProgram",
    render: (text) => <a>{text}</a>,
  },
  {
    title: "Base Earn Rate",
    dataIndex: "baseRate",
    key: "baseRate",
  },
  {
    title: "Applicable Categories",
    dataIndex: "applicableCategories",
    key: "applicableCategories",
  },

];
const data = [
  {
    cardProgram: "SCIS Shopping Card",
    baseRate: "1 point/SGD on spend, 4 points/SGD on all shopping spend",
    applicableCategories: "10 points/SGD on all online spend",
  },
  {
    cardProgram: "SCIS PremiumMiles Card",
    baseRate: "1.1 miles/SGD , 2.2 miles/SGD on all foreign card spend",
    applicableCategories: "3 miles/SGD on all hotels spend",
  },
  {
    cardProgram: "SCIS PremiumMiles Card",
    baseRate: "1.4 miles/SGD , 3 miles/SGD on all foreign card spend",
    applicableCategories: "3 miles/SGD on all hotels spend 6 miles/SGD on all foreign hotel spend",
  },

  {
    cardProgram: "SCIS Shopping Card",
    baseRate: "0.5% cashback on all spend, 1.0% cashback for all spend > 500 SGD,3% cashback for all spend > 2000 SGD",
    applicableCategories: "-"
  },
];


const CardPrograms = () => {

  return (
    <>
      <Space direction="vertical table" className="font-lexend">
        <div className="font-nunito flex content-start">
          <Typography.Title
            // level={4}
            className="font-lexend font-bold font-6xl mt-5 mr-4"
          >
            Card Programs
          </Typography.Title>
        </div>
        <Table columns={columns} dataSource={data} className = "font-lexend"    
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
            }}/>
      </Space>
    </>
  );
};

export default CardPrograms;
