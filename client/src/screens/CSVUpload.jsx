import React, { useState } from "react";
import { Typography, Space, message, Upload, Spin } from "antd";
import { InboxOutlined } from "@ant-design/icons";
import {Storage} from 'aws-amplify';
import {withAuthenticator} from "@aws-amplify/ui-react";

const { Dragger } = Upload;

const CSVUpload = () => {
  
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileInput = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const uploadFile = async (file) => {
    if (!file) return;

    try {
      const result = await Storage.put(file.name, file, {
        contentType: "text/csv",
      });
      console.log("File uploaded:", result);
      message.success(`${file.name} file uploaded to S3 successfully.`);
    } catch (err) {
      console.error(err);
      message.error(`${file.name} file upload to S3 failed.`);
    }
  };

  const props = {
    name: "file",
    multiple: false,
    showUploadList: true,
    beforeUpload: (file) => {
      setSelectedFile(file);
      return false;
    },
    onDrop(e) {
      console.log("Dropped files", e.dataTransfer.files);
    },
    onChange(info) {
      const { status } = info.file;
      if (status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        message.success(`${info.file.name} file uploaded successfully.`);
      } else if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
  };
  return (
      <>
        <Space direction="vertical table" className="font-lexend">
          <div className="font-nunito flex content-start">
            <Typography.Title
                className="font-lexend font-bold font-6xl mt-5 mr-4"
            >
              File Upload
            </Typography.Title>
          </div>
          <Dragger {...props}>
            <p className="ant-upload-drag-icon w-full">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">
              Click or drag file to this area to upload
            </p>
            <p className="ant-upload-hint">
              Upload a single CSV file with transactions.
            </p>
          </Dragger>

          <br></br>

          <button
              type="button"
              className="text-white bg-[#66347F] hover:bg-[#D1C2D8] hover:text-[#66347F] font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2"
              onClick={() => uploadFile(selectedFile)}
          >
            Upload File
          </button>
        </Space>
      </>
  );
};

export default withAuthenticator(CSVUpload);
// export default CSVUpload;

