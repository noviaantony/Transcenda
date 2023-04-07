import React from "react";
import { Typography, Space} from "antd";
import {Link} from "react-router-dom"
import { ReactComponent as PageNotFoundSvg } from './PageNotFound.svg';


const PageNotFound = () => {

  return (
    <>
    <Space direction="vertical table" className="font-lexend">
      <div className="font-nunito flex content-start">
            <Typography.Title
                className="font-lexend font-bold font-6xl mt-5 mr-4"
            >
              Oh No! Page Not Found
            </Typography.Title>
          </div>
          <PageNotFoundSvg classname="" width="35rem" />
  
          <Link
              type="button"
              className="rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 text-white bg-[#66347F]  hover:bg-[#D1C2D8] hover:text-[#66347F] focus:outline-none"
              to = "/"
      
          >
            Go Back Home
          </Link>
    </Space>
    </>
  );
};

export default PageNotFound;
