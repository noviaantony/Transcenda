import React, {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import {AiFillPlusCircle} from "react-icons/ai";
import MerchantCategoryCodeExclusionService from "../services/MerchantCategoryCodeExclusionService";
import mccExclusion from "../components/exclusions/MccExclusion";
import MccExclusion from "../components/exclusions/MccExclusion";

const Exclusions = () => {
    const [mccExclusions, setMccExclusions] = useState([]);

    // retrieve exclusions from backend
    useEffect(() => {
        MerchantCategoryCodeExclusionService.getAllMerchantCategoryCodeExclusions().then((response) => {
            setMccExclusions(response);
        });
    }, []);

    useEffect(() => {
        console.log(mccExclusions);
    }, [mccExclusions]);


    const [isAddingExclusion, setIsAddingExclusion] = useState(false);
    const [newExclusion, setNewExclusion] = useState({
        merchantCategoryCode: "",
        mccDescription: "",
        mccMerchantName: ""
    });

    const handleInputChange = (event) => {
        const {name, value} = event.target;
        setNewExclusion((prevExclusion) => ({...prevExclusion, [name]: value}));
    };

    const handleAddExclusion = () => {
        setMccExclusions((prevExclusion) => [
            ...prevExclusion,
            {...newExclusion, id: mccExclusion.length + 1},
        ]);
        setNewExclusion({merchantCategoryCode: "", mccDescription: "", mccMerchantName: ""});
        setIsAddingExclusion(false);

        MerchantCategoryCodeExclusionService.addNewMerchantCategoryCodeExclusion(newExclusion).then((response) => {
            console.log("Exclusion added successfully");
        });
    };


    return (
        <>
            <Space direction="vertical table" className="font-lexend">
                <div className="font-nunito flex content-start">
                    <Typography.Title
                        // level={4}
                        className="font-lexend font-bold font-6xl mt-5 mr-4"
                    >
                        Merchant Category Code Exclusion Management
                    </Typography.Title>
                </div>
            </Space>
            <div className="container mx-auto px-4 py-8 w-6/6 font-lexend">
                <div className="flex justify-between items-center mb-4">
                    <h1 className="text-4xl font-semibold">Current Exclusions</h1>
                    <button
                        className="flex items-center font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 text-white bg-[#66347F]  hover:bg-[#D1C2D8] hover:text-[#66347F] focus:outline-none ml-96"
                        onClick={() => setIsAddingExclusion(true)}
                    >
                        Add Exclusion
                        <AiFillPlusCircle className="w-6 h-6 ml-1"/>
                    </button>
                </div>
                {mccExclusions && mccExclusions.map((mccExclusion) => (
                    <MccExclusion key={mccExclusion.id} mccExclusion={mccExclusion}/>
                ))}
                {isAddingExclusion && (
                    <div className="mt-4">
                        <h2 className="text-lg font-medium mb-2">Add New Exclusion</h2>
                        <form onSubmit={(event) => event.preventDefault()}>
                            <div className="mb-4">
                                <label
                                    htmlFor="merchantCategoryCode"
                                    className="block text-gray-700 font-medium mb-2"
                                >
                                    Exclusion Merchant Category Code
                                </label>
                                <input
                                    type="number"
                                    id="merchantCategoryCode"
                                    name="merchantCategoryCode"
                                    value={newExclusion.merchantCategoryCode}
                                    onChange={handleInputChange}
                                    className="border border-gray-400 p-2 w-full"
                                />
                            </div>
                            <div className="mb-4">
                                <label
                                    htmlFor="mccDescription"
                                    className="block text-gray-700 font-medium mb-2"
                                >
                                    Exclusion Merchant Category Code Description
                                </label>
                                <input
                                    type="text"
                                    id="mccDescription"
                                    name="mccDescription"
                                    value={newExclusion.mccDescription}
                                    onChange={handleInputChange}
                                    className="border border-gray-400 p-2 w-full"
                                />
                            </div>
                            <div className="mb-4">
                                <label
                                    htmlFor="mccMerchantName"
                                    className="block text-gray-700 font-medium mb-2"
                                >
                                    Exclusion Merchant Name(s)
                                </label>
                                <input
                                    type="text"
                                    id="mccMerchantName"
                                    name="mccMerchantName"
                                    value={newExclusion.mccMerchantName}
                                    onChange={handleInputChange}
                                    className="border border-gray-400 p-2 w-full"
                                />
                            </div>
                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    className="rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 bg-white text-[#66347F]  hover:text-white hover:bg-[#66347F] focus:outline-none"
                                    onClick={() => setIsAddingExclusion(false)}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="button"
                                    className="rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 text-white bg-[#66347F]  hover:bg-[#D1C2D8] hover:text-[#66347F] focus:outline-none"
                                    onClick={handleAddExclusion}
                                >
                                    Add Exclusion
                                </button>
                            </div>
                        </form>
                    </div>
                )}
            </div>
        </>
    );
};

export default Exclusions;
