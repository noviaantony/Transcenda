import { Image, Space } from "antd";
import { Link } from "react-router-dom";
import "../../App.css";
import {Auth} from "aws-amplify";

function Navbar() {
    async function handleLogout() {
        try {
            await Auth.signOut();
            // Redirect to the login page or any other page you want
            window.location.href = "/Login";
        } catch (error) {
            console.log('Error signing out: ', error);
        }
    }

    return (
    <div className="AppHeader mx-5 grid grid-cols-2">
      <div>
        <Image
          width={150}
          src={require("./TranscendaLogo.png")}
          alt="TranscendaLogo"
          className="mt-5"
        ></Image>
      </div>
      <Space className="ml-96">
        <button
          type="button"
          class="text-white bg-[#66347F] hover:bg-white hover:text-[#66347F] hover:bg-white font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 ml-96 font-lexend font-bold"
          onClick={handleLogout}
        >
          Log Out
        </button>
      </Space>
    </div>
  );
}
export default Navbar;
